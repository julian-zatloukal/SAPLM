
#include "Command_Handler.h"
#include "nrf24.h"
#include "crc.h"



const CommandType commandList[] = {
	{ .handlerFunction = &UPDATE_ALL_DEVICES_VALUE_H},         
	{ .handlerFunction = &UPDATE_DEVICE_VALUE_H},         
	{ .handlerFunction = &GET_ALL_DEVICES_VALUE_H},             
	{ .handlerFunction = &GET_DEVICE_VALUE_H},
	{ .handlerFunction = &MESSAGE_STATUS_H}
};
#define commandListLength (uint8_t)(sizeof commandList/sizeof commandList[0])

bool initliazeMemory(){
	if(memoryInitialized) return false;
	parameter[0].startingPointer = (void*)calloc(23,1);
	parameter[1].startingPointer = (void*)calloc(2,1);
	parameter[2].startingPointer = (void*)calloc(2,1);
	for (uint8_t x = 3; x<12; x++) parameter[x].startingPointer = (void*)calloc(1,1);
	command_buffer = (uint8_t*)calloc(32,1);
	if(command_buffer==NULL) return false;
	for (uint8_t x = 0; x<12; x++) { if(parameter[x].startingPointer==NULL) return false; }
	memoryInitialized = true;
	return true;
}

CommandStatus DecomposeMessageFromBuffer(){
	// Search for header
	uint8_t* headerStart = command_buffer;
	uint8_t* footerEnd = command_buffer+31;

	for(;headerStart!=(command_buffer+22);headerStart++){
		if (*headerStart==SOH&&(*(headerStart+4)==STX)){
			for(;footerEnd!=(command_buffer+6);footerEnd--){
				if (*footerEnd==ETB&&(*(footerEnd-2)==ETX)){
					uint8_t netMessageLength = ((footerEnd-2)-headerStart);
					crc_t crc;
					crc = crc_init();
					crc = crc_update(crc, headerStart, netMessageLength);
					crc = crc_finalize(crc);
					if (*(footerEnd-1)!=crc) return WRONG_CHECKSUM_CONSISTENCY;
					if (*(headerStart+2)!=currentModuleID&&*(headerStart+2)!=0xFF&&currentModuleID!=0x01) return WRONG_MODULE_ID;
					lastTargetModuleID = *(headerStart+2);
					lastTransmitterModuleID = *(headerStart+3);
					if (*(headerStart+5)>commandListLength-1) return UNDEFINED_COMMAND_CODE;
					lastMessageCommandType = commandList[*(headerStart+5)];
					lastMessagePID = *(headerStart+1);

					uint8_t* parameterStart = headerStart+6;

					for (uint8_t x = 0; x < 12; x++) {
						realloc(parameter[x].startingPointer, *parameterStart);
						parameter[x].byteLength = *parameterStart;
						memcpy(parameter[x].startingPointer,parameterStart+1, *parameterStart);
						parameterStart+=((*parameterStart)+1);
						if (parameterStart>=(footerEnd-2)) break;
					}

					return SUCCESFUL_DECOMPOSITION;
				}
			}
		}
	}
	return WRONG_HEADER_SEGMENTATION;
}

CommandStatus ComposeMessageToBuffer(CommandTypeID targetTypeID, uint8_t parameterCount, uint8_t targetBoardID){
	memset(command_buffer, 0, 32);
	command_buffer[0] = SOH;
	if (lastMessagePID==0xFF) { lastMessagePID++; } else { lastMessagePID = 0; }
	command_buffer[1] = lastMessagePID;
	command_buffer[2] = targetBoardID;
	command_buffer[3] = currentModuleID;
	command_buffer[4] = STX;
	command_buffer[5] = targetTypeID;

	if (parameterCount>12) return PARAMETER_COUNT_OVERSIZE;

	uint8_t* parameterStart = &command_buffer[6];

	for (uint8_t x = 0; x < parameterCount; x++){
		*parameterStart = parameter[x].byteLength;
		memcpy(parameterStart+1, parameter[x].startingPointer, parameter[x].byteLength);
		parameterStart+=(parameter[x].byteLength)+1;
	}

	crc_t crc;
	crc = crc_init();
	uint8_t crc_length = ((parameterStart)-(&command_buffer[0]));
	crc = crc_update(crc, &command_buffer[0], crc_length);
	crc = crc_finalize(crc);

	*parameterStart = ETX;
	*(parameterStart+1) = crc;
	*(parameterStart+2) = ETB;
	
	return SUCCESFUL_COMPOSITION;
}

void HandleAvailableCommand(){
	lastMessageCommandType.handlerFunction();
}

RF_TransmissionStatus RetransmissionToModule(){
	nrf24_initRF_SAFE(lastTargetModuleID, TRANSMIT);	// CONNECTION TO MODULE: GENERAL RF CHANNEL 112
	nrf24_send(command_buffer);
	while(nrf24_isSending());

	uint8_t messageStatus = nrf24_lastMessageStatus();
	if(messageStatus == NRF24_TRANSMISSON_OK) { return RF_SUCCESFUL_TRANSMISSION; }
	else if(messageStatus == NRF24_MESSAGE_LOST) { return RF_UNREACHEABLE_MODULE;}
	return RF_UNREACHEABLE_MODULE;
}

void writeParameterValue(uint8_t parameterIndex, void* parameterData, uint8_t parameterByteLength){
	parameter[parameterIndex].startingPointer = (uint8_t*) realloc(parameter[parameterIndex].startingPointer, parameterByteLength);
	memcpy(parameter[parameterIndex].startingPointer, parameterData, parameterByteLength);
	parameter[parameterIndex].byteLength = parameterByteLength;
}

void UPDATE_ALL_DEVICES_VALUE_H() {
	for (uint8_t x = 0; x < AVAILABLE_DEVICES;x++)
	{
		deviceStoredValue[x] = *((uint8_t*)parameter[x].startingPointer);
		switch (x) {
			case 0x00:
				bit_write(deviceStoredValue[x], PORTD, BIT(3));
				break;
			case 0x01:
				bit_write(deviceStoredValue[x], PORTD, BIT(2));
				break;
			case 0x02:
				bit_write(deviceStoredValue[x], PORTD, BIT(6));
				break;
			case 0x03:
				bit_write(deviceStoredValue[x], PORTD, BIT(5));
				break;
		}
	}
	

	
}

void UPDATE_DEVICE_VALUE_H() {
	uint8_t deviceIndex = *((uint8_t*)parameter[0].startingPointer); 
	uint8_t deviceValue = *((uint8_t*)parameter[1].startingPointer); 
	
	switch (deviceIndex) {
		case 0:
			bit_write(deviceValue, PORTD, BIT(3));
			break;
		case 1:
			bit_write(deviceValue, PORTD, BIT(2));
			break;
		case 2:
			bit_write(deviceValue, PORTD, BIT(6));
			break;
		case 3:
			bit_write(deviceValue, PORTD, BIT(5));
			break;
	}
	
	deviceStoredValue[deviceIndex] = deviceValue;
	
}
void GET_ALL_DEVICES_VALUE_H() {
	_delay_ms(50);
	
	for (uint8_t x = 0; x < AVAILABLE_DEVICES;x++)
	{
		writeParameterValue(x, &deviceStoredValue[x], 2);
	}

	ComposeMessageToBuffer(UPDATE_ALL_DEVICES_VALUE_ID, AVAILABLE_DEVICES, PHONE_MODULE);  // PHONE_MODULE deberia ser lastTransmitterModuleID
	
	nrf24_initRF_SAFE(MAIN_BOARD, TRANSMIT);
	nrf24_send(command_buffer);
	while(nrf24_isSending());
	uint8_t messageStatus = nrf24_lastMessageStatus();
}
void GET_DEVICE_VALUE_H() {
	_delay_ms(50);
	uint8_t deviceIndex = *((uint8_t*)parameter[0].startingPointer);
	writeParameterValue(0, &deviceIndex, 1);
	writeParameterValue(1, &deviceStoredValue[deviceIndex], 2);
	ComposeMessageToBuffer(UPDATE_DEVICE_VALUE_ID, 2, PHONE_MODULE);  // PHONE_MODULE deberia ser lastTransmitterModuleID
	
	nrf24_initRF_SAFE(MAIN_BOARD, TRANSMIT);
	nrf24_send(command_buffer);
	while(nrf24_isSending());
	uint8_t messageStatus = nrf24_lastMessageStatus();
}


void MESSAGE_STATUS_H() {}
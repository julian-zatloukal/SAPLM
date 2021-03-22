
#include "Command_Handler.h"
#include "UART_Bluetooth.h"
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
		if (*headerStart==SOH&&(*(headerStart+3)==STX)){
			for(;footerEnd!=(command_buffer+6);footerEnd--){
				if (*footerEnd==ETB&&(*(footerEnd-2)==ETX)){
					uint8_t netMessageLength = ((footerEnd-2)-headerStart);
					crc_t crc;
					crc = crc_init();
					crc = crc_update(crc, headerStart, netMessageLength);
					crc = crc_finalize(crc);
					if (*(footerEnd-1)!=crc) return WRONG_CHECKSUM_CONSISTENCY;
					if (*(headerStart+2)!=currentModuleID&&*(headerStart+2)!=0xFF&&currentModuleID!=0x00) return WRONG_MODULE_ID;
					lastTargetModuleID = *(headerStart+2);
					if (*(headerStart+4)>commandListLength-1) return UNDEFINED_COMMAND_CODE;
					lastMessageCommandType = commandList[*(headerStart+4)];
					lastMessagePID = *(headerStart+1);

					uint8_t* parameterStart = headerStart+5;

					for (uint8_t x = 0; x < 12; x++) {
						realloc(parameter[x].startingPointer, *parameterStart);
						parameter[x].byteLength = *parameterStart;
						memcpy(parameter[x].startingPointer,parameterStart+1, *parameterStart);
						parameterStart+=((*parameterStart)+1);
						if (parameterStart<=(footerEnd-2)) break;
					}

					return SUCCESFUL_DECOMPOSITION;
				}
			}
		}
	}
	return WRONG_HEADER_SEGMENTATION;
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

CommandStatus ComposeMessageToBuffer(CommandTypeID targetTypeID, uint8_t parameterCount, uint8_t targetBoardID){
	command_buffer[0] = SOH;
	if (lastMessagePID==0xFF) { lastMessagePID++; } else { lastMessagePID = 0; }
	command_buffer[1] = lastMessagePID;
	command_buffer[2] = targetBoardID;
	command_buffer[3] = STX;
	command_buffer[4] = targetTypeID;

	if (parameterCount>12) return PARAMETER_COUNT_OVERSIZE;

	uint8_t* parameterStart = &command_buffer[5];

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

void writeParameterValue(uint8_t parameterIndex, uint8_t* parameterData, uint8_t parameterByteLength){
	parameter[parameterIndex].startingPointer = (uint8_t*) realloc(parameter[parameterIndex].startingPointer, parameterByteLength);
	memcpy(parameter[parameterIndex].startingPointer, parameterData, parameterByteLength);
	parameter[parameterIndex].byteLength = parameterByteLength;
}

void UPDATE_ALL_DEVICES_VALUE_H() {}
void UPDATE_DEVICE_VALUE_H() {}
void GET_ALL_DEVICES_VALUE_H() {}
void GET_DEVICE_VALUE_H() {}
void MESSAGE_STATUS_H() {}
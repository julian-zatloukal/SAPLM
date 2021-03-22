
#include "Command_Handler.h"
#include "UART_Bluetooth.h"
#include "nrf24.h"


const commandType availableCommand[AVAILABLE_COMMANDS] = {
	{ .commandBase = "ROTATE_FORWARDS", .nParameters = 2, .handlerFunction = &ROTATE_FORWARDS_HANDLE},
	{ .commandBase = "ROTATE_BACKWARDS", .nParameters = 2, .handlerFunction = &ROTATE_BACKWARDS_HANDLE},
	{ .commandBase = "TURN_LED_ON", .nParameters = 1, .handlerFunction = &TURN_LED_ON_HANDLE},
	{ .commandBase = "TURN_LED_OFF", .nParameters = 1, .handlerFunction = &TURN_LED_OFF_HANDLE},
	{ .commandBase = "TURN_RELAY_ON", .nParameters = 1, .handlerFunction = &TURN_RELAY_ON_HANDLE},
	{ .commandBase = "TURN_RELAY_OFF", .nParameters = 1, .handlerFunction = &TURN_RELAY_OFF_HANDLE},
	{ .commandBase = "UART_TEST", .nParameters = 0, .handlerFunction = &UART_TEST_HANDLER},
	{ .commandBase = "BUILT_IN_LED_TEST", .nParameters = 0, .handlerFunction = &BUILT_IN_LED_TEST_HANDLER},
	{ .commandBase = "TURN_EVERYTHING_ON", .nParameters = 0, .handlerFunction = &TURN_EVERYTHING_ON_HANDLE},
	{ .commandBase = "TURN_EVERYTHING_OFF", .nParameters = 0, .handlerFunction = &TURN_EVERYTHING_OFF_HANDLE},
	{ .commandBase = "CALL_NURSE", .nParameters = 0, .handlerFunction = &CALL_NURSE_HANDLE},
	{ .commandBase = "CURTAIN_POS_CHANGE", .nParameters = 1, .handlerFunction = &CURTAIN_POS_CHANGE_HANDLE},
	{ .commandBase = "STRETCHER_POS_CHANGE", .nParameters = 1, .handlerFunction = &STRETCHER_POS_CHANGE_HANDLE}
};

bool initliazeMemory(){
	if(memoryInitialized) return false;
	parameter[0] = (void*)calloc(28,1);
	parameter[1] = (void*)calloc(28,1);
	parameter[2] = (void*)calloc(28,1);
	command_buffer = (uint8_t*)calloc(32,1);
	if(parameter[0]==nullptr||parameter[1]==nullptr||parameter[2]==nullptr||command_buffer==nullptr) return false;
	memoryInitialized = true;
	return true;
}


void composeCommand(void* output_buffer, commandType* commandT, void** inputParameter){
	strcpy(output_buffer, commandT->commandBase);
	char* startParamPTR = (char*)(output_buffer+strlen(commandT->commandBase));
	char* endParamPTR = (char*)(startParamPTR+1+strlen(*inputParameter));

	for (uint8_t index = 0; index < commandT->nParameters; index++){
		*startParamPTR='[';
		strcpy(startParamPTR+1, *inputParameter);
		*endParamPTR=']';
		startParamPTR=(endParamPTR+1);
		if (index!=(commandT->nParameters-1)){
			inputParameter++;
			uint8_t len = strlen(*inputParameter);
			endParamPTR = (char*)(startParamPTR+len+1);
		}
	}
	*startParamPTR='\0';
}

bool decomposeCommand(void* input_buffer, commandType* commandT, void** outputParameter){
	
	
	for (uint8_t index = 0; index < AVAILABLE_COMMANDS; index++){
		if (memmem(input_buffer, COMMAND_BUFFER_SIZE, availableCommand[index].commandBase, strlen(availableCommand[index].commandBase))!=nullptr) 
		{ 
			*commandT = availableCommand[index]; break; 
		}
		else if (index==(AVAILABLE_COMMANDS-1)) { return false;}
	}
	
	for (uint8_t x = 0; x < commandT->nParameters; x++){
		uint8_t* startNumPTR = memchr(input_buffer, '[', COMMAND_BUFFER_SIZE);
		uint8_t* endNumPTR = memchr(input_buffer, ']', COMMAND_BUFFER_SIZE);
		if (startNumPTR==nullptr||endNumPTR==nullptr) { if(x==0) return false; break; }
		(*startNumPTR) = 0x20;
		(*endNumPTR) = 0x20;
		startNumPTR++;
		uint32_t bytes = ((endNumPTR)) - ((startNumPTR));
		if (bytes>PARAMETER_BUFFER_SIZE) return false;
		memcpy(outputParameter[x], startNumPTR, bytes);
	}

	return true;
}
	
void ROTATE_FORWARDS_HANDLE() {}
	
void ROTATE_BACKWARDS_HANDLE() {}
	
void TURN_LED_ON_HANDLE() {}
	
void TURN_LED_OFF_HANDLE() {}
	
void CURTAIN_POS_CHANGE_HANDLE(){
	RETRANSMISSION_TO_BOARD(MOTORIZED_BOARD, &availableCommand[11]);
}

void STRETCHER_POS_CHANGE_HANDLE(){
	RETRANSMISSION_TO_BOARD(MOTORIZED_BOARD, &availableCommand[12]);
}

void CALL_NURSE_HANDLE(){
	RETRANSMISSION_TO_BOARD(MOTORIZED_BOARD, &availableCommand[10]);
}
	
void TURN_RELAY_ON_HANDLE() {
	RETRANSMISSION_TO_BOARD(POWER_BOARD, &availableCommand[4]);
}

void TURN_RELAY_OFF_HANDLE() {
	RETRANSMISSION_TO_BOARD(POWER_BOARD, &availableCommand[5]);
}

void UART_TEST_HANDLER() {
	transmitMessageSync("Successful UART transmission!\n", 30);
}

void BUILT_IN_LED_TEST_HANDLER(){
	for (uint8_t x = 0; x < 8; x++) {
		bit_flip(PORTD, BIT(7));
		bit_flip(PORTB, BIT(0));
		_delay_ms(250);
	}
	bit_clear(PORTD, BIT(7));
	bit_clear(PORTB, BIT(0));
}

void TURN_EVERYTHING_ON_HANDLE(){
	RETRANSMISSION_TO_BOARD(POWER_BOARD, &availableCommand[8]);
}

void TURN_EVERYTHING_OFF_HANDLE(){
	RETRANSMISSION_TO_BOARD(POWER_BOARD, &availableCommand[9]);
}

void RETRANSMISSION_TO_BOARD(uint8_t board, const commandType* targetType){
	// Retransmission using previous paramenters
	
	nrf24_initRF_SAFE(board, TRANSMIT);	// CONNECTION TO BOARD: GENERAL RF CHANNEL 112
	
	composeCommand(command_buffer, targetType, parameter);
	
	nrf24_send(command_buffer);
	while(nrf24_isSending());

	uint8_t messageStatus = nrf24_lastMessageStatus();
	if(messageStatus == NRF24_TRANSMISSON_OK) { transmitMessageSync("Successful RF transmission! \n", 29); }
	else if(messageStatus == NRF24_MESSAGE_LOST) { transmitMessageSync("Failure on RF transmission! \n", 29); }
	
	uint8_t retransmissionCount = nrf24_retransmissionCount();
	char* retransmissionString = malloc(40);
	sprintf(retransmissionString, "Retransmission count: %d \n", retransmissionCount);
	transmitMessageSync(retransmissionString, strlen(retransmissionString));
	sprintf(retransmissionString, "RX ADDRESS: %X :: TX ADDRESS: %X \n",selectedRX_ADDRESS, selectedTX_ADDRESS);
	transmitMessageSync(retransmissionString, strlen(retransmissionString));
	free(retransmissionString);
}


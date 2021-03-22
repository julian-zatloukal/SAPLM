
#include "Command_Handler.h"
#include "nrf24.h"
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>
#include <stdint.h>
#include <avr/io.h>
#include <util/delay.h>


const commandType availableCommand[AVAILABLE_COMMANDS] = {
	{ .commandBase = "TURN_RELAY_ON", .nParameters = 1, .handlerFunction = &TURN_RELAY_ON_HANDLE},
	{ .commandBase = "TURN_RELAY_OFF", .nParameters = 1, .handlerFunction = &TURN_RELAY_OFF_HANDLE},
	{ .commandBase = "BUILT_IN_LED_TEST", .nParameters = 0, .handlerFunction = &BUILT_IN_LED_TEST_HANDLER},
	{ .commandBase = "TURN_EVERYTHING_ON", .nParameters = 0, .handlerFunction = &TURN_EVERYTHING_ON_HANDLER},
	{ .commandBase = "TURN_EVERYTHING_OFF", .nParameters = 0, .handlerFunction = &TURN_EVERYTHING_OFF_HANDLER},
	{ .commandBase = "CALL_NURSE", .nParameters = 0, .handlerFunction = &CALL_NURSE_HANDLE}
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

void TURN_RELAY_ON_HANDLE() {
	uint8_t relayIndex =  atoi(parameter[0]);
	switch (relayIndex) {
		case 0:
		bit_set(PORTD, BIT(3));
		break;
		case 1:
		bit_set(PORTD, BIT(2));
		break;
		case 2:
		bit_set(PORTD, BIT(6));
		break;
		case 3:
		bit_set(PORTD, BIT(5));
		break;
	}
}

void TURN_RELAY_OFF_HANDLE() {
	uint8_t relayIndex =  atoi(parameter[0]);
	switch (relayIndex) {
		case 0:
		bit_clear(PORTD, BIT(3));
		break;
		case 1:
		bit_clear(PORTD, BIT(2));
		break;
		case 2:
		bit_clear(PORTD, BIT(6));
		break;
		case 3:
		bit_clear(PORTD, BIT(5));
		break;
	}
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

void TURN_EVERYTHING_ON_HANDLER(){
	bit_set(PORTD, BIT(3));
	bit_set(PORTD, BIT(2));
	bit_set(PORTD, BIT(6));
	bit_set(PORTD, BIT(5));
}

void TURN_EVERYTHING_OFF_HANDLER(){
	bit_clear(PORTD, BIT(3));
	bit_clear(PORTD, BIT(2));
	bit_clear(PORTD, BIT(6));
	bit_clear(PORTD, BIT(5));
}

void CALL_NURSE_HANDLE(){
	bit_set(PORTD, BIT(5));
	_delay_ms(500);
	bit_clear(PORTD, BIT(5));
	_delay_ms(500);
	bit_set(PORTD, BIT(5));
	_delay_ms(500);
	bit_clear(PORTD, BIT(5));
	_delay_ms(500);
	bit_set(PORTD, BIT(5));
	_delay_ms(500);
	bit_clear(PORTD, BIT(5));
}

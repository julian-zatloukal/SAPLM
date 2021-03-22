
#include "Command_Handler.h"



const commandType availableCommand[AVAILABLE_COMMANDS] = {
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
	
uint8_t currentCurtainPosition = 0;
uint8_t currentStretcherPosition = 0;

#define MOTOR_SPEED_MS 1
	
void CURTAIN_POS_CHANGE_HANDLE(){
	bit_set(PORTB, BIT(1));
	bit_set(PORTB, BIT(2));
	
	uint8_t positionToMove =  atoi(parameter[0]);
	if (positionToMove<8) {
		uint16_t degreesToMove = abs(positionToMove-currentCurtainPosition)*175;
	
		if((positionToMove-currentCurtainPosition)>0){
			for (uint16_t x = 0; x < degreesToMove;x++){
				PORTD = 0b00000011;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b00000110;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b00001100;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b00001001;
				_delay_ms(MOTOR_SPEED_MS);
			}
		}else{
			for (uint16_t x = 0; x < degreesToMove;x++){
				PORTD = 0b00001100;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b00000110;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b00000011;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b00001001;
				_delay_ms(MOTOR_SPEED_MS);
			}
		}
	
		PORTD = 0b00000000;
		currentCurtainPosition = positionToMove;
	}
	bit_clear(PORTB, BIT(1));
	bit_clear(PORTB, BIT(2));
}

void STRETCHER_POS_CHANGE_HANDLE(){
	bit_set(PORTB, BIT(1));
	bit_set(PORTB, BIT(2));
	
	uint8_t positionToMove =  atoi(parameter[0]);
	if (positionToMove<6) {
		uint16_t degreesToMove = abs(positionToMove-currentStretcherPosition)*50;
	
		if((positionToMove-currentCurtainPosition)>0){
			for (uint16_t x = 0; x < degreesToMove;x++){
				PORTD = 0b00110000;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b01100000;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b11000000;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b10010000;
				_delay_ms(MOTOR_SPEED_MS);
			}
			}else{
			for (uint16_t x = 0; x < degreesToMove;x++){
				PORTD = 0b11000000;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b01100000;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b00110000;
				_delay_ms(MOTOR_SPEED_MS);
				PORTD = 0b10010000;
				_delay_ms(MOTOR_SPEED_MS);
			}
		}
	
		PORTD = 0b00000000;
		currentStretcherPosition = positionToMove;
	}
	bit_clear(PORTB, BIT(1));
	bit_clear(PORTB, BIT(2));
}

void CALL_NURSE_HANDLE(){
	bit_clear(PORTB, BIT(1));
	bit_clear(PORTB, BIT(2));
	
	for (uint8_t x = 0; x<8; x++)
	{
		bit_flip(PORTB, BIT(1));
		_delay_ms(125);
		bit_flip(PORTB, BIT(2));
		_delay_ms(125);
	}
	
}




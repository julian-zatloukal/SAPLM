#ifndef F_CPU
#define F_CPU 16000000UL
#endif
#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <stdint.h>

#include "nrf24.h"

void initIO();

int main(void)
{
	sei();	// Interrupts on
    initIO();
	nrf24_initRF_SAFE(MAIN_BOARD, RECEIVE);	// CONNECTION TO MAIN BOARD : GENERAL RF CHANNEL 112
	
    while (1) 
    {
		if(nrf24_dataReady())
		{
			bit_set(PORTB, BIT(0));
			nrf24_getData(command_buffer);
			commandType currentCommand;
			bool success = decomposeCommand(command_buffer, &currentCommand, parameter);
			if (success) { currentCommand.handlerFunction(); }
			bit_clear(PORTB, BIT(0));
		}
		
		if (nrf24_checkAvailability()==false) { nrf24_initRF_SAFE(MAIN_BOARD, RECEIVE); }
    }
}


void initIO(){
	/*	
		Input/Output pin initialization
		1 : OUTPUT | 0 : INPUT | 0b76543210 Bit order
		ATTACHMENTS
			RELAY 0		: PD3				|	OUTPUT
			RELAY 1		: PD2				|	OUTPUT
			RELAY 2		: PD6				|	OUTPUT
			RELAY 3		: PD5				|	OUTPUT
			RED LED		: PD7				|	OUTPUT
			GREEN LED	: PB0				|	OUTPUT
		nRF24L01
			CE	 : PC0						|	OUTPUT
			CSN	 : PC1						|	OUTPUT
			MISO : PD0 (MSPIM MISO ATMEGA)	|	INPUT
			MOSI : PD1 (MSPIM MOSI ATMEGA)	|	OUTPUT
			SCK	 : PD4 (MSPIM XCK)			|	OUTPUT
	*/ 
	DDRD = 0b11111110;
	DDRB = 0b00101001;
	DDRC = 0b11011111;
}






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
    initIO();
	nrf24_initRF_SAFE(MAIN_BOARD, RECEIVE);	// CONNECTION TO MAIN BOARD : GENERAL RF CHANNEL 112
	
	while (1)
	{
		if(nrf24_dataReady())
		{
			
			nrf24_getData(command_buffer);
			CommandStatus status = DecomposeMessageFromBuffer();
			if (status==SUCCESFUL_DECOMPOSITION) { HandleAvailableCommand(); }
		}
		
		if (nrf24_checkAvailability()==false) { nrf24_initRF_SAFE(MAIN_BOARD, RECEIVE); }
	}
}


void initIO(){
	/*	
		Input/Output pin initialization
		1 : OUTPUT | 0 : INPUT | 0b76543210 Bit order
		ATTACHMENTS
			NURSE SIGN	: PB0				|	OUTPUT
			GREEN LED	: PB1				|	OUTPUT		(SWAPPED IN PCB)
			RED LED		: PB2				|	OUTPUT
		STEP MOTOR A (CURTAIN)
			TERMINAL NO.1 : PD0				|	OUTPUT
			TERMINAL NO.2 : PD1				|	OUTPUT
			TERMINAL NO.3 : PD2				|	OUTPUT
			TERMINAL NO.4 : PD3				|	OUTPUT
		STEP MOTOR B (STRETCHER)
			TERMINAL NO.1 : PD4				|	OUTPUT
			TERMINAL NO.2 : PD5				|	OUTPUT
			TERMINAL NO.3 : PD6				|	OUTPUT
			TERMINAL NO.4 : PD7				|	OUTPUT
		nRF24L01
			CE	 : PC0						|	OUTPUT
			CSN	 : PC1						|	OUTPUT
			MISO : PD0 (MSPIM MISO ATMEGA)	|	INPUT
			MOSI : PD1 (MSPIM MOSI ATMEGA)	|	OUTPUT
			SCK	 : PD4 (MSPIM XCK)			|	OUTPUT
	*/ 
	DDRD = 0b11111111;
	DDRB = 0b00101111;
	DDRC = 0b11011111;
}






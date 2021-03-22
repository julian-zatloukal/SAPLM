#define F_CPU					16000000UL

#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <stdint.h>

#include "UART_Bluetooth.h"
#include "nrf24.h"

void initIO();
char messageTest[] = "UART TESTING COMMANDS! \n";
	
int main(void)
{
	sei();	// Interrupts on
    initBluetoothUart();
    initIO();
	nrf24_initRF_SAFE(0, RECEIVE);	// CONNECTION TO MAIN BOARD : GENERAL RF CHANNEL 112
	setupReceiveMode();
    while (1) 
    {
		while(!commandAvailable);
		bit_clear(PORTB, BIT(0));
		processReceivedLine();
		setupReceiveMode();
    }
}


void initIO(){
	/*	
		Input/Output pin initialization
		1 : OUTPUT | 0 : INPUT | 0b76543210 Bit order
		ATTACHMENTS
			RED LED		: PD7				|	OUTPUT
			GREEN LED	: PB0				|	OUTPUT
		HC-05
			TX			: PD0 (RX ATMEGA)	|	INPUT
			RX			: PD1 (TX ATMEGA)	|	OUTPUT
			KEY/ENABLE	: PD2				|	OUTPUT
			STATE		: PC5				|	INPUT
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
	bit_clear(PORTD, BIT(2));
}






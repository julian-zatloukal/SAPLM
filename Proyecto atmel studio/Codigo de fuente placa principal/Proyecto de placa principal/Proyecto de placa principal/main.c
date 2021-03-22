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
	cli();	// Interrupts off
	initIO();
    initBluetoothUart();
	setupReceiveMode();
	nrf24_initRF_SAFE(POWER_BOARD_RF, RECEIVE);	// CONNECTION TO POWER BOARD AND MOTORIZED BOARD : GENERAL RF CHANNEL 11
	sei();	// Interrupts on
    while (1) 
    {
		if (commandAvailable) {
			cli();
			processReceivedLine();
			setupReceiveMode();
			
		}
		
		 // Disable UART
		
		if(nrf24_dataReady())
		{
			cli();
			nrf24_getData(command_buffer);
			CommandStatus status = DecomposeMessageFromBuffer();
			if (status==SUCCESFUL_DECOMPOSITION) { RetransmissionToPhone();  }
			sei();
		}
		
		if (nrf24_checkAvailability()==false) { nrf24_initRF_SAFE(POWER_BOARD_RF, RECEIVE); }

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
}






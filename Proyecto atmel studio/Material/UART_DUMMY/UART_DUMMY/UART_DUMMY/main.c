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

uint8_t dummyMessage[] = {SOH, 0x7f, 0x00, STX, 0x01, 0x02, 0x72, 0x72, 0x03, 0x72, 0x72, 0x72, ETX, 0xEA, ETB};
//uint8_t dummyMessage[] = {0x7F, 0x7F, 0x7F, 0x7F};
#define dummyMessageLength (uint8_t)(sizeof dummyMessage/sizeof dummyMessage[0])

int main(void)
{
	cli();	// Interrupts off
	initIO();
    initBluetoothUart();
	setupReceiveMode();
	sei();	// Interrupts on
    while (1) 
    {
		transmitMessageSync(dummyMessage, dummyMessageLength);
		_delay_ms(3000);
    }
}


void initIO(){
	/*	
		Input/Output pin initialization
		1 : OUTPUT | 0 : INPUT | 0b76543210 Bit order
		ATTACHMENTS
			RED LED		: PD7				|	OUTPUT
			GREEN LED	: PD6				|	OUTPUT
			BLUE LED	: PD5				|	OUTPUT
		HC-05
			TX			: PD0 (RX ATMEGA)	|	INPUT
			RX			: PD1 (TX ATMEGA)	|	OUTPUT
	*/ 
	DDRD = 0b11111110;
	DDRB = 0b00101001;
	DDRC = 0b11011111;
}

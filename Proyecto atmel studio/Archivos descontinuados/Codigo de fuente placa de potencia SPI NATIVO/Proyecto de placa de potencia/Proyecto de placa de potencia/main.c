
#define F_CPU 16000000UL

#define bit_get(p,m) ((p) & (m))
#define bit_set(p,m) ((p) |= (m))
#define bit_clear(p,m) ((p) &= ~(m))
#define bit_flip(p,m) ((p) ^= (m))
#define bit_write(c,p,m) (c ? bit_set(p,m) : bit_clear(p,m))
#define BIT(x) (0x01 << (x))
#define LONGBIT(x) ((unsigned long)0x00000001 << (x))

#include "nrf24.h"
#include "Command_Handler.h"

#include <avr/io.h>
#include <string.h>
#include <stdlib.h>
#include <util/delay.h>

void initRF();
void initIO();

int main(void)
{
	initIO();
	initRF();
	
	while (1)
	{
		if(nrf24_dataReady())
		{
			nrf24_getData(command_buffer);
			
			commandType currentCommand;
			bool success = decomposeCommand(command_buffer, &currentCommand, parameter);
			if (success) { currentCommand.handlerFunction(); }
		}
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
			GREEN LED	: PD7				|	OUTPUT
			RED LED		: PB0				|	OUTPUT
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

void initRF(){
	uint8_t tx_address[5] = {0xD7,0xD7,0xD7,0xD7,0xD7};
	uint8_t rx_address[5] = {0xE7,0xE7,0xE7,0xE7,0xE7};

	initliazeMemory();	
	nrf24_init();
		
	/* Channel #64 , payload length: 32 */
	nrf24_config(64,32);

	/* Set the device addresses */
	nrf24_tx_address(tx_address);
	nrf24_rx_address(rx_address);
}






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

bool initRF();
void initIO();
void faultyRF_Alarm();

int main(void)
{
	initIO();
	initRF();
	
	while (1)
	{
		if(nrf24_dataReady())
		{
			bit_clear(PORTB, BIT(0));

			nrf24_getData(command_buffer);
			
			bit_set(PORTD, BIT(7));
			_delay_ms(500);
			commandType currentCommand;
			bool success = decomposeCommand(command_buffer, &currentCommand, parameter);
			if (success) { currentCommand.handlerFunction(); }
			bit_clear(PORTD, BIT(7));
		}
		
		if (nrf24_checkAvailability()==false) { while(initRF()==false); } 
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
	
	PORTD = 0b00000000;
	PORTC = 0b00000000;
	PORTB = 0b00000000;
}

bool initRF(){
	uint8_t tx_address[5] = {0xD7,0xD7,0xD7,0xD7,0xD7};
	uint8_t rx_address[5] = {0xE7,0xE7,0xE7,0xE7,0xE7};

	initliazeMemory();	
	
	/* Power down module */
	nrf24_powerDown();
	
	nrf24_init();
		
	/* Channel #112 , payload length: 32 */
	nrf24_config(112,32);

	/* Check module configuration */
	if (nrf24_checkConfig()==false) { faultyRF_Alarm(); return false; }
		
	/* Set the device addresses */
	nrf24_tx_address(tx_address);
	nrf24_rx_address(rx_address);
	
	/* Power up in receive mode */
	nrf24_powerUpRx();

	return true;
}

void faultyRF_Alarm(){
	bit_clear(PORTD, BIT(7));
	for (uint8_t x = 0; x < 6; x++)
	{
		bit_flip(PORTD, BIT(7));
		_delay_ms(125);
	}
}




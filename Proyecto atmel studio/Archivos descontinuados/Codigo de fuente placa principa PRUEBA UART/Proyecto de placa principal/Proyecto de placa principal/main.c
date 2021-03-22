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
void initRF();
char messageTest[] = "UART TESTING COMMANDS! \n";
	
int main(void)
{
	sei();	// Interrupts on
    initBluetoothUart();
    initIO();
	
	for (uint8_t x = 0; x < 100; x++){
		transmitMessageSync(messageTest, strlen(messageTest));
		if (bit_get(PINC, BIT(5))) { bit_set(PORTD, BIT(7)); } else { bit_clear(PORTD, BIT(7)); }
		_delay_ms(1000);
	}
	
	//initRF();
	setupReceiveMode();
	
	
	
    while (1) 
    {
		while(!commandAvailable);
		processReceivedLine();
		setupReceiveMode();
    }
}


void initIO(){
	/*	
		Input/Output pin initialization
		1 : OUTPUT | 0 : INPUT | 0b76543210 Bit order
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

void initRF(){
	uint8_t tx_address[5] = {0xE7,0xE7,0xE7,0xE7,0xE7};
	uint8_t rx_address[5] = {0xD7,0xD7,0xD7,0xD7,0xD7};
	
	nrf24_init();
	
	/* Channel #64 , payload length: 32 */
	nrf24_config(64,32);

	/* Set the device addresses */
	nrf24_tx_address(tx_address);
	nrf24_rx_address(rx_address);
}





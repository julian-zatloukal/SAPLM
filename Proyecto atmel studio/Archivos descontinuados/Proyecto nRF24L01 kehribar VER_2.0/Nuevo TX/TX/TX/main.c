
// Design half-duplex UART utility functions

#define uartBufferSize          32
#define uartEndMsgChar          '$'
#define F_CPU					8000000UL
#define BUAD					9600
#define BRC						(((F_CPU / (BUAD * 8UL))) - 1)
#define bit_get(p,m) ((p) & (m))
#define bit_set(p,m) ((p) |= (m))
#define bit_clear(p,m) ((p) &= ~(m))
#define bit_flip(p,m) ((p) ^= (m))
#define bit_write(c,p,m) (c ? bit_set(p,m) : bit_clear(p,m))
#define BIT(x) (0x01 << (x))
#define LONGBIT(x) ((unsigned long)0x00000001 << (x))

#include <avr/io.h>
#include <util/delay.h>
#include <stdint.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "..\nrf24.c"

uint8_t* uartBuffer;
uint8_t* uartBufferPos;
uint8_t* uartTxMessageEnd;
uint8_t messageArray[2][13] = {{"TURN_LED_ON"},{"TURN_LED_OFF"}};
uint8_t messageReply[2][19] = {{"Turning led on! \n"},{"Turning led off! \n"}};
bool commandAvailable = false;

uint8_t temp;
uint8_t q = 0;
uint8_t data_array[4];
uint8_t tx_address[5] = {0xE7,0xE7,0xE7,0xE7,0xE7};
uint8_t rx_address[5] = {0xD7,0xD7,0xD7,0xD7,0xD7};
	
void initUart();
void initIO();
void transmitMessage(uint8_t* message, uint8_t length);
bool transmissionState();
void setupReceiveMode();
void processReceivedLine();
void disableUART();
/* ------------------------------------------------------------------------- */
int main()
{
	sei();
	initUart();
	initIO();
	setupReceiveMode();
	
	// 0x5D PRENDER LED; 0x7F APAGAR LED
	data_array[0] = 0x5D; data_array[1] = 0x5D; data_array[2] = 0x5D; data_array[3] = 0x5D; 
	nrf24_init();
	nrf24_config(2,4);	// Channel, payload
	nrf24_tx_address(tx_address);
	nrf24_rx_address(rx_address);
	
	for (uint8_t x = 0; x < 4; x++)
	{
		PORTD^=0xFF;
		_delay_ms(250);
	}

	while(1)
	{
		if (commandAvailable==true) { commandAvailable = false; processReceivedLine();  }
	}
}

void initUart(){
	// UART Initialization : 8-bit : No parity bit : 1 stop bit
	UBRR0H = (BRC >> 8); UBRR0L =  BRC;             // UART BAUDRATE
	UCSR0A |= (1 << U2X0);                          // DOUBLE UART SPEED
	UCSR0C |= (1 << UCSZ01) | (1 << UCSZ00);        // 8-BIT CHARACTER SIZE
	
	// Setup UART buffer
	uartBuffer = (uint8_t*)malloc(uartBufferSize);
	uartBufferPos = uartBuffer;
}

void initIO(){
	// Input/Output pin initialization
	// 1 : OUTPUT | 0 : INPUT | 0b76543210 Bit order
	DDRD = 0b11000010;
}


void transmitMessage(uint8_t* message, uint8_t length){
	uartBufferPos = uartBuffer;
	uartTxMessageEnd = (uartBuffer+length);
	memcpy(uartBuffer, message, length);
	UCSR0B |= (1<<TXEN0) | (1<<TXCIE0);
	UCSR0B &=~(1<<RXEN0) &~(1<<RXCIE0);
	
	uartBufferPos++;
	UDR0 = *(uartBufferPos-1);
}

bool transmissionState(){
	// True : Currently transmitting | False : Transmission finished
	if (uartBufferPos!=uartTxMessageEnd) {return true;}
	else { return false; }
}


void setupReceiveMode(){
	uartBufferPos = uartBuffer;
	UCSR0B &=~(1<<TXEN0) &~(1<<TXCIE0);
	UCSR0B |= (1<<RXEN0) | (1<<RXCIE0);
}

void processReceivedLine(){
	
	if(memcmp(&messageArray[0][0], uartBuffer, 11)==0) {
		transmitMessage(&messageReply[0][0], 17);
		while(transmissionState()) {}
		bit_set(PORTD, BIT(7));

		data_array[0] = 0x7F; data_array[1] = 0x7F; data_array[2] = 0x7F; data_array[3] = 0x7F; 
		nrf24_send(data_array);
		while(nrf24_isSending());
		temp = nrf24_lastMessageStatus();
		if(temp == NRF24_TRANSMISSON_OK)
		{
			for (uint8_t x = 0; x < 4; x++)
			{
				PORTD^=0xFF;
				_delay_ms(250);
			}
		}
		else if(temp == NRF24_MESSAGE_LOST)
		{
			
		}	
	}
	
	if(memcmp(&messageArray[1][0], uartBuffer, 12)==0) {
		transmitMessage(&messageReply[1][0], 18);
		while(transmissionState()) {}
		bit_clear(PORTD, BIT(7));
		
		data_array[0] = 0x5D; data_array[1] = 0x5D; data_array[2] = 0x5D; data_array[3] = 0x5D;
		nrf24_send(data_array);
		while(nrf24_isSending());
		temp = nrf24_lastMessageStatus();
		if(temp == NRF24_TRANSMISSON_OK)
		{
			for (uint8_t x = 0; x < 4; x++)
			{
				PORTD^=0xFF;
				_delay_ms(250);
			}
		}
		else if(temp == NRF24_MESSAGE_LOST)
		{
			
		}
	}
	setupReceiveMode();
	uartBufferPos = uartBuffer;
}


void disableUART(){
	UCSR0B &=~(1<<TXEN0) &~(1<<TXCIE0);
	UCSR0B &=~(1<<RXEN0) &~(1<<RXCIE0);
}


ISR(USART_TX_vect){
	if (uartBufferPos!=uartTxMessageEnd){
		UDR0 = *uartBufferPos;
		uartBufferPos++;
	}
}

ISR(USART_RX_vect){
	if(uartBufferPos!=(uartBuffer+uartBufferSize)) {
		*uartBufferPos=UDR0;
		if (*uartBufferPos!=uartEndMsgChar) { uartBufferPos++; }
		else { disableUART(); commandAvailable = true; }
		} else {uartBufferPos = uartBuffer;}
	}


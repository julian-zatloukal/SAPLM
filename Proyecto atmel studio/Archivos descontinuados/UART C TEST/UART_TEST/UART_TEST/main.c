#pragma region Define section
#define F_CPU					8000000UL
#define BUAD					9600
#define BRC						(((F_CPU / (BUAD * 8UL))) - 1)
#define CLK_8Prescaler			(double)(8.0d/8000UL)
#define TX_BUFFER_MAX_INDEX		127
#define RX_BUFFER_MAX_INDEX		127
#define bit_get(p,m) ((p) & (m))
#define bit_set(p,m) ((p) |= (m))
#define bit_clear(p,m) ((p) &= ~(m))
#define bit_flip(p,m) ((p) ^= (m))
#define bit_write(c,p,m) (c ? bit_set(p,m) : bit_clear(p,m))
#define BIT(x) (0x01 << (x))
#define LONGBIT(x) ((unsigned long)0x00000001 << (x))
#pragma endregion Defines

#pragma region Include
#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <util/delay.h>
#include <string.h>
#include <math.h>
#pragma endregion Include

uint8_t readBuffer(uint8_t* command, const uint8_t commandLen);
void transmitArray(uint8_t* command, const uint8_t commandLen);

uint8_t rxWritePos			=	0;
uint8_t rxPrevReadedPos		=	0;
uint8_t txReadPos			=	0;
uint8_t txWritePos			=	0;
uint8_t txCarriageReturn    =	0;
uint8_t *transmitIndexPTR	=	0;
uint8_t *reciveIndexPTR		=	0;
uint8_t rxBuffer[RX_BUFFER_MAX_INDEX+1] = {0};
uint8_t txBuffer[TX_BUFFER_MAX_INDEX+1] = {0};
uint8_t messageArray[4][26] = {
	{"/VOLTAGE=00 //"},
	{"/CURRENT=0.000 /"},
	{"/POWER_FACTOR=1.000  IND/"},
	{"/FREQUENCY=00 /"}};
	uint8_t commandArray[2][10] = {
		{"RELAY_OFF$"},
		{"RELAY_ON$"}
	};


	int main(void){
		sei();
		// UART
		UBRR0H = (BRC >> 8); UBRR0L =  BRC;
		UCSR0A = 0b00000010;
		UCSR0C |= (1 << UCSZ01) | (1 << UCSZ00);
		UCSR0B |= (1 << TXEN0)  | (1 << TXCIE0);
		UCSR0B &=~(1<<RXEN0) &~(1<<RXCIE0);
		// PORT
		DDRD = 0b11110010;
		DDRC = 0b00110000;
		PORTD = 0b00000000;
		PORTC = 0b00110000;
		while (1)
		{
		
		}
	}

	void StartCommunication(){
		UCSR0B |= (1 << TXEN0)  | (1 << TXCIE0);	// Enable UART TX; Disable UART RX
		UCSR0B &=~(1<<RXEN0) &~(1<<RXCIE0);
		transmitArray(&messageArray[0][0], 14);		// VOLTAGE MEASUREMENT
		transmitArray(&controlCharacter[1], 1);
		transmitArray(&messageArray[1][0], 16);		// CURRENT MEASUREMENT
		transmitArray(&controlCharacter[1], 1);
		transmitArray(&messageArray[2][0], 25);		// POWER FACTOR MEASUREMENT
		transmitArray(&controlCharacter[1], 1);
		transmitArray(&messageArray[3][0], 15);		// FREQUENCY MEASUREMENT
		transmitArray(&controlCharacter[0], 1);
		_delay_ms(10);
		UCSR0B |= (1 << RXEN0)  | (1 << RXCIE0);	// Enable UART RX; Disable UART TX
		UCSR0B &=~(1<<TXEN0) &~(1<<TXCIE0);
		_delay_ms(250);
		UCSR0B &=~(1<<RXEN0) &~(1<<RXCIE0);			// Disable UART RX
	}
	


uint8_t readBuffer(uint8_t* command, const uint8_t commandLen) {
	if (rxWritePos == rxPrevReadedPos) { return 0; } else { rxPrevReadedPos = rxWritePos;}
	if ((rxWritePos - commandLen) >= 0) { reciveIndexPTR = &rxBuffer[rxWritePos - commandLen]; }
	else { reciveIndexPTR = &rxBuffer[RX_BUFFER_MAX_INDEX - (commandLen-rxWritePos-1)]; }
	for (uint8_t i = 0; i < commandLen; i++) {
		if((rxWritePos+i) > RX_BUFFER_MAX_INDEX) { reciveIndexPTR = (&rxBuffer[0])-i; }
		if (*(reciveIndexPTR + i) != *(command + i)) return 0;
	}
	return 1;
}

void transmitArray(uint8_t* command, const uint8_t commandLen) {
	if ((txWritePos+commandLen) > TX_BUFFER_MAX_INDEX) { txCarriageReturn = txWritePos; txWritePos = 0;}
	transmitIndexPTR = &txBuffer[txWritePos];
	for (uint8_t i = 0; i < commandLen; i++){
		*(transmitIndexPTR+i) = *(command+i);
	}
	txWritePos += commandLen;
	if(UCSR0A & (1 << UDRE0)) { UDR0 = txBuffer[txReadPos++];}
	while(!((txReadPos)==txWritePos)){}
}

ISR(USART_TX_vect){
	if ((txReadPos == txCarriageReturn) || (txReadPos > TX_BUFFER_MAX_INDEX)) { txReadPos=0; txCarriageReturn = TX_BUFFER_MAX_INDEX;}
	if (txReadPos != txWritePos) { UDR0 = txBuffer[txReadPos++]; }
}

ISR(USART_RX_vect){
	if(rxWritePos+1 > RX_BUFFER_MAX_INDEX) { rxWritePos = 0; } else { rxWritePos++; }
	rxBuffer[rxWritePos] = UDR0;
	if (rxBuffer[rxWritePos]=='$') {
		if (memcmp(&commandArray[0][0], &rxBuffer[rxWritePos-9], 10)==0) {bit_clear(PORTC, BIT(5));} else
		{ if (memcmp(&commandArray[1][0], &rxBuffer[rxWritePos-8], 9)==0) {bit_set(PORTC, BIT(5));}}
	}
}


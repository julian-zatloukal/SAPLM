
#define F_CPU 16000000UL

#define bit_get(p,m) ((p) & (m))
#define bit_set(p,m) ((p) |= (m))
#define bit_clear(p,m) ((p) &= ~(m))
#define bit_flip(p,m) ((p) ^= (m))
#define bit_write(c,p,m) (c ? bit_set(p,m) : bit_clear(p,m))
#define BIT(x) (0x01 << (x))
#define LONGBIT(x) ((unsigned long)0x00000001 << (x))
#include <avr/io.h>
#include <util/delay.h>

void initIO();

int main(void)
{
	initIO();

    while (1) 
    {
		_delay_ms(250);
		bit_flip(PORTD, BIT(7));
		_delay_ms(250);
		bit_flip(PORTB, BIT(0));
		_delay_ms(250);
		bit_flip(PORTD, BIT(3));
		_delay_ms(250);
		bit_flip(PORTD, BIT(2));
		_delay_ms(250);
		bit_flip(PORTD, BIT(6));
		_delay_ms(250);
		bit_flip(PORTD, BIT(5));
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

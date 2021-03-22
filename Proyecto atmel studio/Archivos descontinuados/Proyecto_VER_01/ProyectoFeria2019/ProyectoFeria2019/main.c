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

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include "bluetoothConnectivity.h"

int main(void)
{
	sei();
	// UART
	UBRR0H = (BRC >> 8); UBRR0L =  BRC;
	UCSR0A = 0b00000010;
	UCSR0C |= (1 << UCSZ01) | (1 << UCSZ00);
	UCSR0B |= (1 << TXEN0)  | (1 << TXCIE0);
	UCSR0B &=~(1<<RXEN0) &~(1<<RXCIE0);
	
    while (1) 
    {
		
    }
}


/*
 * Endianess.c
 *
 * Created: 22/10/2019 10:08:23 PM
 * Author : Julian
 */ 

#include <avr/io.h>

static uint32_t endianness = 0xdeadbeef;
enum endianness { BIG, LITTLE };

#define ENDIANNESS ( *(const char *)&endianness == 0xef ? LITTLE : *(const char *)&endianness == 0xde ? BIG : 0)

int main(void)
{
    /* Replace with your application code */
	
	DDRD = 0xFF;
	
	if (ENDIANNESS==BIG) PORTD = 0b00000001;
	//if (ENDIANNESS==LITTLE) PORTD = 0b10000000;
	
    while (1) 
    {
    }
}



#define F_CPU					16000000UL

#ifndef BIT_MANIPULATION_MACRO
#define BIT_MANIPULATION_MACRO
#define bit_get(p,m) ((p) & (m))
#define bit_set(p,m) ((p) |= (m))
#define bit_clear(p,m) ((p) &= ~(m))
#define bit_flip(p,m) ((p) ^= (m))
#define bit_write(c,p,m) (c ? bit_set(p,m) : bit_clear(p,m))
#define BIT(x) (0x01 << (x))
#define LONGBIT(x) ((unsigned long)0x00000001 << (x))
#endif

#include <avr/io.h>
#include <avr/eeprom.h>
#include <util/delay.h>

uint8_t counter = 0;

int main(void)
{
    DDRD = 0xFF;
	DDRC = 0x00;
	PORTC = 0b00000011;
	
	counter = eeprom_read_byte((uint8_t*)20);
	PORTD = counter;
	
    while (1) 
    {
		if (!bit_get(PINC,BIT(0))){
			_delay_ms(50);
			while (!bit_get(PINC,BIT(0)));
			if (counter!=9) PORTD = ++counter;
			eeprom_update_byte ((uint8_t*)20,counter);
		}
		
		if (!bit_get(PINC,BIT(1))){
			_delay_ms(50);
			while (!bit_get(PINC,BIT(1)));
			eeprom_update_byte ((uint8_t*)20,counter);
			counter = 0; 
			PORTD = 0;
		}
    }
}


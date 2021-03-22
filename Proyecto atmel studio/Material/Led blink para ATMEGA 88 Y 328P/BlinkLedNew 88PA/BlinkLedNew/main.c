
#define F_CPU	8000000UL
#define bit_get(p,m) ((p) & (m))
#define bit_set(p,m) ((p) |= (m))
#define bit_clear(p,m) ((p) &= ~(m))
#define bit_flip(p,m) ((p) ^= (m))
#define bit_write(c,p,m) (c ? bit_set(p,m) : bit_clear(p,m))
#define BIT(x) (0x01 << (x))
#define LONGBIT(x) ((unsigned long)0x00000001 << (x))


#include <avr/io.h>
#include <avr/delay.h>

int main(void)
{
	DDRC = 0xFF;
    while (1) 
    {
		bit_flip(PORTC, BIT(5));
		_delay_ms(250);
    }
}



#define F_CPU 8000000
#define bit_get(p,m) ((p) & (m))
#define bit_set(p,m) ((p) |= (m))
#define bit_clear(p,m) ((p) &= ~(m))
#define bit_flip(p,m) ((p) ^= (m))
#define bit_write(c,p,m) (c ? bit_set(p,m) : bit_clear(p,m))
#define BIT(x) (0x01 << (x))
#define LONGBIT(x) ((unsigned long)0x00000001 << (x))

#include <avr/io.h>
#include <avr/delay.h>
#include <stdint.h>
#include "..\nrf24.c"


uint8_t temp;
uint8_t q = 0;
uint8_t data_array[4];
uint8_t tx_address[5] = {0xD7,0xD7,0xD7,0xD7,0xD7};
uint8_t rx_address[5] = {0xE7,0xE7,0xE7,0xE7,0xE7};
/* ------------------------------------------------------------------------- */
int main()
{
	nrf24_init();
	nrf24_config(2,4);	// Channel, payload
	nrf24_tx_address(tx_address);
	nrf24_rx_address(rx_address);

	DDRD = 0xFF;
	PORTD = 0x00;
	
	for (uint8_t x = 0; x < 4; x++)
	{
		PORTD^=0xFF;
		_delay_ms(250);
	}

	while(1)
	{
		if(nrf24_dataReady())
		{
			nrf24_getData(data_array);
			if (data_array[0]==0x5D||data_array[1]==0x5D||data_array[2]==0x5D||data_array[3]==0x5D) { PORTD = 0x00; }
			if (data_array[0]==0x7F||data_array[1]==0x7F||data_array[2]==0x7F||data_array[3]==0x7F) { PORTD = 0xFF; }
				
		}
	}
}


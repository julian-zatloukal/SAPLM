
#define F_CPU					16000000UL
#define MOTOR_SPEED_MS			250

#ifndef BIT_MANIPULATION_MACRO
#define BIT_MANIPULATION_MACRO 1
#define bit_get(p,m) ((p) & (m))
#define bit_set(p,m) ((p) |= (m))
#define bit_clear(p,m) ((p) &= ~(m))
#define bit_flip(p,m) ((p) ^= (m))
#define bit_write(c,p,m) (c ? bit_set(p,m) : bit_clear(p,m))
#define BIT(x) (0x01 << (x))
#define LONGBIT(x) ((unsigned long)0x00000001 << (x))
#endif

#include <avr/io.h>
#include <util/delay.h>
#include <stdbool.h>
#include <stdint.h>

#include <avr/io.h>

void initIO();
void moveMotorA();
void moveMotorB();

int main(void)
{
    initIO();
	
    while (1) 
    {
		for (uint8_t x = 0; x < 6;x++){
			bit_flip(PORTB, BIT(0));
			_delay_ms(125);
			bit_flip(PORTB, BIT(1));
			_delay_ms(125);
			bit_flip(PORTB, BIT(2));
			_delay_ms(125);
		}
		
		moveMotorA();
		moveMotorB();
    }
}

void initIO(){
	/*	
		Input/Output pin initialization
		1 : OUTPUT | 0 : INPUT | 0b76543210 Bit order
		ATTACHMENTS
			NURSE SIGN	: PB0				|	OUTPUT
			RED LED		: PB1				|	OUTPUT
			GREEN LED	: PB2				|	OUTPUT
		STEP MOTOR A (CURTAIN)
			TERMINAL NO.1 : PD0				|	OUTPUT
			TERMINAL NO.2 : PD1				|	OUTPUT
			TERMINAL NO.3 : PD2				|	OUTPUT
			TERMINAL NO.4 : PD3				|	OUTPUT
		STEP MOTOR B (STRETCHER)
			TERMINAL NO.1 : PD4				|	OUTPUT
			TERMINAL NO.2 : PD5				|	OUTPUT
			TERMINAL NO.3 : PD6				|	OUTPUT
			TERMINAL NO.4 : PD7				|	OUTPUT
		nRF24L01
			CE	 : PC0						|	OUTPUT
			CSN	 : PC1						|	OUTPUT
			MISO : PD0 (MSPIM MISO ATMEGA)	|	INPUT
			MOSI : PD1 (MSPIM MOSI ATMEGA)	|	OUTPUT
			SCK	 : PD4 (MSPIM XCK)			|	OUTPUT
	*/ 
	DDRD = 0b11111111;
	DDRB = 0b00101111;
	DDRC = 0b11011111;
}

void moveMotorA(){
	for (uint8_t x = 0; x < 20;x++){
		PORTD = 0b00000011;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b00000110;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b00001100;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b00001001;
		_delay_ms(MOTOR_SPEED_MS);
	}
	
	for (uint8_t x = 0; x < 20;x++){
		PORTD = 0b00001100;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b00000110;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b00000011;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b00001001;
		_delay_ms(MOTOR_SPEED_MS);
	}
	
	PORTD = 0b00000000;
}

void moveMotorB(){
	for (uint8_t x = 0; x < 20;x++){
		PORTD = 0b00110000;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b01100000;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b11000000;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b10010000;
		_delay_ms(MOTOR_SPEED_MS);
	}
	
	for (uint8_t x = 0; x < 20;x++){
		PORTD = 0b11000000;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b01100000;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b00110000;
		_delay_ms(MOTOR_SPEED_MS);
		PORTD = 0b10010000;
		_delay_ms(MOTOR_SPEED_MS);
	}
	
	PORTD = 0b00000000;
}
#pragma region Define section
#define F_CPU					8000000UL
#define BUAD					9600
#define BRC						(((F_CPU / (BUAD * 8UL))) - 1)
#define STABILITY_TIME			(uint16_t)30
#define SAMPLING_BLOCK_SIZE		60
#define CLK_8Prescaler			(double)(8.0d/8000UL)
#define TX_BUFFER_MAX_INDEX		127
#define RX_BUFFER_MAX_INDEX		127
#define VREF_VOLTAGE			5.000d
#define ADC_STEP				(double)(VREF_VOLTAGE/1023)
#define LookupTableSize			20
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
void DetectStability();
void restartCapacitors();
double ADCtoCurrent(double CurrfloatADC);
void StartCommunication();
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

double currentLookupTable[2][LookupTableSize] = {
	{0.0d,      0.108,		0.117,		0.135d,       0.211d,     0.355d,     0.478d,     0.650d,     0.690d,     0.720d,     1.076d,
	1.255d,     1.630d,		1.800d,     1.900d,     2.3d,		2.5d,		2.7d,       2.9d,       3.2d},
	
	{0.230d,    0.260,		0.400,		0.518d,    0.616d,     0.788d,     0.816d,     0.830d,     0.840d,     0.850d,     0.860d,
	0.866d,     0.870d,		0.880d,     0.880d,     0.880d,		0.880d,		0.880d,     0.880d,     0.880d}
};
uint8_t controlCharacter[3] = {'$', '\n', 'K'};
double  calibrationConstant[5] = {1.000d, 1.000d, 1.000d, 1.000d, 63.50d};
uint8_t measurementsReady   =	0;
uint8_t detectStabilityMode =	0;
uint8_t measurementMode		=	0; // 0: Voltage, 1: Current
double  voltageAccumulator	=	0;
double  currentAccumulator	=	0;
uint16_t  powerFactorAccumulator	=	0;
uint16_t TIMEOUT = 0;


int main(void){
	sei();
	// UART
	UBRR0H = (BRC >> 8); UBRR0L =  BRC;
	UCSR0A = 0b00000010;
	UCSR0C |= (1 << UCSZ01) | (1 << UCSZ00);
	UCSR0B |= (1 << TXEN0)  | (1 << TXCIE0);
	UCSR0B &=~(1<<RXEN0) &~(1<<RXCIE0);
	// ADC
	MCUCR  |= (1<<PUD);
	ADMUX  |= (1<<REFS0);
	DIDR0  |= (1 << ADC0D) | (1 << ADC1D);
	ADCSRA |= (1<<ADEN) | (1<<ADPS2) | (1<<ADPS1) | (1<<ADPS0);
	// PORT
	DDRD = 0b11110010;
	DDRC = 0b00110000;
	PORTD = 0b00000000;
	PORTC = 0b00110000;
	while (1)
	{
		TIMEOUT = 0;
		TCNT1 = 0;
		TCNT0 = 0;
		powerFactorAccumulator = 0;
		voltageAccumulator = 0.0d;
		currentAccumulator = 0.0d;
		//DetectStability();
		//EICRA = 0b00001111;
		//EIMSK = 0b00000011;
		//while(powerFactorAccumulator==0){if (TIMEOUT<50){TIMEOUT++;_delay_ms(1);}else{powerFactorAccumulator=1;}}
		for (uint16_t i = 0; i<1200;i++)
		{
			ADMUX &=~(1<<MUX0);
			ADCSRA |= (1 << ADSC);
			while(bit_get(ADCSRA, BIT(ADSC))) {}
			voltageAccumulator += ((ADC*ADC_STEP));
		}
		for (uint16_t i = 0; i<1200;i++)
		{
			ADMUX |=(1<<MUX0);
			ADCSRA |= (1 << ADSC);
			while(bit_get(ADCSRA, BIT(ADSC))) {}
			currentAccumulator += ((ADC*ADC_STEP));
		}
		voltageAccumulator=(voltageAccumulator/1200.0d);
		currentAccumulator=(currentAccumulator/1200.0d);
		voltageAccumulator*=calibrationConstant[4];
		currentAccumulator = ADCtoCurrent(currentAccumulator);
		itoa(voltageAccumulator,&messageArray[0][9],10);
		dtostrf(currentAccumulator,5,3,&messageArray[1][9]);
		restartCapacitors();
		StartCommunication();
	}
}

ISR(INT0_vect){
	if(detectStabilityMode==1){
		if(bit_get(PIND, BIT(2))) {
			TCCR0B = 0b00000000;
			TCNT0 = 0;
			}else{
			TCCR0B = 0b00000101;
		}
		}else{
		if (TCNT1==0){
			TCCR1B |= (1<<CS11);
			}else{
			TCCR1B &=~ (1<<CS11);
			powerFactorAccumulator = TCNT1;
			TCNT1 = 0;
			EIMSK = 0b00000000;
		}
	}
}

ISR(INT1_vect){
	if(detectStabilityMode==1){
		if(bit_get(PIND, BIT(3))) {
			TCCR0B = 0b00000000;
			TCNT0 = 0;
			}else{
			TCCR0B = 0b00000101;
		}
		}else{
		if (TCNT1==0){
			TCCR1B |= (1<<CS11);
			}else{
			TCCR1B &=~ (1<<CS11);
			powerFactorAccumulator = TCNT1;
			TCNT1 = 0;
			EIMSK = 0b00000000;
		}
	}
}

ISR(TIMER0_COMPA_vect){
	if (detectStabilityMode==1){
		detectStabilityMode = 2;
		EIMSK = 0;
		TCCR0B = 0;
		TCNT0 = 0;
	}
}

void DetectStability(){
	TIFR0	=	0b11111111;
	OCR0A	=	STABILITY_TIME;
	detectStabilityMode = 1;
	EICRA	=	0b00000101;
	EIMSK	=	0b00000011;
	TIMSK0	=	0b00000010;
	while((detectStabilityMode == 1)) {} // WAITING STABILITY
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
	
void restartCapacitors(){
	PORTC = bit_clear(PORTC,BIT(0));
	PORTC = bit_clear(PORTC,BIT(1));
	//Descargar capacitor ADC0
	DDRC = 0b00110001;
	DIDR0  &=~ (1 << ADC0D);
	_delay_ms(50);
	DDRC = 0b00110000;
	DIDR0  |= (1 << ADC0D);
	_delay_ms(250);
	//Descargar capacitor ADC1
	DDRC = 0b00110010;
	DIDR0  &=~ (1 << ADC1D);
	_delay_ms(50);
	DDRC = 0b00110000;
	DIDR0  |= (1 << ADC1D);
}
	
double ADCtoCurrent(double CurrfloatADC) {
	uint8_t i;
	double *currentValueX = &currentLookupTable[0][0];
	double *currentValueY = &currentLookupTable[1][0];
	double *endValueX = &currentLookupTable[0][LookupTableSize-1];
	for(i = 0; i < (LookupTableSize);i++){
		if (currentValueX==endValueX) {return (*currentValueY)*CurrfloatADC;} else {
		if(CurrfloatADC<(*(currentValueX+1))) {break;}
		else {currentValueX++; currentValueY++;}
		}
	}
	//return *currentValueY;
	double M = ((*(currentValueY+1)-(*currentValueY))/(*(currentValueX+1)-(*(currentValueX))));
	double C = (( (((*(currentValueX+1))*(*currentValueY)) - ((*currentValueX)*(*(currentValueY+1)))))/((*(currentValueX+1))-(*currentValueX)) );
	return (M*CurrfloatADC+C)*CurrfloatADC;
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




#ifndef UART_BLUETOOTH_H_
#define UART_BLUETOOTH_H_


#include <stdbool.h>
#include <stdint.h>

#ifndef F_CPU
#define F_CPU			16000000UL
#endif

#ifndef BAUD
#define BAUD			9600
#endif

#ifndef BRC
#define BRC				F_CPU/8/BAUD-1
#endif

#ifndef nullptr
#define nullptr			nullptr ((void*)0)
#endif

#define uartBufferSize          32
#define uartEndMsgChar          '$'
#define uartCarriageReturnChar	0x7F

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


extern bool commandAvailable;

extern void initBluetoothUart();
extern void transmitMessage(uint8_t* message, uint8_t length);
extern void transmitMessageSync(uint8_t* message, uint8_t length);
extern bool transmissionState();
extern void setupReceiveMode();
extern void processReceivedLine();
extern void disableUART();



#endif /* UART_BLUETOOTH_H_ */


#include "UART_Bluetooth.h"
#include <avr/io.h>
#include <avr/interrupt.h>
#include "Command_Handler.h"
#include <stdlib.h>
#include <string.h>

uint8_t* uartBufferPos;
uint8_t* uartTxMessageEnd;
bool commandAvailable;

void initBluetoothUart(){
	// UART Initialization : 8-bit : No parity bit : 1 stop bit
	UBRR0H = (BRC >> 8); UBRR0L =  BRC;             // UART BAUDRATE
	UCSR0A |= (1 << U2X0);                          // DOUBLE UART SPEED
	UCSR0C |= (1 << UCSZ01) | (1 << UCSZ00);        // 8-BIT CHARACTER SIZE
	
	// Setup UART buffer
	initliazeMemory();
	uartBufferPos = command_buffer;
}

void transmitMessage(uint8_t* message, uint8_t length){
	while (!(UCSR0A & (1<<UDRE0)));
	uartBufferPos = command_buffer;
	uartTxMessageEnd = (command_buffer+length);
	memcpy(command_buffer, message, length);
	UCSR0A |= (1<<TXC0) | (1<<RXC0);
	UCSR0B |= (1<<TXEN0) | (1<<TXCIE0);
	UCSR0B &=~(1<<RXEN0) &~(1<<RXCIE0);
	
	uartBufferPos++;
	UDR0 = *(command_buffer);
}

void transmitMessageSync(uint8_t* message, uint8_t length){
	while (!(UCSR0A & (1<<UDRE0)));
	uartBufferPos = command_buffer;
	uartTxMessageEnd = (command_buffer+length);
	memcpy(command_buffer, message, length);
	UCSR0A |= (1<<TXC0) | (1<<RXC0);
	UCSR0B |= (1<<TXEN0) | (1<<TXCIE0);
	UCSR0B &=~(1<<RXEN0) &~(1<<RXCIE0);
	
	uartBufferPos++;
	UDR0 = *(command_buffer);

	while (transmissionState());

}

bool transmissionState(){
	// True : Currently transmitting | False : Transmission finished
	if (uartBufferPos!=uartTxMessageEnd) 
	{
		return true;
	}
	else 
	{ 
		return false; 
	}
}


void setupReceiveMode(){
	while (!(UCSR0A & (1<<UDRE0)));
	uartBufferPos = command_buffer;
	
	UCSR0A |= (1<<RXC0) | (1<<TXC0);
	UCSR0B &=~(1<<TXEN0) &~(1<<TXCIE0);
	UCSR0B |= (1<<RXEN0) | (1<<RXCIE0);
}

void processReceivedLine(){
	commandAvailable = false;

	bit_set(PORTD, BIT(7));
	CommandStatus status = DecomposeMessageFromBuffer();
	bit_clear(PORTD, BIT(7));
	if(status==SUCCESFUL_DECOMPOSITION) { 
		if (lastTargetModuleID==0x00){
			//Executed by main module
			HandleAvailableCommand();
		} else {
			//Retransmitted to other module
			
			RF_TransmissionStatus RF_Status = RetransmissionToModule();
			switch (RF_Status) {
				case RF_UNREACHEABLE_MODULE:
				writeParameterValue(0, &(uint8_t){RETRANSMISSION_FAILED}, 1);
				break;
				case RF_ACKNOWLEDGE_FAILED:
				writeParameterValue(0, &(uint8_t){RETRANSMISSION_FAILED}, 1);
				break;
				case RF_SUCCESFUL_TRANSMISSION:
				writeParameterValue(0, &(uint8_t){SUCCESFUL_RETRANSMISSION}, 1);
				break;
			}
			ComposeMessageToBuffer(MESSAGE_STATUS_ID, 1, 0xFF);
			transmitMessageSync(command_buffer, 32);
		}
	}
	
	
}

void disableUART(){
	UCSR0B &=~(1<<TXEN0) &~(1<<TXCIE0);
	UCSR0B &=~(1<<RXEN0) &~(1<<RXCIE0);
}

ISR(USART_TX_vect){
	if (uartBufferPos!=uartTxMessageEnd){
		UDR0 = *uartBufferPos;
		uartBufferPos++;
	}
}

ISR(USART_RX_vect){
	if(uartBufferPos!=(command_buffer+uartBufferSize)) {
		*uartBufferPos=UDR0;
		if ((*uartBufferPos==ETB)&&(DecomposeMessageFromBuffer()==SUCCESFUL_DECOMPOSITION)) {
			disableUART(); commandAvailable = true; 
		}
		else if(*uartBufferPos==uartCarriageReturnChar) {
			for (uint8_t x = 1; x < 4; x++) {
				if ((uartBufferPos-x)<command_buffer) uartBufferPos = command_buffer+(uartBufferSize-1);
				if (*(uartBufferPos-x)!=uartCarriageReturnChar) break; 
				if (x==3) { uartBufferPos = command_buffer; break; } else { uartBufferPos++; }
			} 
		} else {
			uartBufferPos++;
		} 
		
	} else {
		uartBufferPos = command_buffer;
		*uartBufferPos=UDR0;
	}
}
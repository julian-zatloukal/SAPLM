

#ifndef COMMAND_HANDLER_H_
#define COMMAND_HANDLER_H_

#ifndef nullptr
#define nullptr ((void *)0)
#endif

#ifndef F_CPU
#define F_CPU				16000000UL
#endif

#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <avr/io.h>
#include <util/delay.h>

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

typedef struct CommandType {
	void (*handlerFunction)();
} CommandType;

typedef enum   {
	SUCCESFUL_DECOMPOSITION,        
	WRONG_HEADER_SEGMENTATION,					
	WRONG_FOOTER_SEGMENTATION,					
	WRONG_CHECKSUM_CONSISTENCY,					
	WRONG_MODULE_ID,							
	UNDEFINED_COMMAND_CODE,						
	PARAMETER_DATA_OVERFLOW,					
	PARAMETER_COUNT_OVERSIZE,
	RETRANSMISSION_FAILED,
	SUCCESFUL_RETRANSMISSION,
	SUCCESFUL_COMPOSITION
} CommandStatus;


typedef enum {
	RF_SUCCESFUL_TRANSMISSION,
	RF_UNREACHEABLE_MODULE,
	RF_ACKNOWLEDGE_FAILED
} RF_TransmissionStatus;

typedef enum   {
	UPDATE_ALL_DEVICES_VALUE_ID,
	UPDATE_DEVICE_VALUE_ID,
	GET_ALL_DEVICES_VALUE_ID,
	GET_DEVICE_VALUE_ID,
	MESSAGE_STATUS_ID
} CommandTypeID;

typedef struct {
	void *startingPointer;
	uint8_t byteLength;
} Parameter;

#define currentModuleID 0x00

#define SOH 0x01
#define STX 0x02
#define ETX 0x03
#define ETB 0x17
#define ON_STATE    0xFF
#define OFF_STATE   0x00

#define AVAILABLE_DEVICES 4
uint16_t device_value[AVAILABLE_DEVICES];

uint8_t *command_buffer;
Parameter parameter[12];
bool memoryInitialized;

uint8_t lastMessagePID;
uint8_t lastTargetModuleID;
CommandType lastMessageCommandType;

extern bool initliazeMemory();
extern void UPDATE_ALL_DEVICES_VALUE_H(), UPDATE_DEVICE_VALUE_H(), GET_ALL_DEVICES_VALUE_H(), GET_DEVICE_VALUE_H(), MESSAGE_STATUS_H();
extern CommandStatus DecomposeMessageFromBuffer();
extern void HandleAvailableCommand();
extern RF_TransmissionStatus RetransmissionToModule();
extern CommandStatus ComposeMessageToBuffer(CommandTypeID targetTypeID, uint8_t parameterCount, uint8_t targetBoardID);
void writeParameterValue(uint8_t parameterIndex, uint8_t* parameterData, uint8_t parameterByteLength);

#endif /* COMMAND_HANDLER_H_ */
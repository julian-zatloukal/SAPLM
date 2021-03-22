

#ifndef COMMAND_HANDLER_H_
#define COMMAND_HANDLER_H_

#include <stdbool.h>
#include <stdint.h>

#ifndef nullptr
#define nullptr ((void *)0)
#endif

#ifndef F_CPU
#define F_CPU				16000000UL
#endif

#define AVAILABLE_COMMANDS 6
#define COMMAND_BUFFER_SIZE 32
#define PARAMETER_BUFFER_SIZE 28

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

typedef struct commandType {
	const char *commandBase;
	uint8_t nParameters;
	void (*handlerFunction)();
} commandType;

void *parameter[3];
uint8_t *command_buffer;
extern bool initliazeMemory();
bool memoryInitialized;
extern void TURN_RELAY_ON_HANDLE(), TURN_RELAY_OFF_HANDLE(), BUILT_IN_LED_TEST_HANDLER(), TURN_EVERYTHING_ON_HANDLER(), TURN_EVERYTHING_OFF_HANDLER(), CALL_NURSE_HANDLE();

extern void composeCommand(void* output_buffer, commandType* commandT, void** inputParameter);
extern bool decomposeCommand(void* input_buffer, commandType* commandT, void** outputParameter);


#endif /* COMMAND_HANDLER_H_ */
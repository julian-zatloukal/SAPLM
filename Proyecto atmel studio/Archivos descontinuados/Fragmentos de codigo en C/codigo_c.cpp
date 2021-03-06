#include <iostream>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <stddef.h>
#define AVAILABLE_COMMANDS 4

using namespace std;


const char *commandBase[AVAILABLE_COMMANDS][2] = {{"ROTATE_FORWARDS ", "S"}, {"ROTATE_BACKWARDS ", "S"}, 
    {"TURN_LED_ON ", "S"}, {"TURN_LED_OFF ", "S"}
};
enum commandBaseType {ROTATE_FORWARDS, ROTATE_BACKWARDS, TURN_LED_ON, TURN_LED_OFF, NONE};
static commandBaseType getCommandType(char *cmdBuffer);
static void handleCommand(commandBaseType command, void *parameter);
static void handleCommandDouble(commandBaseType command, void *f_parameter, void *s_parameter);

static void ROTATE_FORWARDS_HANDLE(), ROTATE_BACKWARDS_HANDLE(), TURN_LED_ON_HANDLE(), TURN_LED_OFF_HANDLE();

static void *parameter[3];

typedef struct commandType {
    const char *commandBase;
    uint8_t nParameters;
    void (*handlerFunction)();
} commandType;

commandType availableCommand[AVAILABLE_COMMANDS] = {
    { .commandBase = "ROTATE_FORWARDS ", .nParameters = 1, .handlerFunction = &ROTATE_FORWARDS_HANDLE},
    { .commandBase = "ROTATE_BACKWARDS ", .nParameters = 1, .handlerFunction = &ROTATE_BACKWARDS_HANDLE},
    { .commandBase = "TURN_LED_ON ", .nParameters = 1, .handlerFunction = &TURN_LED_ON_HANDLE},
    { .commandBase = "TURN_LED_OFF ", .nParameters = 1, .handlerFunction = &TURN_LED_OFF_HANDLE}
};


int main()
{
    void* commandBufferChar = (void*)("ROTATE_BACKWARDS [32423423][2424][vamosmauri]\n");

    
    int stringLength = 45;
    
	// Hay errores en la utilizacion de punteros de tipo void
	
    for (uint8_t x = 0; x < 3; x++){

    }
    
    return 0;
}

static commandBaseType getCommandType(char *cmdBuffer){
    for (uint8_t x = 0; x < AVAILABLE_COMMANDS; x++){
        if (strstr(cmdBuffer, &commandBase[x][0][0] )!=nullptr) return (commandBaseType)x;
    }
    return NONE;
}

static void handleCommand(commandBaseType command, void *parameter){
    
}

static void handleCommandDouble(commandBaseType command, void *f_parameter, void *s_parameter){
    
}

static void ROTATE_FORWARDS_HANDLE() { }

static void ROTATE_BACKWARDS_HANDLE() { }

static void TURN_LED_ON_HANDLE() { }

static void TURN_LED_OFF_HANDLE() { }





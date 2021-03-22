################################################################################
# Automatically-generated file. Do not edit!
################################################################################

SHELL := cmd.exe
RM := rm -rf

USER_OBJS :=

LIBS := 
PROJ := 

O_SRCS := 
C_SRCS := 
S_SRCS := 
S_UPPER_SRCS := 
OBJ_SRCS := 
ASM_SRCS := 
PREPROCESSING_SRCS := 
OBJS := 
OBJS_AS_ARGS := 
C_DEPS := 
C_DEPS_AS_ARGS := 
EXECUTABLES := 
OUTPUT_FILE_PATH :=
OUTPUT_FILE_PATH_AS_ARGS :=
AVR_APP_PATH :=$$$AVR_APP_PATH$$$
QUOTE := "
ADDITIONAL_DEPENDENCIES:=
OUTPUT_FILE_DEP:=
LIB_DEP:=
LINKER_SCRIPT_DEP:=

# Every subdirectory with source files must be described here
SUBDIRS := 


# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS +=  \
../Command_Handler.c \
../main.c \
../nrf24.c \
../UART_Bluetooth.c


PREPROCESSING_SRCS += 


ASM_SRCS += 


OBJS +=  \
Command_Handler.o \
main.o \
nrf24.o \
UART_Bluetooth.o

OBJS_AS_ARGS +=  \
Command_Handler.o \
main.o \
nrf24.o \
UART_Bluetooth.o

C_DEPS +=  \
Command_Handler.d \
main.d \
nrf24.d \
UART_Bluetooth.d

C_DEPS_AS_ARGS +=  \
Command_Handler.d \
main.d \
nrf24.d \
UART_Bluetooth.d

OUTPUT_FILE_PATH +=Proyecto\ de\ placa\ principal.elf

OUTPUT_FILE_PATH_AS_ARGS +="Proyecto de placa principal.elf"

ADDITIONAL_DEPENDENCIES:=

OUTPUT_FILE_DEP:= ./makedep.mk

LIB_DEP+= 

LINKER_SCRIPT_DEP+= 


# AVR32/GNU C Compiler
./Command_Handler.o: .././Command_Handler.c
	@echo Building file: $<
	@echo Invoking: AVR/GNU C Compiler : 4.5.1
	$(QUOTE)C:\Program Files (x86)\Atmel\AVR Tools\AVR Toolchain\bin\avr-gcc.exe$(QUOTE)  -x c -funsigned-char -funsigned-bitfields -DDEBUG  -I"C:\Program Files (x86)\Atmel\Studio\7.0\Packs\atmel\ATmega_DFP\1.2.209\include"  -O0 -ffunction-sections -fdata-sections -fpack-struct -fshort-enums -g2 -Wall -Wextra -pedantic  -mmcu=atmega328p  -c -std=gnu99 -MD -MP -MF "$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -MT"$(@:%.o=%.o)"   -o "$@" "$<" 
	@echo Finished building: $<
	

./main.o: .././main.c
	@echo Building file: $<
	@echo Invoking: AVR/GNU C Compiler : 4.5.1
	$(QUOTE)C:\Program Files (x86)\Atmel\AVR Tools\AVR Toolchain\bin\avr-gcc.exe$(QUOTE)  -x c -funsigned-char -funsigned-bitfields -DDEBUG  -I"C:\Program Files (x86)\Atmel\Studio\7.0\Packs\atmel\ATmega_DFP\1.2.209\include"  -O0 -ffunction-sections -fdata-sections -fpack-struct -fshort-enums -g2 -Wall -Wextra -pedantic  -mmcu=atmega328p  -c -std=gnu99 -MD -MP -MF "$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -MT"$(@:%.o=%.o)"   -o "$@" "$<" 
	@echo Finished building: $<
	

./nrf24.o: .././nrf24.c
	@echo Building file: $<
	@echo Invoking: AVR/GNU C Compiler : 4.5.1
	$(QUOTE)C:\Program Files (x86)\Atmel\AVR Tools\AVR Toolchain\bin\avr-gcc.exe$(QUOTE)  -x c -funsigned-char -funsigned-bitfields -DDEBUG  -I"C:\Program Files (x86)\Atmel\Studio\7.0\Packs\atmel\ATmega_DFP\1.2.209\include"  -O0 -ffunction-sections -fdata-sections -fpack-struct -fshort-enums -g2 -Wall -Wextra -pedantic  -mmcu=atmega328p  -c -std=gnu99 -MD -MP -MF "$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -MT"$(@:%.o=%.o)"   -o "$@" "$<" 
	@echo Finished building: $<
	

./UART_Bluetooth.o: .././UART_Bluetooth.c
	@echo Building file: $<
	@echo Invoking: AVR/GNU C Compiler : 4.5.1
	$(QUOTE)C:\Program Files (x86)\Atmel\AVR Tools\AVR Toolchain\bin\avr-gcc.exe$(QUOTE)  -x c -funsigned-char -funsigned-bitfields -DDEBUG  -I"C:\Program Files (x86)\Atmel\Studio\7.0\Packs\atmel\ATmega_DFP\1.2.209\include"  -O0 -ffunction-sections -fdata-sections -fpack-struct -fshort-enums -g2 -Wall -Wextra -pedantic  -mmcu=atmega328p  -c -std=gnu99 -MD -MP -MF "$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -MT"$(@:%.o=%.o)"   -o "$@" "$<" 
	@echo Finished building: $<
	




# AVR32/GNU Preprocessing Assembler



# AVR32/GNU Assembler




ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
endif

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: $(OUTPUT_FILE_PATH) $(ADDITIONAL_DEPENDENCIES)

$(OUTPUT_FILE_PATH): $(OBJS) $(USER_OBJS) $(OUTPUT_FILE_DEP) $(LIB_DEP) $(LINKER_SCRIPT_DEP)
	@echo Building target: $@
	@echo Invoking: AVR/GNU Linker : 4.5.1
	$(QUOTE)C:\Program Files (x86)\Atmel\AVR Tools\AVR Toolchain\bin\avr-gcc.exe$(QUOTE) -o$(OUTPUT_FILE_PATH_AS_ARGS) $(OBJS_AS_ARGS) $(USER_OBJS) $(LIBS) -Wl,-Map="Proyecto de placa principal.map" -Wl,--start-group -Wl,-lm  -Wl,--end-group -Wl,--gc-sections  -mmcu=atmega328p   
	@echo Finished building target: $@
	"C:\Program Files (x86)\Atmel\AVR Tools\AVR Toolchain\bin\avr-objcopy.exe" -O ihex -R .eeprom -R .fuse -R .lock -R .signature -R .user_signatures  "Proyecto de placa principal.elf" "Proyecto de placa principal.hex"
	"C:\Program Files (x86)\Atmel\AVR Tools\AVR Toolchain\bin\avr-objcopy.exe" -j .eeprom  --set-section-flags=.eeprom=alloc,load --change-section-lma .eeprom=0  --no-change-warnings -O ihex "Proyecto de placa principal.elf" "Proyecto de placa principal.eep" || exit 0
	"C:\Program Files (x86)\Atmel\AVR Tools\AVR Toolchain\bin\avr-objdump.exe" -h -S "Proyecto de placa principal.elf" > "Proyecto de placa principal.lss"
	"C:\Program Files (x86)\Atmel\AVR Tools\AVR Toolchain\bin\avr-objcopy.exe" -O srec -R .eeprom -R .fuse -R .lock -R .signature -R .user_signatures "Proyecto de placa principal.elf" "Proyecto de placa principal.srec"
	"C:\Program Files (x86)\Atmel\AVR Tools\AVR Toolchain\bin\avr-size.exe" "Proyecto de placa principal.elf"
	
	





# Other Targets
clean:
	-$(RM) $(OBJS_AS_ARGS) $(EXECUTABLES)  
	-$(RM) $(C_DEPS_AS_ARGS)   
	rm -rf "Proyecto de placa principal.elf" "Proyecto de placa principal.a" "Proyecto de placa principal.hex" "Proyecto de placa principal.lss" "Proyecto de placa principal.eep" "Proyecto de placa principal.map" "Proyecto de placa principal.srec" "Proyecto de placa principal.usersignatures"
	
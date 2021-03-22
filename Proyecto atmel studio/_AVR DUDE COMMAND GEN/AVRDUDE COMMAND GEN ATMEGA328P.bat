@echo off
:start
cd %cd%
echo %cd%
echo AVRDUDE COMMAND GENERATOR
echo.
echo [0]: Write flash memory
echo [1]: Read flash memory
echo [2]: Read fuses
echo.
set /p OPT=Option: 	
IF "%OPT%"=="0" goto Option_0
IF "%OPT%"=="1" goto Option_1
IF "%OPT%"=="2" goto Option_2
:Option_0
cls
echo 2000Hz ATMega328P External crystal oscilator 16MHz 
set /p Input=Enter HEX:	
avrdude.exe -p m328p -F -c usbasp -B 10 -U flash:w:"%Input%":i
echo.
pause
cls
goto start
:Option_1
cls
avrdude.exe -p m88p -F -c usbasp -u -e
echo.
pause
cls
goto start
:Option_2
cls
avrdude.exe -p m88p -F -c usbasp -U efuse:r:efuse.hex:b -U hfuse:r:hfuse.hex:b  -U lfuse:r:lfuse.hex:b 
echo.
pause
cls
goto start


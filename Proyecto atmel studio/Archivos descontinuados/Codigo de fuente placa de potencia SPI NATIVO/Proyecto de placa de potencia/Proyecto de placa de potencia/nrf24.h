#ifndef NRF24
#define NRF24

#include "nRF24L01_Definitions.h"
#include <stdint.h>

#define LOW 0
#define HIGH 1

#define nrf24_ADDR_LEN 5
#define nrf24_CONFIG ((1<<EN_CRC)|(0<<CRCO))

#define NRF24_TRANSMISSON_OK 0
#define NRF24_MESSAGE_LOST   1

void    nrf24_init();
void    nrf24_rx_address(uint8_t* adr);
void    nrf24_tx_address(uint8_t* adr);
void    nrf24_config(uint8_t channel, uint8_t pay_length);

uint8_t nrf24_dataReady();
uint8_t nrf24_isSending();
uint8_t nrf24_getStatus();
uint8_t nrf24_rxFifoEmpty();

void    nrf24_send(uint8_t* value);
void    nrf24_getData(uint8_t* data);

uint8_t nrf24_payloadLength();

uint8_t nrf24_lastMessageStatus();
uint8_t nrf24_retransmissionCount();

uint8_t nrf24_payload_length();

void    nrf24_powerUpRx();
void    nrf24_powerUpTx();
void    nrf24_powerDown();

uint8_t spi_transfer(uint8_t tx);
void    nrf24_transmitSync(uint8_t* dataout,uint8_t len);
void    nrf24_transferSync(uint8_t* dataout,uint8_t* datain,uint8_t len);
void    nrf24_configRegister(uint8_t reg, uint8_t value);
void    nrf24_readRegister(uint8_t reg, uint8_t* value, uint8_t len);
void    nrf24_writeRegister(uint8_t reg, uint8_t* value, uint8_t len);

extern void nrf24_setupPins();

extern void nrf24_ce_digitalWrite(uint8_t state);

extern void nrf24_csn_digitalWrite(uint8_t state);

extern void nrf24_sck_digitalWrite(uint8_t state);

extern void nrf24_mosi_digitalWrite(uint8_t state);

extern uint8_t nrf24_miso_digitalRead();

extern void init_native_spi();

#endif

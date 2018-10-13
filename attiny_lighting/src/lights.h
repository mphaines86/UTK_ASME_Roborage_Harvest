#ifndef LIGHTS_H_
#define LIGHTS_H_

#ifdef __AVR__
    #include <avr/power.h>
#endif

#include <Adafruit_NeoPixel.h>

#define NUMBER_OF_STRIPS 1
#define LIGHTS_PER_STRIP 30

typedef enum {
  RED,
  BLUE,
  GREEN,
  YELLOW,
  ORANGE,
  WHITE,
  GREY,
  PURPLE,
} color_t;



void lightsSetFlash(uint8_t strip, uint8_t isFlashing);
void lightsSetup();
void lightsSetPin(uint8_t pin);
void lightsSetColor(color_t colour, uint8_t pin);
void lightsSetColor(uint8_t i, color_t colour, uint8_t pin);
uint8_t lightsGetFlash(uint8_t strip);

#endif

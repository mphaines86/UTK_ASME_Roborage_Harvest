#ifndef LIGHTS_H_
#define LIGNTS_H_

#include <Adafruit_NeoPixel.h>

#define NUMBER_OF_STRIPS 8
#define LIGHTS_PER_STRIP 30

typedef enum {
  RED,
  BLUE,
  GREEN,
  YELLOW,
  ORANGE,
  WHITE,
  GREY,
} color_t;

uint8_t strip_flashing[NUMBER_OF_STRIPS];
void lightsSetFlash(uint8_t strip, uint8_t isFlashing);
void lightsSetup();
void lightsSetPin(uint8_t pin);
void lightsSetColor(color_t colour);

#endif

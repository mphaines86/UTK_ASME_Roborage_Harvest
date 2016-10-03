#include "lights.h"
#include <Adafruit_NeoPixel.h>

Adafruit_NeoPixel strip = Adafruit_NeoPixel(30, 22, NEO_GRB + NEO_KHZ800);
uint8_t strip_increament[] = {0, 0, 0, 0, 0, 0, 0, 0};
color_t strip_color[] = {ORANGE, WHITE, ORANGE, WHITE, ORANGE, WHITE, ORANGE, WHITE};

void lightsSetup(){
  strip.begin();
  for(uint8_t i=22; i<30; i++){
    strip.setPin(i);
    strip.show();
    strip.setBrightness(64);
  }

}
void lightsSetPin(uint8_t pin){
  strip.setPin(pin);
}

void lightsSetFlash(uint8_t strip, uint8_t isFlashing){
  strip_flashing[strip] = isFlashing;
}

void lightsSetColor(color_t colour, uint8_t pin){

  lightsSetPin(30 + pin);
  strip_color[pin] = colour;

  switch (colour){
    case RED: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 141, 32, 72);
    }
    case BLUE: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 0, 108, 147);
    }
    case GREEN: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 0, 116, 111);
    }
    case YELLOW: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 254, 213, 53);
    }
    case ORANGE: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 255, 130, 0);
    }
    case WHITE: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 255, 255, 255);
    }
    case GREY: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 88, 89, 91);
    }
  }
}

ISR(TIMER4_COMPA_vect){
  int i, j;
  for(i=0; i<NUMBER_OF_STRIPS; i++){
    if (strip_flashing[i]){
      lightsSetPin(30 + i);
      for(j=0; j<LIGHTS_PER_STRIP; j++){
        if (j != strip_increament[i]){
          strip.setPixelColor(j, 0, 0, 0);

        }
      }
    }
  }

}

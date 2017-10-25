#include "lights.h"

Adafruit_NeoPixel strip = Adafruit_NeoPixel(30, 4, NEO_GRB + NEO_KHZ800);

color_t strip_color[] = {ORANGE, WHITE, ORANGE, WHITE, ORANGE, WHITE, ORANGE, WHITE};
uint8_t strip_flashing[NUMBER_OF_STRIPS];

void lightsSetup(){

    #if defined (__AVR_ATtiny85__)
      if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
    #endif

    strip.begin();
    strip.setPin(4);
    strip.show();
    strip.setBrightness(100);
}


void lightsSetPin(uint8_t pin){
  strip.setPin(pin);
}

void lightsSetFlash(uint8_t strip, uint8_t isFlashing){
  strip_flashing[strip] = isFlashing;
}

uint8_t lightsGetFlash(uint8_t strip){
  return strip_flashing[strip];
}

void lightsSetColor(color_t colour, uint8_t pin){

  lightsSetPin(pin);
  strip_color[pin] = colour;

  switch (colour){
    case RED: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 141, 32, 72);
      break;
    }
    case BLUE: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 0, 108, 147);
      break;
    }
    case GREEN: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 0, 116, 111);
      break;
    }
    case YELLOW: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 254, 213, 53);
      break;
    }
    case ORANGE: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 255, 130, 0);
      break;
    }
    case WHITE: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 255, 255, 255);
      break;
    }
    case GREY: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 88, 89, 91);
      break;
    }
    case BLACK: {
      for(uint8_t i = 0; i<30; i++)
        strip.setPixelColor(i, 0, 0, 0);
      break;
    }
  }
  strip.show();
}

void lightsSetColor(uint8_t i, color_t colour, uint8_t pin){

  lightsSetPin(pin);
  strip_color[pin] = colour;

  switch (colour){
    case RED: {
      strip.setPixelColor(i, 141, 32, 72);
      break;
      }
    case BLUE: {
      strip.setPixelColor(i, 0, 108, 147);
      break;
    }
    case GREEN: {
      strip.setPixelColor(i, 0, 116, 111);
      break;
    }
    case YELLOW: {
      strip.setPixelColor(i, 254, 213, 53);
      break;
    }
    case ORANGE: {
      strip.setPixelColor(i, 255, 130, 0);
      break;
    }
    case WHITE: {
      strip.setPixelColor(i, 255, 255, 255);
      break;
    }
    case GREY: {
      strip.setPixelColor(i, 88, 89, 91);
      break;
    }
    case BLACK: {
      strip.setPixelColor(i, 0, 0, 0);
      break;
    }
  }
  strip.show();
}

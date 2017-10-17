#include "Arduino.h"
#include "lights.h"

uint8_t flagBit = 0;

int main() {
    init();

#if defined(USBCON)
    USB.attach();
#endif

    setup();

    while (1) {
        loop();
    }

    return 0;
}

void Delay(void){
    float usdelay = 0.0;
    usdelay =(1000/ ( (1.0 / 28000000) * 1000000000 ) );

    while (usdelay != 0) {
        asm("NOP");
        usdelay--;
    }
}

void Delay_us(int delayus){
    while (delayus != 0){
        Delay();
        delayus--;
    }

}

void setup(void){
    pinMode(1, INPUT);
    lightsSetPin(0);
    lightsSetColor(RED, 0);
}

void loop(void){
    uint8_t pinValue = (PINB >> 1) & 0b0000011;
    if (pinValue == 1 && pinValue != flagBit){
        lightsSetColor(BLUE, 0);
        flagBit = pinValue;
    }
    else if (pinValue == 3 && pinValue != flagBit){
        uint8_t currentPin = 0;
        while (currentPin < 30){
            lightsSetColor(currentPin, WHITE, 0);
            delay(10);
            lightsSetColor(currentPin, BLUE, 0);
            currentPin++;
        }
    }
    else if(pinValue == 0 && pinValue != flagBit){
        for (uint8_t i = 0; i < 30; i+=2) {
           lightsSetColor(i, ORANGE, 0);
        }
        for (uint8_t i = 1; i < 30; ++i) {
            lightsSetColor(i, WHITE, 0);
        }
        flagBit = pinValue;
    }
}

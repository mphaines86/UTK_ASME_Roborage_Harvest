#include "Arduino.h"
#include "lights.h"

uint8_t flagBit = 4;

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
    pinMode(0, INPUT);
    lightsSetup();
    for (uint8_t i = 0; i < 30; i+=2) {
        lightsSetColor(i, ORANGE, 4);
    }
    for (uint8_t i = 1; i < 30; i+=2) {
        lightsSetColor(i, WHITE, 4);
    }
}

void loop(void){
    uint8_t pinValue = PINB & 0b00000011;


    if (pinValue == 1 && pinValue != flagBit){
        lightsSetColor(PURPLE, 4);
        flagBit = pinValue;
    }
    else if (pinValue == 3 && pinValue != flagBit){
        uint8_t currentFlash = 0;
        while (currentFlash < 3){
            lightsSetColor(WHITE, 4);
            delay(300);
            lightsSetColor(PURPLE, 4);
            delay(300);
            currentFlash++;
        }
        flagBit = pinValue;
    }
    else if(pinValue == 0 && pinValue != flagBit){
        for (uint8_t i = 0; i < 30; i+=2) {
           lightsSetColor(i, ORANGE, 4);
        }
        for (uint8_t i = 1; i < 30; i+=2) {
            lightsSetColor(i, WHITE, 4);
        }
        flagBit = pinValue;
    }
}

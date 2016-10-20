
#include "MessageReader.h"
#include "process.h"
#include "lights.h"

//#define NUM_MOTORS 6
//#define DEBOUNCE_MAX 4

const int timeout = 500;
uint32_t lasttime = 0;
uint32_t haslostsignal = 0;

void setup();
void loop();

int main(){
	init();

	#if defined(USBCON)
		USB.attach();
	#endif

		setup();

		while(1) {
			loop();
			if (serialEventRun) serialEventRun();
		}

		return 0;
}

struct message_t message;

void setup(void) {
  Serial.begin(115200);

	for(uint8_t i=22; i<30; i++){
		pinMode(i, OUTPUT);
	}

	for(uint8_t i =30; i<38; i++){
		pinMode(i, INPUT_PULLUP);
	}

	lightsSetup();
	lightsSetPin(22);
	for (uint8_t i = 0; i<8; i++){
		lightsSetColor(ORANGE, i);
	}

	process_begin();

	cli();//stop interrupts

	//set timer1 interrupt at 1Hz
	TCCR3A = 0;// set entire TCCR1A register to 0
	TCCR3B = 0;// same for TCCR1B
	TCNT3  = 0;//initialize counter value to 0
	// set compare match register for 1hz increments
	OCR3A = F_CPU / 625;// = (16*10^6) / (1*1024) - 1 (must be <65536)
	// turn on CTC mode
	TCCR3B |= (1 << WGM32);
	// Set CS10 and CS12 bits for 1024 prescaler
	TCCR3B |= (1 << CS32) | (0 << CS31) | (0 << CS30);
	// enable timer compare interrupt
	TIMSK3 |= (1 << OCIE3A);

	/*TCCR4A = 0;
	TCCR4B = 0;
	OCR4A = F_CPU / 15625;
	TCCR4B |= (1 << WGM42);
	TCCR4B |= (1 << CS42) | (0 << CS41) | (1 << CS40);
	TIMSK4 |= (1 << OCIE4A);*/
	sei();

	setupReader(&message);
}

void loop(void){
	if (read_message(&message)) {
		process_message(&message);
		//Serial.println(1);
		//message_processed(&message);
	}

}


#include "message.h"

#define NUM_MOTORS 6
#define DEBOUNCE_MAX 4

const int ledpin = 13;
const int timeout = 500;
uint32_t lasttime = 0;
uint32_t haslostsignal = 0;

struct motor_t {
	byte value;
	byte pin;
} motors[NUM_MOTORS];

struct motor_message_t {
	byte motor_number;
	byte value;
};

typedef enum{
	RED_TEAM,
	BLUE_TEAM,
	GREEN_TEAM,
	YELLOW_TEAM,
	NUM_TEAMS,
} teams_t;

struct team_t {
	byte active;
	byte color;
	byte isPressed;
	byte integrator;
	byte lastUpdate;
	byte score;
} team_data[NUM_TEAMS];

struct start_message_t {
	byte start;
	byte teams;
};

struct team_message_t {
	byte team;
};


byte process_start_message(struct start_message_t*, byte);
byte process_team_message(struct team_message_t*, byte);
byte process_motor_message(struct motor_message_t*, byte);
byte process_ping_message();
void process_message(struct message_t *);

void process_message(struct message_t *message) {
	byte body_length;

	if (message->state != MESSAGE_READY) {
		return;
	}

	//Serial.print(message->data.header.length);
	//Serial.print(": ");

	body_length = message->data.header.length - sizeof(message->data.header);

	//Serial.print(message->data.header.action);
	//Serial.print(" ");

	switch (message->data.header.action) {
		case 'm':
			if (process_motor_message((struct motor_message_t *)&message->data.body, body_length)) {
				message_processed(message);
			}
			break;

		case 'k':
			break;

		case 'p':
			if(process_ping_message()){
				message_processed(message);
			}
			break;
		case 's':
			if (process_start_message((struct start_message_t *)&message->data.body, body_length)) {
				message_processed(message);
			}
			break;
		case 't':
			if (process_team_message((struct team_message_t *)&message->data.body, body_length)){
				message_processed(message);
			}
		default:
			message_processed(message);
			break;
	}
}

byte process_ping_message(){
	lasttime = millis();
	//Serial.println("Ping");
	return 1;

}

byte process_team_message(struct team_message_t *team_message, byte size){
	Serial.println(team_message->team);
	Serial.println(team_data[team_message->team].score);
	return 1;
}


byte process_start_message(struct start_message_t *start_message, byte size){
		byte active_teams = start_message->teams;
		byte i;
		for (i=0; i<4; i++){
			if (active_teams&(1<<i)){
					team_data[i].active = 1;
					team_data[i].color = i;
					team_data[i].score = 0;
			}
			else{
				team_data[i].active = 0;
			}
		}
		if (start_message->start) sei();

		return 1;
}

byte process_motor_message(struct motor_message_t *motor_message, byte size) {
	byte motor_number = motor_message->motor_number;

	//Serial.println(motor_number);
	//Serial.print(" ");

	if (!(0 <= motor_number && motor_number < NUM_MOTORS)) {
		return 1;
	}
	motors[motor_number].value = motor_message->value;

	return 1;
}

struct message_t message;
void setup(void) {
  	Serial.begin(115200);

	cli();//stop interrupts

	//set timer1 interrupt at 1Hz
	TCCR3A = 0;// set entire TCCR1A register to 0
	TCCR3B = 0;// same for TCCR1B
	TCNT3  = 0;//initialize counter value to 0
	// set compare match register for 1hz increments
	OCR3A = F_CPU / 50000;// = (16*10^6) / (1*1024) - 1 (must be <65536)
	// turn on CTC mode
	TCCR3B |= (1 << WGM32);
	// Set CS10 and CS12 bits for 1024 prescaler
	TCCR3B |= (1 << CS32) | (0 << CS31) | (1 << CS30);
	// enable timer compare interrupt
	TIMSK3 |= (1 << OCIE3A);
}

void loop(void){

	if (read_message(&message)) {
		process_message(&message);

	}

}

static uint8_t debounce(uint16_t portRegister, uint8_t port, teams_t team_color){

  uint8_t output = 0;

  if (!(portRegister&(1<<port))){
    if (team_data[team_color].integrator > 0)
      team_data[team_color].integrator--;
  }
  else if (team_data[team_color].integrator < DEBOUNCE_MAX)
      team_data[team_color].integrator++;

  if (team_data[team_color].integrator == 0)
      output = 0;
  else if (team_data[team_color].integrator>= DEBOUNCE_MAX){
    output = 1;
    team_data[team_color].integrator = DEBOUNCE_MAX;
  }

  return output;
}

ISR(TIMER3_COMPA_vect){
	Serial.println(millis());
	uint8_t output;
	if(team_data[RED_TEAM].active){
			output = debounce(PORTH, 0x10, RED_TEAM);
			if(output){
					if(team_data[RED_TEAM].isPressed){
							team_data[RED_TEAM].score += millis() - team_data[RED_TEAM].lastUpdate;
							team_data[RED_TEAM].lastUpdate = millis();
					}
			}
		}
	if(team_data[BLUE_TEAM].active){
			output = debounce(PORTH, 0x10, BLUE_TEAM);
			if(output){
					if(team_data[BLUE_TEAM].isPressed){
							team_data[BLUE_TEAM].score += millis() - team_data[BLUE_TEAM].lastUpdate;
							team_data[BLUE_TEAM].lastUpdate = millis();
					}
			}
		}
	if(team_data[GREEN_TEAM].active){
			output = debounce(PORTH, 0x10, GREEN_TEAM);
			if(output){
					if(team_data[GREEN_TEAM].isPressed){
							team_data[GREEN_TEAM].score += millis() - team_data[GREEN_TEAM].lastUpdate;
							team_data[GREEN_TEAM].lastUpdate = millis();
					}
			}
		}
	if(team_data[YELLOW_TEAM].active){
			output = debounce(PORTH, 0x10, YELLOW_TEAM);
			if(output){
					if(team_data[YELLOW_TEAM].isPressed){
							team_data[YELLOW_TEAM].score += millis() - team_data[YELLOW_TEAM].lastUpdate;
							team_data[YELLOW_TEAM].lastUpdate = millis();
					}
			}
		}

}

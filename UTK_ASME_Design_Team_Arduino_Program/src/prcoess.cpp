#include "process.h"
#include "MessageReader.h"
#include "MessageWriter.h"
#include "lights.h"
#include <Arduino.h>

#define uint16touint8(value, byteArray, index) {byteArray[index] = value & 0xFF; byteArray[index +1] = value >>8;}

uint8_t strip_increament[] = {0, 0, 0, 0, 0, 0, 0, 0};

struct team_t team_data[NUM_TEAMS];
struct motor_t motors[NUM_MOTORS];
struct pole_t poles[8];

uint8_t active = 0;
bool invalidTeamConfigure;

void process_message(struct message_t *message) {
	uint8_t body_length;

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
				struct message_output_t outputMessage;
				uint8_t body[MAX_MESSAGE_SIZE];
				body[0] = 6;
				writerPrepMessage(&outputMessage, 'p', body);
				writerSendMessage(&outputMessage);
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

uint8_t process_ping_message(){
	//lasttime = millis();
	//Serial.println("Ping");
	return 1;

}

uint8_t process_team_message(struct team_message_t *team_message, uint8_t size){
		struct message_output_t outputMessage;
		uint8_t data[MAX_MESSAGE_SIZE];

		data[0] = team_data[team_message->team].active;
		data[1] = team_data[team_message->team].color;
		uint16touint8(team_data[team_message->team].score, data, 2);

		writerPrepMessage(&outputMessage, 't', data);
		writerSendMessage(&outputMessage);

	return 1;
}


uint8_t process_start_message(struct start_message_t *start_message, uint8_t size){
		uint8_t i;
		uint8_t numTeams = 0;
		if (start_message->setupBit){
			for (i=0; i<4; i++){
				if (start_message->teams&(1<<i)){
						team_data[i].active = 1;
						team_data[i].color = i;
						team_data[i].score = 0;
						numTeams++;
				}
				else{
					team_data[i].active = 0;
				}
			}
			switch (numTeams){
				case 2:
				case 4: {
					uint8_t j = 0;
					while(j<8){
						for (i=0; i<4; i++){
							if(team_data[i].active){
								poles[j].poleId = j;
								poles[j].colorOwnership = i;
								poles[j].isPressed = 0;
								j++;

							}
						}
					}
					break;
				}
				case 0:
				case 1:
				case 3:{
					invalidTeamConfigure = true;
				}
			}
	}

	if (start_message->start){
		if (invalidTeamConfigure){
			if (numTeams == 0){
				Serial.println("Invalid Team Configuration Error: No teams activated!");
			}
			else{
				Serial.println("Invalid Team Configuration Error: Not a valid team configuration!");
			}
		}
		else{
			active = 1;
		}
	}
		return 1;
}

uint8_t process_motor_message(struct motor_message_t *motor_message, uint8_t size) {
	uint8_t motor_number = motor_message->motor_number;

	//Serial.println(motor_number);
	//Serial.print(" ");

	if (!(0 <= motor_number && motor_number < NUM_MOTORS)) {
		return 1;
	}
	motors[motor_number].value = motor_message->value;

	return 1;
}

static uint8_t debounce(uint16_t portRegister, uint8_t port, uint8_t poleId){

  uint8_t output = 0;

  if (!(portRegister&(1<<port))){
    if (poles[poleId].integrator > 0)
      poles[poleId].integrator--;
  }
  else if (poles[poleId].integrator < DEBOUNCE_MAX)
      poles[poleId].integrator++;

  if (poles[poleId].integrator == 0)
      output = 0;
  else if (poles[poleId].integrator>= DEBOUNCE_MAX){
    output = 1;
    poles[poleId].integrator = DEBOUNCE_MAX;
  }

  return output;
}

ISR(TIMER3_COMPA_vect){
	//Serial.println(millis());
	if (active){
		uint8_t output;
		for(uint8_t i=0; i<8; i++){
			output = debounce(PORTC, i, i);
			if(!output){
				if(poles[i].isPressed){
					poles[i].isPressed = 0;
					team_data[poles[i].colorOwnership].score = millis() - poles[i].lastUpdate;
					team_data[poles[i].colorOwnership].active = 0;
					lightsSetFlash(i, 1);
				}
			}
			else if (output){
				if (!poles[i].isPressed){
					poles[i].isPressed = 1;
					poles[i].lastUpdate = millis();
					team_data[poles[i].colorOwnership].active = 1;
					lightsSetFlash(i, 0);
					lightsSetColor((color_t)poles[i].colorOwnership, i);
				}
			}
		}

		for(uint8_t i=0; i<0; i++){
				if(poles[i].isPressed){
					poles[i].lastUpdate = millis();
				}
		}

	}
}

ISR(TIMER4_COMPA_vect){
  int i, j;
  for(i=0; i<NUMBER_OF_STRIPS; i++){
    if (lightsGetFlash(i)){
      for(j=0; j<LIGHTS_PER_STRIP; j++){
        if (j <= strip_increament[i] || j >= strip_increament[i] + 8){
          lightsSetColor(j, BLACK, i);

        }
        else {
						lightsSetColor(j, (color_t)poles[i].colorOwnership, i);
        }
      }
    }
  }

}

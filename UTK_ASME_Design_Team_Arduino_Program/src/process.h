#ifndef PROCESS_MESSAGE_H_
#define PROCESS_MESSAGE_H_

#include <Arduino.h>

#define NUM_MOTORS 6
#define DEBOUNCE_MAX 4

struct motor_t {
	uint8_t value;
	uint8_t pin;
};

struct motor_message_t {
	uint8_t motor_number;
	uint8_t value;
};

typedef enum{
	 RED_TEAM,
	 BLUE_TEAM,
	 GREEN_TEAM,
	 YELLOW_TEAM,
	 NUM_TEAMS,
} teams_t;

struct team_t {
	uint8_t color;
	uint8_t active;
	uint16_t score;
};

struct pole_t {
	uint8_t poleId;
	uint8_t colorOwnership;
	uint8_t isPressed;
	uint8_t integrator;
	uint8_t lastUpdate;
};

struct start_message_t {
	//uint8_t gameTime;
	uint8_t setupBit;
	uint8_t start;
	uint8_t teams;
};

struct team_message_t {
	uint8_t team;
	uint8_t active;
	uint8_t value;
};

uint8_t process_start_message(struct start_message_t*, uint8_t);
uint8_t process_team_message(struct team_message_t*, uint8_t);
uint8_t process_motor_message(struct motor_message_t*, uint8_t);
uint8_t process_receiver_message(struct receiver_message_t*, uint8_t);
uint8_t process_ping_message();
void process_message(struct message_t *);

#endif

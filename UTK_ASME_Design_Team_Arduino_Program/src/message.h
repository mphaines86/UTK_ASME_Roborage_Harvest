#ifndef COMMUNICATE_MESSAGE_H_
#define COMMUNICATE_MESSAGE_H_

#include <Arduino.h>

#define MAX_MESSAGE_LENGTH 32
#define MEMBER_SIZE(type, member) sizeof(((type *)0)->member)

enum message_state_t {
	WAITING_FOR_LENGTH,
	WAITING_FOR_ACTION,
	WAITING_FOR_MESSAGE,
	MESSAGE_READY
};

struct message_data_t {
	enum message_state_t state;
	struct {
		byte length;
		byte action;
	} header;
	byte body[MAX_MESSAGE_LENGTH];
};

struct message_t {
	enum message_state_t state;
	struct message_data_t data;
};

byte read_message(struct message_t *);
void message_processed(struct message_t *);

#endif // COMMUNICATE_MESSAGE_H_

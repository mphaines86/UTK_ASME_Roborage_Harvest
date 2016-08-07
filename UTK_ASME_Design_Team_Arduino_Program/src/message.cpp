#include "message.h"
#include <Arduino.h>

byte read_message(struct message_t *message) {
	byte body_length;

	switch (message->state) {
	case WAITING_FOR_LENGTH: {
		if (Serial.available() > 0) {
			message->data.header.length = Serial.read();
			message->state = WAITING_FOR_ACTION;
			//Serial.println("got length");
		}
		break;
	}
	case WAITING_FOR_ACTION: {
		if (Serial.available() > 0) {
			message->data.header.action = Serial.read();
			message->state = WAITING_FOR_MESSAGE;
			//Serial.println("got Action");
		}
		break;
	}
	case WAITING_FOR_MESSAGE: {
		body_length = message->data.header.length - sizeof(message->data.header);
		if (Serial.available() >= body_length) {
			for (byte i=0; i<body_length; ++i) {
				message->data.body[i] = Serial.read();
			//	Serial.println("getting messages");
			}
			//Serial.println("got messages");
			message->state = MESSAGE_READY;
		}
		break;
	}
	case MESSAGE_READY:
		//Serial.println("ready for a message");
		/** Do nothing if the message is ready */
		break;
	}

	return message->state == MESSAGE_READY;
}

void message_processed(struct message_t *message) {
	message->state = WAITING_FOR_LENGTH;
}

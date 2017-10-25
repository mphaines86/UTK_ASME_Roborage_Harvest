//
// Created by michael on 10/14/17.
//
#include "Arduino.h"
#include "main.h"
#include "BlynkSimpleStream.h"
#include "MessageWriter.h"

char auth[] = "2242d5b5067b422ba8fd1bf80e98c40f";
struct team_t team_data[NUM_TEAMS];

int main() {
    init();

#if defined(USBCON)
    USB.attach();
#endif

    setup();

    while (1) {
        loop();
        if (serialEventRun) serialEventRun();
    }

    return 0;
}

BLYNK_WRITE(V0){
    team_data[RED_TEAM].score += param.asInt() - team_data[RED_TEAM].redBall;
    team_data[RED_TEAM].readWrite |= 0xC;
    team_data[RED_TEAM].redBall = (uint8_t) param.asInt();
}

BLYNK_WRITE(V1){
    team_data[RED_TEAM].score -= 3*(param.asInt() - team_data[RED_TEAM].blueBall);
    team_data[RED_TEAM].readWrite |= 0x14;
    team_data[RED_TEAM].blueBall = (uint8_t) param.asInt();
}

BLYNK_WRITE(V2){
    team_data[RED_TEAM].score -= 3*(param.asInt() - team_data[RED_TEAM].greenBall);
    team_data[RED_TEAM].readWrite |= 0x24;
    team_data[RED_TEAM].greenBall = (uint8_t) param.asInt();
}

BLYNK_WRITE(V3){
    team_data[RED_TEAM].score -= 3*(param.asInt() - team_data[RED_TEAM].purpleBall);
    team_data[RED_TEAM].readWrite |= 0x44;
    team_data[RED_TEAM].purpleBall = (uint8_t) param.asInt();
}

BLYNK_WRITE(V4){
    team_data[RED_TEAM].score += 3*(param.asInt() - team_data[RED_TEAM].racketBall);
    team_data[RED_TEAM].readWrite |= 0x84;
    team_data[RED_TEAM].racketBall = (uint8_t) param.asInt();
}

BLYNK_WRITE(V5){
    team_data[BLUE_TEAM].score -= 3*(param.asInt() - team_data[BLUE_TEAM].redBall);
    team_data[BLUE_TEAM].readWrite |= 0x0C;
    team_data[BLUE_TEAM].redBall = (uint8_t) param.asInt();
}

BLYNK_WRITE(V6){
    team_data[BLUE_TEAM].score += param.asInt() - team_data[BLUE_TEAM].blueBall;
    team_data[BLUE_TEAM].readWrite |= 0x14;
    team_data[BLUE_TEAM].blueBall = (uint8_t) param.asInt();
}

BLYNK_WRITE(V7){
    team_data[BLUE_TEAM].score -= 3*(param.asInt() - team_data[BLUE_TEAM].greenBall);
    team_data[BLUE_TEAM].readWrite |= 0x24;
    team_data[BLUE_TEAM].greenBall = (uint8_t) param.asInt();
}

BLYNK_WRITE(V8){
    team_data[BLUE_TEAM].score -= 3*(param.asInt() - team_data[BLUE_TEAM].purpleBall);
    team_data[BLUE_TEAM].readWrite |= 0x44;
    team_data[BLUE_TEAM].purpleBall = (uint8_t) param.asInt();
}

BLYNK_WRITE(V9){
    team_data[BLUE_TEAM].score += 3*(param.asInt() - team_data[BLUE_TEAM].racketBall);
    team_data[BLUE_TEAM].readWrite |= 0x84;
    team_data[BLUE_TEAM].racketBall = (uint8_t) param.asInt();
}



void setup(void) {
    Serial.begin(115200);
    Blynk.begin(Serial, auth);
    Serial1.begin(115200);


    for (uint8_t i = 0; i < NUM_TEAMS - 2; ++i) {
        team_data[i].color = i;
        team_data[i].readWrite = 0;
    }
}

void loop(void) {

    Blynk.run();

    for (int i = 0; i < NUM_TEAMS - 2; ++i) {

        if (team_data[i].readWrite) {
            uint8_t data[MAX_MESSAGE_SIZE - 2];
            struct message_output_t outputMessage{};
            data[1] = 1;
            data[0] = team_data[i].color;
            //uint16touint8(team_data[team_message->team].score, data, 2);
            data[2] = (uint8_t) team_data[i].score;
            data[3] = team_data[i].redBall;
            data[4] = team_data[i].greenBall;
            data[5] = team_data[i].blueBall;
            data[6] = team_data[i].purpleBall;
            data[7] = team_data[i].racketBall;
            data[8] = team_data[i].readWrite;
            writerPrepMessage(&outputMessage, 't', data);
            writerSendMessage(&outputMessage);
            team_data[i].readWrite = 0;
        }
    }

}
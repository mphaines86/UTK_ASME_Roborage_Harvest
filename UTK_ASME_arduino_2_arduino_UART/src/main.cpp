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
        if(team_data[RED_TEAM].redBall != param.asInt()){
            team_data[RED_TEAM].redBall = (uint8_t) param.asInt();
            team_data[RED_TEAM].score += 1;
            team_data[RED_TEAM].readWrite |= 1 << 1;
        }
}

BLYNK_WRITE(V1){
    if(team_data[RED_TEAM].greenBall != param.asInt()){
        team_data[RED_TEAM].greenBall = (uint8_t) param.asInt();
        team_data[RED_TEAM].score -= 3;
        team_data[RED_TEAM].readWrite |= 1 << 2;
    }
}

BLYNK_WRITE(V2){
    if(team_data[RED_TEAM].blueBall != param.asInt()){
        team_data[RED_TEAM].blueBall = (uint8_t) param.asInt();
        team_data[RED_TEAM].score -= 3;
        team_data[RED_TEAM].readWrite |= 1 << 3;
    }
}

BLYNK_WRITE(V3){
    if(team_data[RED_TEAM].purpleBall != param.asInt()){
        team_data[RED_TEAM].purpleBall = (uint8_t) param.asInt();
        team_data[RED_TEAM].score -= 3;
        team_data[RED_TEAM].readWrite |= 1 << 4;
    }
}

BLYNK_WRITE(V4){
    if(team_data[RED_TEAM].racketBall != param.asInt()){
        team_data[RED_TEAM].racketBall = (uint8_t) param.asInt();
        team_data[RED_TEAM].score += 3;
        team_data[RED_TEAM].readWrite |= 1 << 5;
    }
}

BLYNK_WRITE(V5){
    if(team_data[BLUE_TEAM].redBall != param.asInt()){
        team_data[BLUE_TEAM].redBall = (uint8_t) param.asInt();
        team_data[BLUE_TEAM].score -= 3;
        team_data[BLUE_TEAM].readWrite |= 1 << 1;
    }
}

BLYNK_WRITE(V6){
    if(team_data[BLUE_TEAM].greenBall != param.asInt()){
        team_data[BLUE_TEAM].greenBall = (uint8_t) param.asInt();
        team_data[BLUE_TEAM].score -= 3;
        team_data[BLUE_TEAM].readWrite |= 1 << 2;
    }
}

BLYNK_WRITE(V7){
    if(team_data[BLUE_TEAM].blueBall != param.asInt()){
        team_data[BLUE_TEAM].blueBall = (uint8_t) param.asInt();
        team_data[BLUE_TEAM].score += 1;
        team_data[BLUE_TEAM].readWrite |= 1 << 3;
    }
}

BLYNK_WRITE(V8){
    if(team_data[BLUE_TEAM].purpleBall != param.asInt()){
        team_data[BLUE_TEAM].purpleBall = (uint8_t) param.asInt();
        team_data[BLUE_TEAM].score -= 3;
        team_data[BLUE_TEAM].readWrite |= 1 << 4;
    }
}

BLYNK_WRITE(V9){
    if(team_data[BLUE_TEAM].racketBall != param.asInt()){
        team_data[BLUE_TEAM].racketBall = (uint8_t) param.asInt();
        team_data[BLUE_TEAM].score += 3;
        team_data[BLUE_TEAM].readWrite |= 1 << 5;
    }
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
            uint8_t data[MAX_MESSAGE_SIZE];
            struct message_output_t outputMessage{};
            data[1] = 1;
            data[0] = team_data[i].color;
            //uint16touint8(team_data[team_message->team].score, data, 2);
            data[2] = team_data[i].score;
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
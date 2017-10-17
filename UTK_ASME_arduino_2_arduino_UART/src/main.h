//
// Created by michael on 10/14/17.
//

#ifndef UTK_ASME_ARDUINO_2_ARDUINO_UART_MAIN_H
#define UTK_ASME_ARDUINO_2_ARDUINO_UART_MAIN_H

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
    uint8_t score;
    uint8_t redBall;
    uint8_t greenBall;
    uint8_t blueBall;
    uint8_t purpleBall;
    uint8_t racketBall;
    uint8_t readWrite;
};

#endif //UTK_ASME_ARDUINO_2_ARDUINO_UART_MAIN_H

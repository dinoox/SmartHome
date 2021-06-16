#ifndef __RECV__H__
#define __RECV__H__



enum MO_CMD{
    FAN_OFF = STORE2|FAN|0x00,
    FAN_1 = STORE2|FAN|0x01,
    FAN_2 = STORE2|FAN|0x02,
    FAN_3 = STORE2|FAN|0x03,

    BEEP_OFF = STORE2|BEEP|0x00,
    BEEP_ON = STORE2|BEEP|0x01,
    BEEP_ALRRM_OFF = STORE2|BEEP|0x02,
    BEEP_ALRRM_ON = STORE2|BEEP|0x03,

    LED_OFF = STORE2|LED|0x00,
    LED_ON = STORE2|LED|0x01,

    // my CAMERA
    CAMERA_ON = STORE2|CAMERA|0x01,
    CAMERA_OFF = STORE2|CAMERA|0x00,


};

int init_usb();
int start_recv();
int wait_recv();

void fan_control(int speed);
void beep_control(int on);
void led_control(int on);
#endif

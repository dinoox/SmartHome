#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <pthread.h>
#include <stdint.h>
#include <termios.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include "data_global.h"
#include "serial.h"

static int fd_uart;
static pthread_t pid;

/**
 * 初始化串口/dev/ttyUSB0
 * @return 0成功 -1失败
 */
int init_usb()
{
    struct termios options;

    //打开串口设备
    fd_uart = open("/dev/ttyUSB0", O_RDWR);
    if(fd_uart < 0){
        perror("open err");
        return -1;
    }

    //设置串口属性
    tcgetattr(fd_uart, &options);
    options.c_cflag |= ( CLOCAL | CREAD );
    options.c_cflag &= ~CSIZE;
    options.c_cflag &= ~CRTSCTS;
    options.c_cflag |= CS8;
    options.c_cflag &= ~CSTOPB;
    options.c_iflag |= IGNPAR;
    options.c_iflag &= ~(ICRNL | IXON);
    options.c_oflag = 0;
    options.c_lflag = 0;

    cfsetispeed(&options, B115200);
    cfsetospeed(&options, B115200);
    tcsetattr(fd_uart, TCSANOW, &options);

    return 0;
}


static float dota_atof (char unitl)
{
    if (unitl > 100)
    {
        return unitl / 1000.0;
    }
    else if (unitl > 10)
    {
        return unitl / 100.0;
    }
    else
    {
        return unitl / 10.0;
    }
}

static int dota_atoi (const char *cDecade)
{
    int result = 0;
    if (' ' != cDecade[0])
    {
        result = (cDecade[0] - 48) * 10;
    }
    result += cDecade[1] - 48;
    return result;
}

static float dota_adc (unsigned int ratio)
{
    return ((ratio * 3.3) / 1024);
}

void *pthread_serial (void *arg)
{
    struct env_info envinfo;
    int ret;

    while(1){
        ret = read(fd_uart, &envinfo, sizeof(struct env_info));
        if(ret != sizeof(envinfo)){
            continue;
        }

        env_msg.x = envinfo.x;
        env_msg.y = envinfo.y;
        env_msg.z = envinfo.z;
        env_msg.temperature = envinfo.temp[0] + dota_atof(envinfo.temp[1]);
        env_msg.humidity = envinfo.hum[0] + dota_atof(envinfo.hum[1]);
        env_msg.ill = envinfo.ill;
        env_msg.bet = dota_adc(envinfo.bet);
        env_msg.adc = dota_adc(envinfo.adc);

        raise(DATA_ARRIVE);
    }
}

int start_recv()
{
    return pthread_create(&pid, NULL, pthread_serial, NULL);
}

/**
 * @brief 等待串口线程
 * @return
 */
int wait_recv()
{
    return pthread_join(pid, NULL);
}

void led_control(int on)
{
    uint8_t cmd;

    if(on){
        cmd = LED_ON;
    }else{
        cmd = LED_OFF;
    }
    write(fd_uart, &cmd, 1);
}

void beep_control(int on)
{
    uint8_t cmd;

    if(on){
        cmd = BEEP_ON;
    }else{
        cmd = BEEP_OFF;
    }
    write(fd_uart, &cmd, 1);
}

void fan_control(int speed)
{
    uint8_t cmd;
    switch(speed){
        case 0:
        cmd = FAN_OFF;
        break;
        case 1:
        cmd = FAN_1;
        break;
        case 2:
        cmd = FAN_2;
        break;
        case 3:
        cmd = FAN_3;                
        break;
        default:
        break;
    }
    write(fd_uart, &cmd, 1);
}

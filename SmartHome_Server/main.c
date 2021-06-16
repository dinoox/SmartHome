#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>


#include <signal.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <pthread.h>
#include "data_global.h"
#include "serial.h"
#include "server.h"
#include <time.h>
void rand_data();

void data_handler(int signal)
{
    printf("数据： 温度=%0.2f, 湿度=%0.2f, 光照=%0.2f, 电池电量=%0.2f, 电位器信息=%0.2f ,X轴=%d, Y轴=%d, Z轴=%d \n", \
               env_msg.temperature, env_msg.humidity, env_msg.ill, env_msg.bet, env_msg.adc, env_msg.x, env_msg.y, env_msg.z);



    send_data(&env_msg);
}

void cmd_handler(int signal)
{
    printf("recv cmd = 0x%x\n", recv_cmd);
    switch(recv_cmd){
        case FAN_OFF:
			printf("FAN_OFF\n");
        break;
        case FAN_1:
			printf("FAN_1\n");
        break;
        case FAN_2:
			printf("FAN_2\n");
        break;
        case FAN_3:
			printf("FAN_3\n");
        break;
        case BEEP_OFF:
			printf("BEEP_OFF\n");
        break;
        case BEEP_ON:
			printf("BEEP_ON\n");
        break;        
        case LED_OFF:
			printf("LED_OFF\n");
        break;
        case LED_ON:
			printf("LED_ON\n");
        break;
        case CAMERA_OFF:
			printf("CAMERA_OFF\n");
        break;
        case CAMERA_ON:
			printf("CAMERA_ON\n");
        break;
        default:
        break;
    }
}

int main(int argc, char *argv[])
{
    int ret;

    if(argc != 2){
        printf("usage: <%s> port\n", argv[0]);
        return -1;
    }

    //初始化服务器
    ret = init_network(atoi(argv[1]));
    if(ret < 0){
        puts("init_server err");
        return -1;
    }
    
	signal(DATA_ARRIVE, data_handler);  //数据接收处理
    signal(CMD_ARRIVE, cmd_handler);  //命令接收处理
	while(1)
	{
		rand_data();
		sleep(1);
	}


 /*   
  	ret = start_recv();  //启动数据接收处理
    if(ret == 0){
        puts("start_recv success");
    }
	*/

    ret = wait_recv();  //等待接收

    return 0;
}

void rand_data()
{
	srand(time(NULL));
	env_msg.temperature = rand()%30;
	env_msg.humidity = rand()%20;
	env_msg.ill = rand()%1000;
	env_msg.bet = rand()%3;
	env_msg.adc = rand()%3;
	env_msg.x = rand()%5/2.0;
	env_msg.y = rand()%5/2.0;
	env_msg.z = rand()%5/2.0;
	kill(getpid(), DATA_ARRIVE);
}

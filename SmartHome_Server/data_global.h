#ifndef __DATA_GLOBAL__H__
#define __DATA_GLOBAL__H__



struct env_info
{
	uint8_t head[3];	 //标识位st:
	uint8_t type;		 //数据类型
	uint8_t snum;		 //仓库编号
	uint8_t temp[2];	 //温度	
	uint8_t hum[2];		 //湿度
	uint8_t x;			 //三轴信息
    uint8_t y;
    uint8_t z;
	uint32_t ill;		 //光照
	uint32_t bet;		 //电池电量
	uint32_t adc; 		 //电位器信息
};

//数据转换后的环境信息
struct conver_env_info {
    int snum;		 //仓库编号
    float temperature;	 //温度	
    float humidity;		 //湿度
    float ill;		 //光照
    float bet;		 //电池电量
    float adc; 		 //电位器信息
    
    signed char x;			 //三轴信息
    signed char y;			 
    signed char z;			 
};


#define DATA_ARRIVE SIGUSR1 //数据到达信号
#define CMD_ARRIVE SIGUSR2 //数据到达信号
extern struct conver_env_info env_msg;

//仓库编号
#define STORE1  0x40
#define STORE2  0x80
#define STORE3  0xc0

//设备编号
#define FAN  0x00
#define BEEP 0x10
#define LED  0x20

//自己加的小摄像头
#define CAMERA  0x30

extern uint8_t recv_cmd; 

struct control_cmd {
     uint8_t type;  //控制板端类型:A9或者MO
     uint8_t cmd;
};

#define CONTROL_CMD_LEN sizeof(struct control_cmd)

#endif

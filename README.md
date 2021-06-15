# 智能家居项目数据与接口


## 给到客户端的数据结构体
```c
	struct conver_env_info {
			int snum;						  //仓库编号
			float temperature;		 //温度
			float humidity;			    //湿度
			float ill;						   //光照
			float bet;					  //电池电量
			float adc; 				   //电位器信息
			signed char x;		 //三轴信息
			signed char y;    
			signed char z;
	};
```
**目前数据是一秒一次**

------------

## 控制命令结构体

```C
	struct control_cmd {
		 uint8_t type;	//控制板端类型，智能家居项目不用
		 uint8_t cmd;	//控制命令,位组合
	};
```

## 相关宏定义和命令码
```C
//仓库编号
#define STORE1  0x40	//仓库一
#define STORE2  0x80	//仓库二，智能家居项目用仓库二
#define STORE3  0xc0	//仓库三

//设备编号
#define FAN  0x00
#define BEEP 0x10
#define LED  0x20
#define CAMERA  0x30

```
### 命令码是用一个字节的位组合构成

**|仓库编号2bit|设备编号2bit|保留2bit|设备控制命令码2bit**

```C
<<<<<<< HEAD
enum MO_CMD {
=======
enum MO_CMD{
>>>>>>> e2c926fe33c275cdf5404b7863ba71a6f58a5a69
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
    
<<<<<<< HEAD
     // my CAMERA
=======
        // my CAMERA
>>>>>>> e2c926fe33c275cdf5404b7863ba71a6f58a5a69
    CAMERA_ON = STORE2|CAMERA|0x01,
    CAMERA_OFF = STORE2|CAMERA|0x00,
};
```



<<<<<<< HEAD


=======
>>>>>>> e2c926fe33c275cdf5404b7863ba71a6f58a5a69
>房间Code

- 0x80 → 10000000 房间2（只用）



>设备Code

- 0x00 → 00000000 风扇

- 0x10 → 00010000 蜂鸣器

- 0x20 → 00100000 LED灯

- 0x30 → 00110000 



>最终命令Code

**风扇控制**

- FAN_OFF：10000000

- FAN_1：10000001

- FAN_2：10000010

- FAN_3：10000011

---

**蜂鸣器控制**

- BEEP_OFF：10010001

- BEEP_ON：10010000


--------

**LED控制**

- LED_OFF：10100000

- LED_ON：10100001

---

**相机控制**

- CAMERA_ON：10110001
<<<<<<< HEAD
=======

>>>>>>> e2c926fe33c275cdf5404b7863ba71a6f58a5a69
- CAMERA_OFF：10110000



<<<<<<< HEAD
=======




【答辩之前的任务】
1.整合本次实训所有的代码功能到一个app中
2.在小组的app中应用一些小组内自发的设计
3.完善课堂代码中一些可改善的点
4.适当增加一些功能（力所能及！！！）
5.编译出apk安装包
（Eclipse可以直接从bin下面取出apk文件，Android Stdio一定要给apk签名！）
 https://www.jianshu.com/p/e809150fc3f4
推荐直接上架软件市场：
https://www.pgyer.com/
6.制作答辩的PPT（产品介绍、代码解析、项目展示、感谢致辞）
7.要求至少提交
  （1）项目源代码
  （2）编译的apk安装包
  （3）PPT
8.端午节后（15日或16日下午进行答辩）
答辩之前QQ上，每个组把上面要提交的资料打包发给我。
>>>>>>> e2c926fe33c275cdf5404b7863ba71a6f58a5a69

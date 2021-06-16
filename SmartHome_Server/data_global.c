#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <time.h>
#include <termios.h>
#include <signal.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <pthread.h>
#include "data_global.h"



struct conver_env_info env_msg;
uint8_t recv_cmd;
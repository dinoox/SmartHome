#include <stdio.h>
#include <stdlib.h>
#include "linklist.h"
#include <sys/select.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <unistd.h>
#include <signal.h>
#include <pthread.h>
#include <stdint.h>
#include <string.h>
#include "data_global.h"
#include "server.h"
#include "serial.h"

#define max(a,b) \
   ({ __typeof__ (a) _a = (a); \
       __typeof__ (b) _b = (b); \
     _a > _b ? _a : _b; })

static int sockfd = -1;
static int clientfd = -1;
int addrlen = sizeof(struct sockaddr_in);

struct conver_env_info data_client = {.temperature = 24,};


typedef struct {
	int fd;
	struct sockaddr_in clientaddr;
	struct list_head list;
} client_node_t;

client_node_t head;

pthread_mutex_t data_mutex;
pthread_cond_t data_cond;


void *pthread_push_client(void *arg)
{

	struct list_head * head = (struct list_head *)arg;
	int ret = -1;
	while (1)
	{
		pthread_mutex_lock(&data_mutex);
		pthread_cond_wait(&data_cond, &data_mutex);
		pthread_mutex_unlock(&data_mutex);
		// sleep(1);
		if (list_empty(head))
		{
			sleep(1);
			continue;
		}
		client_node_t *client;
		list_for_each_entry(client, head, list)
		{
			printf("send to %d %s\n",client->fd, inet_ntoa(client->clientaddr.sin_addr));
			ret = send(client->fd, &data_client, sizeof(struct conver_env_info), 0);
			if (ret < sizeof(struct conver_env_info))
			{
				printf("send data to %s fail\n", inet_ntoa(client->clientaddr.sin_addr));
			}
			else{
				printf("  OK\n");
			}

		}
	}
}

void parse_cmd(uint8_t cmd)
{
	printf("recv cmd = 0x%x\n", cmd);
    switch(cmd){
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


void *pthread_server (void *arg)
{
	pid_t pid;
	int ret, nbytes;
	int sockfd = *(int *)arg;
	int maxfd = sockfd;

	pthread_t push_thread;

	INIT_LIST_HEAD(&head.list);

	pthread_cond_init(&data_cond,NULL);
	pthread_mutex_init(&data_mutex, NULL);

	pthread_create(&push_thread, NULL, pthread_push_client, &head.list);

	struct sockaddr_in clientaddr;
	// 5接收连接--accept


	printf("wait connecting...\n");
	printf("server = %d\n", sockfd);



	fd_set readfds,tempfds;

	FD_ZERO(&readfds);
	FD_SET(sockfd, &readfds);
	struct control_cmd tmp_cmd;
	maxfd = sockfd;
	while (1)
	{
		tempfds = readfds;
		ret = select(maxfd + 1, &tempfds, NULL, NULL, NULL);
		int i = 3;
		//printf("select ret : %d\n",ret);
		for (; i <= maxfd && ret > 0;i++)
		{
			//printf("i %d\n",i);
			if (!FD_ISSET(i,&tempfds))
			{
				sleep(1);
				continue;
			}
			ret--;
			//printf("sock %d event\n",i);

			if (i == sockfd)
            {
				clientfd = accept(sockfd, (struct sockaddr *)&clientaddr, &addrlen);
                if (clientfd < 0)
                {
                    perror("accept client error :");
                    continue;
                }

				client_node_t *client = (client_node_t *)malloc(sizeof(client_node_t));
				if (!client)
				{
					perror("malloc client node fail");
					close(clientfd);
					continue;
				}

				client->fd = clientfd;
				memcpy(&(client->clientaddr), &clientaddr, addrlen);
				list_add(&(client->list), &head.list);

				maxfd = max(maxfd,clientfd);
				FD_SET(clientfd, &readfds);
            }
            else
            {
				//printf("fd : %d\n",i);
				nbytes = recv(i, &tmp_cmd, CONTROL_CMD_LEN, 0);
				if(nbytes > 0){
					recv_cmd = tmp_cmd.cmd;
					printf("recv client cmd\n");
					// raise(CMD_ARRIVE);
					parse_cmd(recv_cmd);
					continue;
				}else if(nbytes = 0){
					printf("client quit :");
					clientfd = -1;
				}else{
					

				}
				struct list_head * pos;
				struct list_head * n;
				client_node_t *client = NULL;
				list_for_each_safe(pos, n, &head.list)
				{
					//For safe delete node
					client = list_entry(pos, client_node_t, list);
					if (client->fd == i)
					{
						list_del(&client->list);
						break;
					}
				}

				 // seek second max fd, clean this fd
				if (maxfd == i)
				{
					int sec_fd = 0;
					int j = 0;
					for (; j < maxfd; j++)
					{
						if(FD_ISSET(j, &readfds))
						{
							sec_fd = j;
						}
					}
					maxfd = sec_fd;
				}

				
				close(i);
				FD_CLR(i, &readfds);
				if(client != NULL)
				{
					struct in_addr a;
					printf("client [%s] exit. \n",inet_ntoa(client->clientaddr.sin_addr));
					free(client);
				}


            }
		}


	}

}


int init_network(uint16_t port)
{
	printf("prot = %d\n", port);
	struct sockaddr_in addr;
	int ret, nbytes;
	struct sockaddr_in clientaddr;
	pthread_t pid;

	// 1创建一个套接字--socket
	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if(sockfd < 0) {
		perror("socket err");
		return -1;
	}
	int opt = 1;
	//setsockopt(sockfd, SOL_SOCKET, SO_REUSEPORT, &opt, sizeof(opt));
	setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

	// 2定义套接字地址--sockaddr_in
	bzero(&addr, addrlen);
	addr.sin_family = AF_INET;
	addr.sin_addr.s_addr = INADDR_ANY;
	addr.sin_port = htons(port);

	// 3绑定套接字--bind
	if(bind(sockfd, (struct sockaddr *)&addr, addrlen) < 0) {
		perror("bind err");
		return -1;
	}

	// 4启动监听--listen
	if(listen(sockfd, 5) < 0) {
		perror("listen err");
		return -1;
	}

	return pthread_create(&pid, NULL, pthread_server, &sockfd);
}


/**
 * @brief 发送给客户端
 * @return 发送成功字节数 -1发送失败或者无客户端
 */
int send_data(struct conver_env_info *data)
{

	if (list_empty(&head.list))
	{
		printf("no client connect\n");
		return -1;
	}
	else{
		data_client = *data;
		return pthread_cond_signal(&data_cond);
	}
}

//
//  mtserver.c
//  ass1
//
//  Created by William You on 1/18/2014.
//  Copyright (c) 2014 William You. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <netdb.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <pthread.h>
#include <time.h>

#define BUF_SIZE 50

int conn = 0;
pthread_mutex_t lock;

void *child_labour(void *arg) {
    pthread_mutex_lock(&lock);
    conn++;
    pthread_mutex_unlock(&lock);
    
    int fd = *(int*)arg;
    fd_set fd_s;
    FD_ZERO(&fd_s);
    FD_SET(fd, &fd_s);

    printf("[%i] Created new connection\n", fd);

    int res = -1;
    int off = 0;
    int err = 0;
    int total = 0;
    int summing = 0;
    int exit = 0;
    int len;
    char *buf;
    char *left_over = NULL;
    int left_over_len = 0;
    struct timeval tv;

    while(exit == 0) {
        buf = (char*)malloc(sizeof(char) * BUF_SIZE);
        memset(buf, 0, BUF_SIZE);
        if (left_over != NULL) {
            strncpy(buf, left_over, left_over_len);
            free(left_over);
            left_over = NULL;
        }
        
        tv.tv_sec = 5;
        int sel = select(FD_SETSIZE, &fd_s, NULL, NULL, &tv);
        
        if (sel == 0) {
            printf("[%i] Connection timeout\n", fd);
            exit = 1;
        }
        else if (sel > 0) {
            len = (int)recv(fd, buf + left_over_len, BUF_SIZE - left_over_len, 0);
            len += left_over_len;
            left_over_len = 0;
            
            off = 0;
            while (off < len && exit == 0) {
                char c = *(buf + off);
                if (summing == 1 || atoi(&c) != 0 || c == '0') {
                    err = 0;
                    if (atoi(&c) != 0 || c == '0') {
                        total += atoi(&c);
                        off += 1;
                        c = *(buf + off);
                        while (off < len && (atoi(&c) != 0 || c == '0')) {
                            total += atoi(&c);
                            off += 1;
                            c = *(buf + off);
                        }
                        
                        if (off == len) { // This means the buffer ends with number, wait for next receive
                            summing = 1;
                            printf("[%i] Waiting for more numbers\n", fd);
                            continue;
                        }
                        else {
                            summing = 0;
                            res = total;
                            total = 0;
                            printf("[%i] Sum: %i\n", fd, res);
                        }
                    }
                    else { // Waiting and not getting number, do the sum
                        summing = 0;
                        res = total;
                        total = 0;
                        printf("[%i] Sum: %i\n", fd, res);
                    }
                }
                else {
                    if (strncmp((buf + off), "uptime", 6) == 0) {
                        res = (int)time(NULL);
                        printf("[%i] Uptime: %i\n", fd, res);
                        off += 6;
                        err = 0;
                    }
                    else if (strncmp((buf + off), "load", 4) == 0) {
                        pthread_mutex_lock(&lock);
                        res = conn;
                        printf("[%i] Load: %i\n", fd, res);
                        off += 4;
                        err = 0;
                        pthread_mutex_unlock(&lock);
                    }
                    else if (strncmp((buf + off), "exit", 4) == 0) {
                        res = 0;
                        printf("[%i] Exit\n", fd);
                        off += 4;
                        err = 0;
                        exit = 1;
                    }
                    else {
                        res = -1;
                        
                        if ((len - off) < 6) {
                            int left = len - off;
                            if (strncmp((buf + off), "uptime", left) == 0
                                || strncmp((buf + off), "load", left) == 0
                                || strncmp((buf + off), "exit", left) == 0) {
                                left_over = (char*)malloc(sizeof(char) * left);
                                strncpy(left_over, (buf + off), left);
                                left_over_len = left;
                                
                                printf("[%i] Waiting for partial request\n", fd);
                                break;
                            }
                        }
                        
                        err++;
                        printf("[%i] Invalid request\n", fd);
                        off += 1;
                    }
                }

                write(fd, &res, sizeof(int));
                if (err >= 2) break;
            }
        }

        free(buf);
        if (err >= 2) break;
    }

    printf("[%i] Closing connection\n", fd);

    pthread_mutex_lock(&lock);
    conn--;
    pthread_mutex_unlock(&lock);

    close(fd);
    pthread_exit(NULL);
}


int main(int argc, const char * argv[])
{
    int maxConn, portNum, res, sockfd;
    const char *port;
    struct addrinfo hints, *servinfo, *p;
    struct sockaddr_storage conn_addr;
    char s[INET6_ADDRSTRLEN];
    socklen_t sin_size;
    pthread_t t;
    int yes = 1;
    
    if (argc != 3) {
        printf("The server takes exactly 2 parameters, max number of clients and port number\n");
        return -1;
    }
    
    maxConn = atoi(argv[1]);
    if (maxConn == 0) {
        printf("Invalid max number of connections\n");
        return -1;
    }
    port = argv[2];
    portNum = atoi(port);
    if (portNum == 0) {
        printf("Invalid port number\n");
        return -1;
    }

    // Setting up mutex lock
    res = pthread_mutex_init(&lock, NULL);
    if (res < 0) {
        printf("Error creating mutex lock\n");
        return res;
    }
    
    memset(&hints, 0, sizeof(hints));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;
    res = getaddrinfo(NULL, port, &hints, &servinfo);
    if (res != 0) {
        printf("Error in getaddrinfo\n");
        return res;
    }
    for (p=servinfo; p!=NULL; p=p->ai_next) {
        if ((sockfd = socket(p->ai_family, p->ai_socktype,
                             p->ai_protocol)) == -1) {
			printf("Error in creating socket, try next\n");
			continue;
		}
        
		if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes,
                       sizeof(int)) == -1) {
			printf("Error in setsockopt\n");
			exit(1);
		}
        
		if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
			close(sockfd);
			printf("Error in bind, try next\n");
			continue;
		}
        
		break;
    }
    if (p == NULL) {
        printf("Can not bind a socket\n");
        return -1;
    }
    free(servinfo);
    
    res = listen(sockfd, 5);
    if (res < 0) {
        printf("Error in listen\n");
        return res;
    }
    
    printf("Server running: waiting for connections...\n");
    
    while (1) {
        sin_size = sizeof(conn_addr);
        
        int new_fd = accept(sockfd, (struct sockaddr*)&conn_addr, &sin_size);
        if (new_fd < 0) {
            printf("Error accpeting new connection\n");
            continue;
        }
        
        pthread_mutex_lock(&lock);
        if (conn >= maxConn) {
            printf("Reached maximum number of connections\n");
            close(new_fd);
            pthread_mutex_unlock(&lock);
            continue;
        }
        pthread_mutex_unlock(&lock);
        
        void *addr;
        if (((struct sockaddr *)&conn_addr)->sa_family == AF_INET) {
            addr = &((struct sockaddr_in *)&conn_addr)->sin_addr;
        }
        else {
            addr = &((struct sockaddr_in6 *)&conn_addr)->sin6_addr;
        }
        
        inet_ntop(conn_addr.ss_family, addr, s, sizeof(s));
        printf("Got connection from %s\n", s);
        
        
        res = pthread_create(&t, NULL, child_labour, &new_fd);
    }
    
    return 0;
}


/**
 * Socket program used to simulate http service
 *
 * Created by Wei (William) You
 * Student number: 77610095
 * CS id: r9e7
 *
 */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>
#include <time.h>

// Three response code used
#define OK_RESPONSE_CODE " 200 OK\r\n"
#define FOUND_RESPONSE_CODE " 302 Found\r\n"
#define FORBIDDEN_RESPONSE_CODE " 403 Forbidden\r\n"
#define NOT_FOUND_RESPONSE_CODE " 404 Not Found\r\n"
#define NOT_IMPLEMENTED_RESPONSE_CODE " 501 Not Implemented\r\n"

#define END_RECV "\r\n\r\n"

#define BACKLOG 10	 // how many pending connections queue will hold

void sigchld_handler(int s)
{
	while(waitpid(-1, NULL, WNOHANG) > 0);
}

// get sockaddr, IPv4 or IPv6:
void *get_in_addr(struct sockaddr *sa)
{
	if (sa->sa_family == AF_INET) {
		return &(((struct sockaddr_in*)sa)->sin_addr);
	}
    
	return &(((struct sockaddr_in6*)sa)->sin6_addr);
}

// Send the send_buffer to fd
void send_data(int fd,char* send_buffer,int total_byte)
{
    int sent = 0;
    int sending = 0;
    
    // Send until all data is sent
    while (sent < total_byte){
        if ((sending = (int)send(fd, &send_buffer[sent], (total_byte-sent), 0)) < 0)
            perror("send");
        sent += sending;
    }
}

// Construct part of the header based on http version, response code, and time
void constrcut_header(char* send_buffer,char* http_version,char* response_code,char* time_string)
{
    
    strcat(send_buffer, http_version);
    strcat(send_buffer, response_code);
    strcat(send_buffer, "Date: ");
    strcat(send_buffer, time_string);
    
}


int main (int argc, const char * argv[]) {
    
    // Size of buffers
    int buf_size = 2048;
    
	int sockfd, new_fd;  // listen on sock_fd, new connection on new_fd
	struct addrinfo hints, *servinfo, *p;
	struct sockaddr_storage their_addr; // connector's address information
	socklen_t sin_size;
	struct sigaction sa;
	int yes=1;
	char s[INET6_ADDRSTRLEN];
	int rv;
    
	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE; // use my IP
    
    /* Check arguments */
    if (argc != 2) {
        fprintf(stderr, "Invalid arguments. You should provide a single argument.\n");
        return 1;
    }
    
    const char* port = argv[1];
    
	if ((rv = getaddrinfo(NULL, port, &hints, &servinfo)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return 1;
	}
    
	// loop through all the results and bind to the first we can
	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
                             p->ai_protocol)) == -1) {
			perror("server: socket");
			continue;
		}
        
		if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes,
                       sizeof(int)) == -1) {
			perror("setsockopt");
			exit(1);
		}
        
		if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
			close(sockfd);
			perror("server: bind");
			continue;
		}
        
		break;
	}
    
	if (p == NULL)  {
		fprintf(stderr, "server: failed to bind\n");
		return 2;
	}
    
	freeaddrinfo(servinfo); // all done with this structure
    
	if (listen(sockfd, BACKLOG) == -1) {
		perror("listen");
		exit(1);
	}
    
	sa.sa_handler = sigchld_handler; // reap all dead processes
	sigemptyset(&sa.sa_mask);
	sa.sa_flags = SA_RESTART;
	if (sigaction(SIGCHLD, &sa, NULL) == -1) {
		perror("sigaction");
		exit(1);
	}
    
	printf("server: waiting for connections...\n");
    
	while(1) {  // main accept() loop
        
		sin_size = sizeof their_addr;
        
		new_fd = accept(sockfd, (struct sockaddr *)&their_addr, &sin_size);
		if (new_fd == -1) {
			perror("accept");
			continue;
		}
        
		inet_ntop(their_addr.ss_family,
                  get_in_addr((struct sockaddr *)&their_addr),
                  s, sizeof s);
		printf("server: got connection from %s\n", s);
        
        if (!fork()) { // this is the child process
            
            while (1){
                
                char* recv_buffer = (char*) (malloc(buf_size * sizeof(char)));
                
                int temp_count;
                int recv_count = 0;
                
                
                // Keep receiving until end of request is detected
                while (strstr(recv_buffer, END_RECV)==NULL){
                    char* temp = (char*) (malloc(buf_size * sizeof(char)));
                    if ((temp_count = (int)recv(new_fd, temp,buf_size, 0)) < 0)
                        break;
                    recv_count += temp_count;
                    
                    if (recv_count > buf_size){
                        buf_size *= 2;
                        char* expand = (char*) (malloc(buf_size*sizeof(char)));
                        strcpy(expand, recv_buffer);
                        char* to_free = recv_buffer;
                        recv_buffer = expand;
                        free(to_free);
                    }
                    
                    strcat(recv_buffer, temp);
                }
                
                if (temp_count < 0){
                    perror("receive");
                    continue;
                }

                char* rest;
                recv_buffer = strtok_r(recv_buffer, " ", &rest);
                
                // Get and store the method
                char* method = (char*) (malloc(strlen(recv_buffer) * sizeof(char)));
                strcpy(method,recv_buffer);
                
                // The Sending buffer.
                char* send_buffer = (char*) (malloc(buf_size * sizeof(char)));
                
                // Get and store the command
                recv_buffer = strtok_r(rest, " ", &rest);
                char* command = (char*) (malloc(strlen(recv_buffer) * sizeof(char)));
                strcpy(command, recv_buffer);
                
                // Get the current time
                char* time_string = (char*) (malloc(32 * sizeof(char)));
                time_t* current_time = (time_t*) (malloc(sizeof(time_t)));
                time(current_time);
                struct tm* time = localtime(current_time);
                strftime(time_string, 100, "%a, %d %b %Y %H:%M:%S %Z\r\n", time);
                
                // Get the http version
                recv_buffer = strtok_r(rest, "\r\n", &rest);
                char* http_version = (char*) (malloc(strlen(recv_buffer) * sizeof(char)));
                strcpy(http_version,recv_buffer);
                
                
                // The content of the response
                char* content;

                // Length of content in string
                char* content_length = (char*) (malloc(4 * sizeof(char)));
                
                
                
                // Method beside GET is received
                if (strstr(method, "GET")==NULL){
                    
                    constrcut_header(send_buffer,http_version,NOT_IMPLEMENTED_RESPONSE_CODE,time_string);
                    
                    strcat(send_buffer, "Content-type: text/plain\r\n");
                    strcat(send_buffer, "Connection: close\r\n");
                    strcat(send_buffer, "Cache-Control: public\r\n");
                    
                    content = "Method is not implemented.\r\n\r\nConnection closing...\r\n";
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer,content);
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                    
                    close(new_fd);
                    
                    free(send_buffer);
                    free(http_version);
                    free(method);
                    free(command);
                    free(time_string);
                    free(current_time);
                    free(content_length);
                    free(content);
                    
                    exit(0);
                    
                }
                
                // About
                if ((strncmp(command, "/about", strlen("/about")))==0){
                    
                    constrcut_header(send_buffer, http_version, OK_RESPONSE_CODE,time_string);
                    
                    strcat(send_buffer, "Content-type: text/plain\r\n");
                    strcat(send_buffer, "Connection: keep-alive\r\n");
                    strcat(send_buffer, "Cache-Control: public\r\n");
                    
                    content = "Hi,\nMy name is Wei (William) You.\nStudent number: 77610095.\r\nComputer Networking is awesome!!\r\n";
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer, content);
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                    
                }
                
                // Time
                else if ((strncmp(command, "/time", strlen("/time")))==0){
                    
                    constrcut_header(send_buffer, http_version, OK_RESPONSE_CODE,time_string);
                    
                    strcat(send_buffer, "Content-type: text/plain\r\n");
                    strcat(send_buffer, "Connection: keep-alive\r\n");
                    strcat(send_buffer, "Cache-Control: no-cache\r\n");
                    
                    content = time_string;
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer, content);
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                    
                }
                
                // Print
                else if ((strncmp(command, "/print", strlen("/print")))==0){
                    
                    constrcut_header(send_buffer, http_version, OK_RESPONSE_CODE,time_string);
                    
                    strcat(send_buffer, "Content-type: text/plain\r\n");
                    strcat(send_buffer, "Connection: keep-alive\r\n");
                    strcat(send_buffer, "Cache-Control: public\r\n");
                    
                    strtok_r(command,"/",&command);
                    
                    // Print nothing if no parameter is provided
                    if ((content = strtok_r(command,"\r\n",&command))==NULL)
                        content = "";
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer, content);
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                    
                }
                
                // Browser
                else if ((strncmp(command, "/browser", strlen("/browser")))==0){
                    
                    constrcut_header(send_buffer, http_version, OK_RESPONSE_CODE,time_string);
                    
                    strcat(send_buffer, "Content-type: text/plain\r\n");
                    strcat(send_buffer, "Connection: keep-alive\r\n");
                    strcat(send_buffer, "Cache-Control: private\r\n");
                    
                    char* user_agent;
                    if ((user_agent = strstr(rest,"User-Agent"))!=NULL){
                        user_agent = strtok_r(user_agent,"\r\n",&rest);
                        content = (char*) (malloc(strlen(user_agent) * sizeof(char)));
                        strcpy(content,user_agent);
                    }
                    else
                        user_agent = "";
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer, content);
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                    
                }
                
                // UBC
                else if ((strncmp(command, "/ubc", strlen("/ubc")))==0){
                    
                    constrcut_header(send_buffer, http_version, OK_RESPONSE_CODE,time_string);
                    
                    strcat(send_buffer, "Content-type: text/html\r\n");
                    strcat(send_buffer, "Connection: keep-alive\r\n");
                    strcat(send_buffer, "Cache-Control: public\r\n");
                    
                    content = "<html><body><img src=\"http://www.ubc.ca/_ubc_clf/img/footer/logo.gif\"/></body></html>\r\n";
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer, content);
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                    
                }
                
                // Cookie
                else if ((strncmp(command, "/cookie", strlen("/cookie")))==0){
                    
                    constrcut_header(send_buffer, http_version, OK_RESPONSE_CODE,time_string);
                    
                    strcat(send_buffer, "Content-type: text/plain\r\n");
                    strcat(send_buffer, "Connection: keep-alive\r\n");
                    strcat(send_buffer, "Cache-Control: no-cache\r\n");
                    
                    char* myCookie;
                    strtok_r(command,"/",&command);
                    strcat(send_buffer, "Set-Cookie: mycookie=");
                    
                    // Set new value for mycookie, last for 24 hours
                    if ((myCookie = strtok_r(command,"\r\n",&command))!=NULL){
                        strcat(send_buffer,myCookie);
                        strcat(send_buffer,";");
                        strcat(send_buffer,"Max-Age=86400;Path=/\r\n");
                    }
                    // If no parameter, delete mycookie
                    else{
                        strcat(send_buffer,"");
                        strcat(send_buffer,";");
                        strcat(send_buffer,"Max-Age=0;Path=/\r\n");
                    }
                    
                    char* cookie_val;
                    if ((cookie_val = strstr(rest,"mycookie"))!=NULL){
                        cookie_val = strtok_r(cookie_val,";\r\n",&rest);
                        
                        content = (char*) (malloc(buf_size*sizeof(char)));
                        
                        strcat(content, "Previously, ");
                        strcat(content, cookie_val);
                        strcat(content, "\r\n");
                    }
                    else
                        content = "No previous mycookie value.\r\n";
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer, content);
                    
                    send_data(new_fd, send_buffer,(int)strlen(send_buffer));
                    
                }
                
                // Favicon.ico
                else if ((strncmp(command, "/favicon.ico", strlen("/favicon.ico")))==0){
                    
                    constrcut_header(send_buffer, http_version, OK_RESPONSE_CODE,time_string);
                    
                    strcat(send_buffer, "Content-type: image/x-icon\r\n");
                    strcat(send_buffer, "Connection: keep-alive\r\n");
                    strcat(send_buffer, "Cache-Control: public\r\n");
                    
                    FILE* file = fopen("favicon.ico","r");
                    if (file==NULL){
                        perror("favicon.ico");
                        exit(1);
                    }
                    
                    // File is 1150 bytes
                    int data_size = 1150;
                    content = (char*) (malloc(data_size * sizeof(char)));
                    
                    // Read the complete file
                    while (feof(file)==0) {
                        fread(content,data_size,1,file);
                    }
                    
                    sprintf(content_length, "%d",data_size);
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                    send_data(new_fd,content,data_size);
                    
                    fclose(file);
                    
                }
                
                // Redirect
                else if ((strncmp(command, "/redirect", strlen("/redirect")))==0){
                    
                    char* addr;
                    strtok_r(command,"/",&command);
                    
                    content = (char*) (malloc(buf_size*sizeof(char)));
                    
                    if ((addr = strtok_r(command,"\r\n",&command))==NULL){
                        constrcut_header(send_buffer, http_version, FORBIDDEN_RESPONSE_CODE,time_string);
                        strcpy(content,"<html><body>Must provide a parameter</body></html>\r\n");
                    }
                    else {
                        constrcut_header(send_buffer, http_version, FOUND_RESPONSE_CODE,time_string);
                        sprintf(content, "<html><body><a href=\"%s\">Redirecting...</a></body></html>\r\n",addr);
                        
                        strcat(send_buffer, "Location: ");
                        strcat(send_buffer, addr);
                        strcat(send_buffer, "\r\n");
                    }
                    
                    strcat(send_buffer, "Content-type: text/html\r\n");
                    strcat(send_buffer, "Connection: keep-alive\r\n");
                    strcat(send_buffer, "Cache-Control: no-cache\r\n");
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer, content);
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                    
                }
                
                // Google
                else if ((strncmp(command, "/google", strlen("/google")))==0){
                    
                    char* search;
                    strtok_r(command,"/",&command);
                    
                    content = (char*) (malloc(buf_size*sizeof(char)));
                    
                    if ((search = strtok_r(command,"\r\n",&command))==NULL){
                        constrcut_header(send_buffer, http_version, FORBIDDEN_RESPONSE_CODE,time_string);
                        strcpy(content,"<html><body>Must provide a parameter</body></html>\r\n");
                    }
                    else {
                        constrcut_header(send_buffer, http_version, FOUND_RESPONSE_CODE,time_string);
                        sprintf(content, "<html><body><a href=\"http://www.google.com/search?as_q=%s\">Redirecting...</a></body></html>\r\n",search);
                        
                        strcat(send_buffer, "Location: http://www.google.com/search?as_q=");
                        strcat(send_buffer, search);
                        strcat(send_buffer, "\r\n");
                    }
                    
                    strcat(send_buffer, "Content-type: text/html\r\n");
                    strcat(send_buffer, "Connection: keep-alive\r\n");
                    strcat(send_buffer, "Cache-Control: no-cache\r\n");
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer, content);
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                                        
                }
                
                // Close
                else if ((strncmp(command, "/close", strlen("/close")))==0){
                    
                    constrcut_header(send_buffer, http_version, OK_RESPONSE_CODE,time_string);
                    
                    strcat(send_buffer, "Content-type: text/plain\r\n");
                    strcat(send_buffer, "Connection: close\r\n");
                    strcat(send_buffer, "Cache-Control: public\r\n");
                    
                    content = (char*) (malloc(buf_size*sizeof(char)));
                    
                    char* cookie_val;
                    if ((cookie_val = strstr(rest,"Cookie"))!=NULL){
                        cookie_val = strtok_r(cookie_val,"\r\n",&rest);
                        
                        strcat(content, cookie_val);
                        strcat(content, "\r\n\r\n");
                    }
                    else
                        strcat(content,"No cookie.\r\n\r\n");
                    
                    strcat(content,"Connection closing...\r\n");
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer, content);
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                    
                    close(new_fd);
                    
                    free(send_buffer);
                    free(http_version);
                    free(method);
                    free(command);
                    free(time_string);
                    free(current_time);
                    free(content_length);
                    free(content);
                    
                    exit(0);
                }
                
                // False case
                else {
                    constrcut_header(send_buffer,http_version,NOT_FOUND_RESPONSE_CODE,time_string);
                    
                    strcat(send_buffer, "Content-type: text/plain\r\n");
                    strcat(send_buffer, "Connection: keep-alive\r\n");
                    strcat(send_buffer, "Cache-Control: public\r\n");
                    
                    content = "Invalid command. You may use:\r\n/close - Close the connection\ry\n/about - Author info\r\n/time - current time\r\n/print - print the parameter\r\n/browser - browser identification\r\n/ubc - logo of ubc\r\n/cookie - set mycookie value to be the parameter\r\n/favicon.ico - binary data of CS icon\r\n/redirect - redirect to parameter url\r\n/google - redirect to google search result of parameter\r\n";
                    
                    sprintf(content_length, "%d",(int)strlen(content));
                    
                    strcat(send_buffer,"Content-Length: ");
                    strcat(send_buffer, content_length);
                    strcat(send_buffer, "\r\n\r\n");
                    
                    strcat(send_buffer,content);
                    
                    send_data(new_fd,send_buffer,(int)strlen(send_buffer));
                    
                }
            }
        }
    }
    
	return 0;
}


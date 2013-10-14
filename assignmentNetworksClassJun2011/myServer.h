#ifndef _MYSERVER_H_
#define _MYSERVER_H_

#include <stdio.h>
#include "myList.h"

// socket files
#include <sys/socket.h>

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 

struct myServer {
    int port, sockfd;
    struct sockaddr_in serv_addr, cli_addr;

    // master list of nodes
    struct myList * master_list;

    int num_clients;

};

void initServer(int argc, char ** argv);
void printServerList();

void serverUploadNode(const char * message, int client_id);
void serverDeleteNode(char * message, int client_id);
void serverSynch(int client_id);

void serverUpdateNode(int update, char * message, int client_id);

#endif /* _MYSERVER_H_ */


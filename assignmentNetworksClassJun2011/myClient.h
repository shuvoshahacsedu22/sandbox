#ifndef _MYCLIENT_H_
#define _MYCLIENT_H_

#include <stdio.h>
#include "myList.h"

#include <sys/socket.h>
#include <netdb.h> 
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 

struct myClient {
    int client_id, sockfd;
    struct hostent *server;
    char * client_string;
    struct myList * nodes;

};

void initClient(int argc, char ** argv);
void clientUploadNode(char * message);
void clientDeleteNode(char * message);
void clientSynch();
void printClientList();

/* Helper methods */
int messageExceedsLength(char * message);
void clientSynchNode(int client_id, char * message);
int clientHasElement(int client_id, char * message);
void clientSetId();
void sendMessageToServer(char * cmd, char * message);
void receiveMessageFromServer();

#endif /* _MYCLIENT_H_ */

#include "myServer.h"
#include "global.h"

// global variables
struct myServer * global_server;
socklen_t fromlen = sizeof(struct sockaddr_in);
int sockfd;
struct sockaddr_in from;

// Common code for sending a reponse message to the client
void sendResponseToClient(char * message){
    int n = sendto(sockfd,message,strlen(message),
                   0,(struct sockaddr *)&from,fromlen);
    if (n < 0){ 
        printf("Error: could not write to socket\n"); 
    }
}

// Have the server generate a new client id for the client
void serverSupplyClientId(){
    global_server->num_clients += 1;
    char msg[1]; 
    sprintf(msg, "%d", global_server->num_clients);
    sendResponseToClient(msg);
}

void initServer(int argc, char ** argv){

    // error handling to catch when port isn't specified
    if (argc != 2 ) {
        printf("Error: you must specify a port\n");
        exit(1); // exit on failure
    }

    global_server = malloc(sizeof(struct myServer));
    global_server->port = atoi(argv[1]);
    global_server->master_list = NULL;
    global_server->num_clients = 0;

    // 1. Create the socket
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        printf("Error: could not open socket for server\n");
        exit(1);
    }
    // 2. Bind the socket to an address 
    global_server->serv_addr.sin_family = AF_INET;
    global_server->serv_addr.sin_addr.s_addr = INADDR_ANY;
    global_server->serv_addr.sin_port = htons(global_server->port);
    if (bind(sockfd, (struct sockaddr *) &global_server->serv_addr,
             sizeof(global_server->serv_addr)) < 0)  {
             printf("Error: could not bind\n");
    }

    // enter infinite loop of servicing clients
    char buffer[1024];
    int count = 1;
    while (1) {
        bzero(buffer,256);
        int n = recvfrom(sockfd,buffer,1024,0,(struct sockaddr *)&from,&fromlen);
        if (n < 0){ 
            printf("ERROR reading from socket\n");
        }
        int delim = getSecondIndexOf(buffer, '|'); 

        char *msg = (char*) malloc(160);
        strncpy(msg, buffer + delim + 1,strlen(buffer) - delim);
        int id;
        switch(buffer[0]){
            case 'u':
	        id = extractClientID(buffer); 
                serverUploadNode(msg, id); // change to real client_id
                break;
            case 'd':
	        id = extractClientID(buffer); 
                serverDeleteNode(msg, id); // change to real client_id
                break;
            case 's':
	        id = extractClientID(buffer); 
                serverSynch(id); // change to real client_id
                break;
            case 'i':
                serverSupplyClientId(); // change to real client_id
                break;
        }
        count += 1;
    }

    close(sockfd);
    return;

}

void serverUploadNode(const char * message, int client_id){
    if (addToList(&global_server->master_list, client_id, message) == 1) {
        sendResponseToClient(SUCCESS_MSG);
    } else {
        sendResponseToClient(UPL_FAIL_MSG);
    }
    printServerList(); // print the list in the end to see the updates
}

void serverDeleteNode(char * message, int client_id){
    if (removeFromList(&global_server->master_list, client_id, message) == 1) {
        printServerList(); // print the list in the end to see the updates
        sendResponseToClient(SUCCESS_MSG);
    } else {
        sendResponseToClient(DEL_FAIL_MSG);
    }
}

/* update tells the server to do upload (if 0) or delete (otherwise) */
void serverUpdateNode(int update, char * message, int client_id){
    if (update == 0) { // update = 0 => add to the server
        serverUploadNode(message, client_id);
    } else if (update == 1) {
        serverDeleteNode(message, client_id);
    } else {
        serverSynch(client_id);
    }
}

void serverSynch(int client_id){
    struct myList * synch_list = NULL;
    int total = extractOtherClients(global_server->master_list, client_id, &synch_list);

    int i;
    char m_send[160]; // won't be more than 150 + delimiter + client_id
    char id_str[1];
    for (i = 1; i <= total; i += 1) {
        bzero(m_send,160); // clear each time
        constructMessage(m_send,"s", synch_list->client_id, synch_list->message);
        sendResponseToClient(m_send);
        synch_list = synch_list->next;
    }
    sendResponseToClient(SUCCESS_MSG);
}

void printServerList(){
    printList(global_server->master_list);
}


#include "myClient.h"
#include "global.h"
#include <stdlib.h>

// global variables
struct myClient * global_client;
int sockfd;
struct sockaddr_in server, from;
unsigned int length;
char buffer[256];

void initClient(int argc, char ** argv){

    // error handling to catch when the ip and port aren't specified
    if (argc != 3 ) {
        printf("Error: you must specify (1) an IP address/DNS name and (2) a port number\n");
        exit(1); // exit on failure
    }

    global_client = malloc(sizeof(struct myClient));
    if (global_client == NULL) {
        printf("Error: unable to create a new client, exiting\n");
        exit(1);  // exit on failure
    }

    /* set the client_id to -1 so that our first message doesn't try to get the value*/
    global_client->client_id = -1;
    global_client->nodes = NULL;

    int n;
    struct sockaddr_in serv_addr;

    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0) 
        printf("ERROR opening socket");
    server.sin_family = AF_INET;
    struct hostent *hp;
    hp = gethostbyname(argv[1]);
    if (hp == NULL) {
        fprintf(stderr,"ERROR, no such host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)hp->h_addr, 
         (char *)&server.sin_addr,
          hp->h_length);
    server.sin_port = htons(atoi(argv[2]));
    length=sizeof(struct sockaddr_in);

    clientSetId();
}

void clientSetId(){
    sendMessageToServer("i", "empty"); 
    global_client->client_id = atoi(buffer);
}

int messageExceedsLength(char * message){
    int ret = 0; // initialize to false 
    int i;
    for (i = 0;  message[i]; i ++){
        // no-op... traversing through message 
        // to get the length
    }
    if (i > MAX_MSG_LENGTH) {
        ret = 1;  // set to false since max length has been exceeded
    }
    return ret;
}

// helper method to send a message (with cmd and client_id prepended)
// to the server
void sendMessageToServer(char * cmd, char * message){
    // get the length of the message once as we use it multiple times
    int m_length = strlen(message);

    // first make sure that there are only MAX_MSG_LENGTH characters
    if (m_length > MAX_MSG_LENGTH) {
        printf("Failed to upload node\n");
        return;
    }

    char m_send[160]; // won't be more than 150 + delimiter + client_id
    bzero(m_send,160); // clear each time
    strcat(m_send, cmd);
    int id = 0;
    if (global_client->client_id != -1) {
        id = global_client->client_id;
    }
    constructMessage(m_send, cmd, id, message);

    bzero(buffer,256);
    int n = sendto(sockfd,m_send,
            strlen(m_send),0,(const struct sockaddr *)&server,length);
    if (n < 0){
        printf("ERROR writing to socket");
        exit(1);
    }
    receiveMessageFromServer();
}

void receiveMessageFromServer(){
    bzero(buffer,256);
    int n = recvfrom(sockfd,buffer,256,0,(struct sockaddr *)&from, &length);
    if (n < 0) 
        printf("ERROR reading from socket");
    if ((strcmp(buffer,DEL_FAIL_MSG) == 0) || (strcmp(buffer,UPL_FAIL_MSG) == 0)) {
        printf("%s\n",buffer); // DO NOT DELETE THIS LINE!
    }
}

void clientUploadNode(char * message){
    // send message to the server
    sendMessageToServer("u", message);
}

void clientDeleteNode(char * message){
    // delete the node from the server
    sendMessageToServer("d", message);
}

void clientSynch(){
    // synch the client to the server 
    sendMessageToServer("s", "");
    int id;

    // keep receiving messages until we get a SUCCESS_MSG
    while (strcmp(buffer, SUCCESS_MSG) != 0){
        // obtain the msg first and then extract the client id
        char *msg = (char*) malloc(160);
        id = getClientIDAndMessage(buffer, msg);
        clientSynchNode(id, msg);
        receiveMessageFromServer(); // get another message 
    }
}

void clientSynchNode(int client_id, char * message){
    if (clientHasElement(client_id, message) != 1 ) {
        // add the node since it doesn't exist yet;
        addToList(&global_client->nodes, client_id, message);        
    }
}

int clientHasElement(int client_id, char * message){
    return listHasElement(global_client->nodes, client_id, message); 
}

void printClientList(){
    printList(global_client->nodes);
}

#include "myClient.h"

int main(int argc, char ** argv) {
    // other random test case program
    initClient(argc, argv);
    char data1[] = {"This is a message1"};
    clientUploadNode(data1);
    char data2[] = {"This is a message2"};
    clientUploadNode(data2);
    clientSynch();
    printClientList();
    return 1;
}

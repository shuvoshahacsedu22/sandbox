#include "myClient.h"

int main(int argc, char ** argv) {
    // Random test case program
    initClient(argc, argv);
    char remove[] = {"This node will be deleted!"};
    char data1[] = {"This is a message1"}; // other's data

    clientUploadNode("Adding a new node to the server");
    clientUploadNode(remove);
    clientUploadNode("blah blah blah blah blah");
    clientUploadNode("Hellloooooooooo");
    clientUploadNode("CS454 Distributed systems");
    clientUploadNode("Random stings of fun :)");

    // Test failure to upload node
    printf("\nTesting failure to upload large message\n");
    clientUploadNode("ATTHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREETHREEHREE");

    // Test failure to delete node other node
    printf("\nTesting deleting node that doesn't exist\n");
    clientDeleteNode("MainClient2 - node added one");

    printf("\nTesting deleting other's node\n");
    clientDeleteNode(data1); // (MainClient 2 should be run before this

    printf("\nTesting deleting own node\n");
    clientDeleteNode(remove);

    printf("Going to Synch\n"); 
    printClientList();
    clientSynch();
    printClientList();

    return 1;
}

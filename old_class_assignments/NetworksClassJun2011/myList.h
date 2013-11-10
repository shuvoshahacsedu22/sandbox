#ifndef _MYLIST_H_
#define _MYLIST_H_

#include <stdio.h>
#include "myList.h"

#define MAX_MSG_LENGTH 150

struct myList {
    int client_id;
    char message[256];
    struct myList * next;

};

int addToList(struct myList ** list, int client_id, char * message);
int removeFromList(struct myList ** list, int client_id, const char * message);
int listHasElement(struct myList * list, int client_id, char * message);
int extractOtherClients(struct myList * list, int client_id, 
                        struct myList ** synch_list);
void printList(struct myList * list);

#endif /* _MYLIST_H_ */


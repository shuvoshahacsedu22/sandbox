#include "myList.h"

int extractOtherClients(struct myList * list, int client_id, 
                        struct myList ** synch_list){
    int count = 0;
    struct myList * curr = list;
    if (curr == NULL){
        printf("Cannot synch. no nodes!\n"); // print empty list
    }

    while (curr != NULL){ 
        if (curr->client_id != client_id){
            addToList(synch_list, curr->client_id, curr->message);
            count += 1;
        }
        curr = curr->next;
    }   
    return count;
}

int listHasElement(struct myList * list, int client_id, char * message) {
    struct myList * curr = list;

    while (curr != NULL) {
        // check the id's first, and then the strings to save time if it's a match
        if ( (curr->client_id==client_id) && (strcmp(curr->message, message)==0)) {
            return 1;
            prinf("Found the element! %s", message);
        }
        curr = curr->next;
    }
    return 0;
}

int addToList(struct myList ** list, int client_id, char * message){

    struct myList * add = malloc(sizeof(struct myList));
    if (add == NULL) {
        return 0; // return failure
    }
    strcpy(add->message, message);
    add->client_id = client_id;
    add->next = NULL;
    struct myList * curr = *list;

    if (curr == NULL) {
        *list = add;
        return 1;
    }

    while (curr->next != NULL) {
        curr = curr->next;
    }
    curr->next = add;
    return 1;
}

int removeFromList(struct myList ** list, int client_id, const char * message){
    int found = 0;
    struct myList * curr = *list;
    struct myList * prev = NULL;

    while (curr != NULL) {
        // check if they're equal 
        if ( (curr->client_id==client_id) && (strcmp(curr->message, message)==0)) {
            found = 1; // found a node to delete
            if (prev == NULL){
                *list = curr->next;
                curr->next = NULL;
                break;
            }
            prev->next = curr->next;
            curr->next = NULL;
            break;
        }
        prev = curr;
        curr = curr->next;
    }
    return found;
}

void printList(struct myList * list){

    int size = 0;
    struct myList * curr = list;
    if (curr == NULL){
        printf("[]\n"); // prints an empty list if nothing is there
    }

    while (curr != NULL){ 
        printf("[%d --- %s]\n", curr->client_id, curr->message);
        size += 1;
        curr = curr->next;
    }
    printf("\n"); // for nice formatting

}

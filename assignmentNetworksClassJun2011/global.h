#ifndef _GLOBAL_H_
#define _GLOBAL_H_

#define MAX_MSG_LENGTH 150

#include<stdio.h>
#include<string.h>

static char * SUCCESS_MSG = "Success";
static char * DEL_FAIL_MSG = "Failed to delete node";
static char * UPL_FAIL_MSG = "Failed to upload node";

int getSecondIndexOf(char * message, char find);
int extractClientID(char * message);
int getClientIDAndMessage(char buffer[], char * message);
void constructMessage(char * buffer, char * cmd, int client_id, char * message);

#endif /* _GLOBAL_H_ */

#include <string.h>

int getSecondIndexOf(char * message, char find){
    int i;
    int count = 0;
    for ( i = 0; message[i]; i ++) {
        if ((message[i] == '|') == 1){
            count += 1;
            if (count == 2) {
                return i;
            }
        }
    } 
    return -1; // not found 
}

int getClientIDAndMessage(char buffer[], char * message){
    int index = getSecondIndexOf(buffer, '|');
    strncpy(message, buffer + index + 1,strlen(buffer) - index);
    int id = extractClientID(buffer); 
    return id;
}

int extractClientID(char *message){
  char * ptr;
  ptr = strtok(message,"|");
  ptr = strtok(NULL, "|");
  if (ptr != NULL) {
     int res = atoi(ptr);
     return atoi(ptr);
  }
  return -1;
}

void constructMessage(char * m_send, char * cmd, int client_id, char * message){
    char * DELIMITER = "|";
    char id_str[1];
    bzero(id_str,1);
    sprintf(id_str, "%d", client_id);
    strcat(m_send, cmd);
    strcat(m_send, DELIMITER);
    strcat(m_send, id_str);
    strcat(m_send, DELIMITER);
    strcat(m_send, message);
}


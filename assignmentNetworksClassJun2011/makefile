all:
	gcc -g -x c -lsocket -o mainServer global.h global.c myList.h myList.c myServer.h myServer.c mainServer.c
	gcc -g -x c -lnsl -lsocket -o mainClient1 global.h global.c myList.h myList.c myClient.h myClient.c mainClient1.c 
	gcc -g -x c -lnsl -lsocket -o mainClient2 global.h global.c myList.h myList.c myClient.h myClient.c mainClient2.c 

clean:
	rm *.o mainClient1 mainClient2 mainServer


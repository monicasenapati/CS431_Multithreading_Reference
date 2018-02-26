###############	CLIENT side	####################	
#	1. Create socket
#	2. Establish connection between client & server
#	3. Send data request and data
#	4. Recieve response from server
#
################################################

import socket
import sys
import select


def writeToScreen(content):
	sys.stdout.write(content)
	sys.stdout.flush()

def main():
	#socket addresses 
	HOST = 'localhost'    	# Set the remote host, for testing it is localhost
	PORT = 8880            # The same port as used by the server
	BUF_SIZE =1024 			# max. amt of data that can be recieved at once
	msgFromClient = ""
	msgFromServer = ""
	continueChat = True
	promptMessage = "\n<Client1>: "

	#create socket
	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			#AF_INET => communicate using IPV4 addresses and SOCK_STREAM => TCP to handle connect
	sock.settimeout(2)

	#connect to sever
	sock.connect((HOST, PORT))  #connection successful if server is running at this port.
	writeToScreen(promptMessage)
	print '\nClient1 connected to server. Begin sending messages.'

	try:
		while True:
			possibleEvents = [sys.stdin, sock]
			readSock, writeSock, errSock = select.select(possibleEvents, [], [])
			for sckt in readSock:
				if sckt == sock:
					msgFromServer = sckt.recv(BUF_SIZE)
					if not msgFromServer:
						sys.exit()
					else:
						sys.stdout.flush()
						writeToScreen(msgFromServer)
						writeToScreen(promptMessage)
				else:
					msgFromClient = sys.stdin.readline()
					sock.send(msgFromClient)
					writeToScreen(promptMessage)

	except:
		print '\nClient1 unable to connect to server.'
		sys.exit()

	finally:
		sock.close()
		print "\nClient1 is closed! See you next time. :)"

main()

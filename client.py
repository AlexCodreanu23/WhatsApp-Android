import socket
import threading

# Server configuration
HOST = '68.183.68.146'  # Replace with the server's public IP address
PORT = 8080

def receive_messages(client_socket):
    while True:
        try:
            message = client_socket.recv(4096).decode('utf-8')
            if message:
                print(message)
            else:
                break
        except Exception as e:
            print(f"Error receiving message: {e}")
            break

# Create a TCP socket
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
try:
    client_socket.connect((HOST, PORT))
    print(f"Connected to server at {HOST}:{PORT}")
except Exception as e:
    print(f"Unable to connect to server: {e}")
    exit(1)

# Start a thread to receive messages from the server
thread = threading.Thread(target=receive_messages, args=(client_socket,))
thread.start()

while True:
    message = input()
    client_socket.send(message.encode('utf-8'))
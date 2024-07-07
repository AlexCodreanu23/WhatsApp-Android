import socket
import select

# Server configuration
HOST = '68.183.68.146'  # Replace with your server's public IP address
PORT = 8080

# List to keep track of socket descriptors
sockets_list = []
clients = {}

# Create a TCP socket
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((HOST, PORT))
server_socket.listen(10)

# Add server socket to the list of readable connections
sockets_list.append(server_socket)

print(f"Chat server started on port {PORT}")

def send_message_to_client(message, target_ip):
    for client_socket, client_address in clients.items():
        if client_address[0] == target_ip:
            try:
                client_socket.send(message)
            except:
                client_socket.close()
                sockets_list.remove(client_socket)
                del clients[client_socket]

def broadcast_message(message, sender_socket):
    for client_socket in clients:
        if client_socket != sender_socket:
            try:
                client_socket.send(message)
            except:
                client_socket.close()
                sockets_list.remove(client_socket)
                del clients[client_socket]

while True:
    # Get the list of sockets which are ready to be read
    read_sockets, write_sockets, error_sockets = select.select(sockets_list, [], [])
    
    for sock in read_sockets:
        # New connection
        if sock == server_socket:
            client_socket, client_address = server_socket.accept()
            sockets_list.append(client_socket)
            clients[client_socket] = client_address
            print(f"Client {client_address} connected")
        
        # Message from a client
        else:
            try:
                data = sock.recv(4096).decode('utf-8')
                if data:
                    # Check if the message is intended for a specific client
                    if data.startswith("@"):
                        target_ip, message = data[1:].split(" ", 1)
                        send_message_to_client(message.encode('utf-8'), target_ip)
                    else:
                        message = f"{clients[sock]}: {data}"
                        print(message)
                        broadcast_message(message.encode('utf-8'), sock)
            except:
                print(f"Client {clients[sock]} disconnected")
                sock.close()
                sockets_list.remove(sock)
                del clients[sock]
                continue
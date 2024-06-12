package com.example.mess;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_ADDRESS = "68.183.68.146";
    private static final int SERVER_PORT = 8080;
    private static final String TAG = "ChatClient";

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public interface MessageListener {
        void onMessageReceived(String message);
    }

    private MessageListener listener;

    public ChatClient(MessageListener listener) {
        this.listener = listener;
    }

    public void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Attempting to connect to server...");
                    socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    Log.d(TAG, "Connected to server");
                    new Thread(new ReceiveMessagesTask()).start();
                } catch (IOException e) {
                    Log.e(TAG, "Error connecting to server", e);
                }
            }
        }).start();
    }

    public void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (out != null) {
                    Log.d(TAG, "Sending message: " + message);
                    out.println(message);
                } else {
                    Log.d(TAG, "Output stream is null, message not sent");
                }
            }
        }).start();
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        socket.close();
                        Log.d(TAG, "Disconnected from server");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error disconnecting from server", e);
                }
            }
        }).start();
    }

    private class ReceiveMessagesTask implements Runnable {
        @Override
        public void run() {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    Log.d(TAG, "Received message: " + response);
                    listener.onMessageReceived(response);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error receiving message", e);
            }
        }
    }
}

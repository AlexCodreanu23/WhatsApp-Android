package com.example.app10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app10.ui.theme.App10Theme
import kotlinx.coroutines.*
import java.net.Socket

class MainActivity : ComponentActivity() {
    private lateinit var clientSocket: Socket
    private lateinit var receiveJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App10Theme {
                var showChat by remember { mutableStateOf(false) }
                var username by remember { mutableStateOf("") }
                var message by remember { mutableStateOf("") }
                val chatLog = remember { mutableStateListOf<String>() }
                var isConnected by remember { mutableStateOf(false) }
                val contact = remember { Contact("John Doe", "123-456-7890") }

                if (showChat) {
                    ChatScreen(contact) { showChat = false }
                } else {
                    MainScreen(
                        username = username,
                        onUsernameChange = { username = it },
                        onConnectClick = {
                            connectToServer(username, chatLog)
                            isConnected = true
                            showChat = true
                        },
                        isConnected = isConnected,
                        chatLog = chatLog,
                        message = message,
                        onMessageChange = { message = it },
                        onSendClick = {
                            sendMessage(message)
                            message = ""
                        }
                    )
                }
            }
        }
    }

    private fun connectToServer(username: String, chatLog: MutableList<String>) {
        val host = "68.183.68.146"
        val port = 8080

        CoroutineScope(Dispatchers.IO).launch {
            try {
                clientSocket = Socket(host, port)
                sendMessage("REGISTER $username")
                receiveMessages(chatLog)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendMessage(message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                clientSocket.getOutputStream().write((message + "\n").toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun receiveMessages(chatLog: MutableList<String>) {
        receiveJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val reader = clientSocket.getInputStream().bufferedReader()
                while (true) {
                    val message = reader.readLine() ?: break
                    withContext(Dispatchers.Main) {
                        chatLog.add(message)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::clientSocket.isInitialized) {
            clientSocket.close()
        }
        if (this::receiveJob.isInitialized) {
            receiveJob.cancel()
        }
    }
}

@Composable
fun MainScreen(
    username: String,
    onUsernameChange: (String) -> Unit,
    onConnectClick: () -> Unit,
    isConnected: Boolean,
    chatLog: List<String>,
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = onConnectClick) {
            Text("Connect")
        }

        if (isConnected) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(chatLog) { log ->
                    Text(log)
                }
            }

            TextField(
                value = message,
                onValueChange = onMessageChange,
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = onSendClick) {
                Text("Send")
            }
        }
    }
}

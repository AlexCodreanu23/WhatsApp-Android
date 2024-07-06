package com.example.app10

import java.io.PrintWriter
import java.net.Socket

class ChatClient(private val serverIp: String, private val serverPort: Int) {
    private var socket: Socket? = null
    private var writer: PrintWriter? = null

    fun connect() {
        try {
            socket = Socket(serverIp, serverPort)
            writer = PrintWriter(socket!!.outputStream, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(message: String) {
        writer?.println(message)
    }

    fun disconnect() {
        try {
            writer?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

package com.mat

import com.mat.plugins.configureRouting
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

fun main() {


    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
    }.start()

    // TCP server code
    runBlocking {
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", 7777))
        println("Listening....")
        while (true) {
            val socket = server.accept()

            launch {
                println("Socket accepted: ${socket.remoteAddress}")
                val input = socket.openReadChannel()
                try {
                    while (true) {
                        val size = input.readInt()
                        val data: ByteArray = ByteArray(size)
                        input.readFully(data, 0, size)
                        println(data.toString(Charsets.UTF_8))
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    socket.close()
                }
            }

        }
    }

}

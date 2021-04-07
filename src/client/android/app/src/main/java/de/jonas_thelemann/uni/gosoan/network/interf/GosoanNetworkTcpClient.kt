package de.jonas_thelemann.uni.gosoan.network.interf

import java.io.OutputStream
import java.net.Socket
import java.net.URI


class GosoanNetworkTcpClient(override val serverUri: URI) : GosoanNetworkInterface {
    private lateinit var clientSocket: Socket
    private lateinit var outputStream: OutputStream

    override val isGosoanOpen
        get() = clientSocket.isConnected

    override fun send(byteArray: ByteArray) {
        val runnable = Runnable {
            outputStream.write(byteArray.plus(0x0A))
            outputStream.flush()
        }
        val thread = Thread(runnable)
        thread.start()
    }

    override fun start() {
        clientSocket = Socket(serverUri.host, serverUri.port)
        outputStream = clientSocket.getOutputStream()
    }

    override fun stop() {
        outputStream.flush()
        outputStream.close()
        clientSocket.close()
    }
}

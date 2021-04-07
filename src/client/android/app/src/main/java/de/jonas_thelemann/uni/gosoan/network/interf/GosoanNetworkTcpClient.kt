package de.jonas_thelemann.uni.gosoan.network.interf

import java.io.OutputStream
import java.net.Socket
import java.net.URI
import java.util.concurrent.ConcurrentLinkedQueue


class GosoanNetworkTcpClient(override val serverUri: URI) : GosoanNetworkInterface {
    override val isGosoanOpen
        get() = if (::clientSocket.isInitialized) clientSocket.isConnected else false
    override val queue: ConcurrentLinkedQueue<ByteArray> = ConcurrentLinkedQueue()
    override var exception: Exception? = null

    private lateinit var clientSocket: Socket
    private lateinit var outputStream: OutputStream

    override fun start() {
        exception = null

        try {
            clientSocket = Socket(serverUri.host, serverUri.port)
            dequeue()
        } catch (e: Exception) {
            exception = e
            return
        }

        outputStream = clientSocket.getOutputStream()
    }

    override fun stop() {
        outputStream.flush()
        outputStream.close()
        clientSocket.close()
    }

    override fun sendBytes(byteArray: ByteArray) {
        outputStream.write(byteArray.plus(0x0A))
        outputStream.flush()
    }
}

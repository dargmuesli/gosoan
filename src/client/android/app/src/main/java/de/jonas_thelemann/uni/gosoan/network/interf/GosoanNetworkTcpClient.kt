package de.jonas_thelemann.uni.gosoan.network.interf

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.net.Socket
import java.net.URI
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue

class GosoanNetworkTcpClient(override val serverUri: URI) : GosoanNetworkInterface {
    override val isGosoanOpen
        get() = if (::clientSocket.isInitialized) !clientSocket.isClosed else false
    override val queue: ConcurrentLinkedQueue<ByteArray> = ConcurrentLinkedQueue()

    override var dataSent: Int = 0
    override var exception: Exception? = null

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val reconnectTask = Runnable {
        CoroutineScope(Dispatchers.IO).launch {
            start()
        }
    }

    private lateinit var clientSocket: Socket
    private lateinit var outputStream: OutputStream

    override fun start() {
        exception = null

        try {
            clientSocket = Socket(serverUri.host, serverUri.port)
        } catch (e: Exception) {
            exception = e
            handler.postDelayed(reconnectTask, 1000)
            return
        }

        outputStream = clientSocket.getOutputStream()
        dequeue()
    }

    override fun stop() {
        if (::outputStream.isInitialized) {
            outputStream.flush()
            outputStream.close()
        }

        if (::clientSocket.isInitialized) {
            clientSocket.close()
        }

        handler.removeCallbacks(reconnectTask)
    }

    @Synchronized
    override fun sendBytes(byteArray: ByteArray): Boolean {
        return try {
            outputStream.write(ByteBuffer.allocate(4).putInt(byteArray.size).array())
            outputStream.write(byteArray)
            outputStream.flush()
            true
        } catch (e: java.lang.Exception) {
            clientSocket.close()
            exception = e
            handler.postDelayed(reconnectTask, 1000)
            false
        }
    }
}

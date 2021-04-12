package de.jonas_thelemann.uni.gosoan.network.interf

import de.jonas_thelemann.uni.gosoan.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.util.concurrent.ConcurrentLinkedQueue

interface GosoanNetworkInterface {
    val isGosoanOpen: Boolean
    val queue: ConcurrentLinkedQueue<ByteArray>
    val serverUri: URI

    var dataSent: Int
    var exception: Exception?

    fun start()
    fun stop()

    suspend fun enqueue(byteArray: ByteArray) {
        return withContext(Dispatchers.Default) {
            if (queue.size >= BuildConfig.QUEUE_SIZE_MAX) {
                for (i in (queue.size - BuildConfig.QUEUE_SIZE_MAX) downTo 0) {
                    queue.remove()
                }
            }

            queue.add(byteArray)
            dequeue()
        }
    }

    suspend fun dequeue() {
        return withContext(Dispatchers.IO) {
            for (item in queue) {
                if (!isGosoanOpen) return@withContext
                if (sendBytes(item)) {
                    queue.remove(item)
                    dataSent++
                }
            }
        }
    }

    fun sendBytes(byteArray: ByteArray): Boolean
}
package de.jonas_thelemann.uni.gosoan.network.interf

import de.jonas_thelemann.uni.gosoan.BuildConfig
import java.net.URI
import java.util.concurrent.ConcurrentLinkedQueue

interface GosoanNetworkInterface {
    val isGosoanOpen: Boolean
    val queue: ConcurrentLinkedQueue<ByteArray>
    val serverUri: URI

    var exception: Exception?

    fun start()
    fun stop()

    fun enqueue(byteArray: ByteArray) {
        Thread {
            if (queue.size >= BuildConfig.QUEUE_SIZE_MAX) {
                for (i in (queue.size - BuildConfig.QUEUE_SIZE_MAX) downTo 0) {
                    queue.remove()
                }
            }

            queue.add(byteArray)
            dequeue()
        }.start()
    }
    fun dequeue() {
        if (isGosoanOpen) {
            queue.forEach { sendBytes(it.plus(0x0A)) }
        }
    }

    fun sendBytes(byteArray: ByteArray)
}
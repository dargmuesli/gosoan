package de.jonas_thelemann.uni.gosoan.network.interf

import java.net.URI

interface GosoanNetworkInterface {
    val isGosoanOpen: Boolean
    val serverUri: URI

    fun send(byteArray: ByteArray)
    fun start()
    fun stop()
}
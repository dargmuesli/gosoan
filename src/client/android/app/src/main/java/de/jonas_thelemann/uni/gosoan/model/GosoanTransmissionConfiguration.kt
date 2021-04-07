package de.jonas_thelemann.uni.gosoan.model

import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkInterface

data class GosoanTransmissionConfiguration(
    val dataFormat: GosoanDataFormat,
    val networkInterface: GosoanNetworkInterface
)
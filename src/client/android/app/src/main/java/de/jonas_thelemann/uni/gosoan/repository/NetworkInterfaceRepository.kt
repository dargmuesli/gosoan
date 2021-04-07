package de.jonas_thelemann.uni.gosoan.repository

import de.jonas_thelemann.uni.gosoan.model.GosoanDataFormat
import de.jonas_thelemann.uni.gosoan.model.GosoanTransmissionConfiguration
import de.jonas_thelemann.uni.gosoan.network.interf.GosoanNetworkInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkInterfaceRepository @Inject constructor() {
    val gosoanNetworkInterfaces = mutableListOf<GosoanNetworkInterface>()

    private val sensorIdTransmissionConfigurationMap =
        mutableMapOf<String, GosoanTransmissionConfiguration>()

    fun addNetworkInterface(gosoanNetworkInterface: GosoanNetworkInterface) {
        gosoanNetworkInterfaces.add(gosoanNetworkInterface)
    }

    fun addSensorIdTransmissionConfigurationMapping(
        gosoanSensorId: String,
        dataFormat: GosoanDataFormat,
        gosoanNetworkInterface: GosoanNetworkInterface
    ) {
        sensorIdTransmissionConfigurationMap[gosoanSensorId] =
            GosoanTransmissionConfiguration(dataFormat, gosoanNetworkInterface)
    }

    fun getGosoanNetworkInterface(serverIp: String, port: Int): GosoanNetworkInterface? {
        for (networkInterface in gosoanNetworkInterfaces) {
            if (networkInterface.serverUri.host == serverIp && networkInterface.serverUri.port == port) {
                return networkInterface
            }
        }

        return null
    }

    fun getTransmissionConfiguration(gosoanSensorId: String): GosoanTransmissionConfiguration? {
        return sensorIdTransmissionConfigurationMap[gosoanSensorId]
    }

    fun clear() {
        for (networkInterface in gosoanNetworkInterfaces) {
            networkInterface.stop()
        }

        gosoanNetworkInterfaces.clear()
        sensorIdTransmissionConfigurationMap.clear()
    }
}
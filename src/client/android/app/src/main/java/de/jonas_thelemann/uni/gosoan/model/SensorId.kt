package de.jonas_thelemann.uni.gosoan.model

import java.io.Serializable

data class SensorId constructor(
    val name: String,
    val type: Int,
) : Serializable {
    override fun toString(): String {
        return type.toString() + "_" + name.replace("""\s""".toRegex(), "-")
    }
}

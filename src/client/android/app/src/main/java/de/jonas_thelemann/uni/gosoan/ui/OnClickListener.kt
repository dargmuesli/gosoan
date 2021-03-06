package de.jonas_thelemann.uni.gosoan.ui

class OnClickListener<T>(private val block: (T) -> Unit) {
    fun onClick(input: T) = block(input)
}

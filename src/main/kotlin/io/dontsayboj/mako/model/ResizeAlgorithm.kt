package io.dontsayboj.mako.model

enum class ResizeAlgorithm {
    Imgscalr,
    Thumbnailator,
    Native;

    fun getLabelRes(): String = when (this) {
        Imgscalr -> "dialog.label.algorithm.imgscalr"
        Thumbnailator -> "dialog.label.algorithm.thumbnailator"
        Native -> "dialog.label.algorithm.native"
    }
}
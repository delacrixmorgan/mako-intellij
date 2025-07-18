package io.dontsayboj.mako.model

enum class FileFormat(
    val extension: String
) {
    PNG(extension = "png"),
    JPG(extension = "jpg"),
    JPEG(extension = "jpeg"),
    WEBP(extension = "webp");
}

val supportedFileFormats by lazy {
    FileFormat.entries.map { it.extension }
}
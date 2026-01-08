package io.dontsayboj.mako

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi
import io.dontsayboj.mako.model.FileFormat
import io.dontsayboj.mako.model.ResizeAlgorithm
import io.dontsayboj.mako.ui.Bundle
import net.coobird.thumbnailator.Thumbnails
import org.imgscalr.Scalr
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.spi.IIORegistry
import kotlin.math.roundToInt

class AndroidDrawableImporterAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val project = e.project

        val basePath = project?.basePath
        val baseDir = basePath?.let {
            com.intellij.openapi.vfs.LocalFileSystem.getInstance().findFileByPath(it)
        }

        val isAndroidProject = listOf(
            "build.gradle", "build.gradle.kts",
            "settings.gradle", "settings.gradle.kts"
        ).any { fileName -> baseDir?.findChild(fileName) != null }

        val isResFolder = file?.isDirectory == true &&
                file.name.startsWith("res") &&
                file.path.contains("/res")

        val isDrawableFolder = file?.isDirectory == true &&
                file.name.startsWith("drawable") &&
                file.path.contains("/drawable")

        e.presentation.isVisible = isAndroidProject && (isResFolder || isDrawableFolder)
        e.presentation.isEnabled = e.presentation.isVisible
    }

    override fun actionPerformed(e: AnActionEvent) {
        registerWebPReaderIfNeeded()

        val selectedDir = e.getData(CommonDataKeys.VIRTUAL_FILE)?.path
        val dialog = ImageImportDialog(selectedDir)
        if (dialog.showAndGet()) {
            val imageFiles = dialog.getDroppedFiles()
            val outputDir = dialog.getOutputDirectory()
            val modifier = dialog.getModifier()
            val algorithm = dialog.getAlgorithm()

            if (imageFiles.isEmpty()) {
                Messages.showErrorDialog(
                    Bundle.message("notification.errorNoImages.description"),
                    Bundle.message("notification.errorNoImages.title")
                )
                return
            }

            val scales = mapOf(
                "xxxhdpi" to 1.0,
                "xxhdpi" to 0.75,
                "xhdpi" to 0.5,
                "hdpi" to 0.375,
                "mdpi" to 0.25
            )

            imageFiles.forEach { imageFile ->
                val image = ImageIO.read(imageFile) ?: return@forEach
                val baseName = imageFile.nameWithoutExtension

                scales.forEach { (bucket, scale) ->
                    val scaledWidth = (image.width * scale).roundToInt()
                    val scaledHeight = (image.height * scale).roundToInt()
                    val resized = when (algorithm) {
                        ResizeAlgorithm.NATIVE -> {
                            val bufferedImage = BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB)
                            val g2d = bufferedImage.createGraphics()
                            g2d.drawImage(image, 0, 0, scaledWidth, scaledHeight, null)
                            g2d.dispose()
                            bufferedImage
                        }

                        ResizeAlgorithm.THUMBNAILATOR -> {
                            Thumbnails.of(image).size(scaledWidth, scaledHeight).asBufferedImage()
                        }

                        ResizeAlgorithm.IMGSCALR -> {
                            Scalr.resize(image, Scalr.Method.QUALITY, scaledWidth, scaledHeight)
                        }
                    }


                    val suffix = if (modifier.isNotBlank()) "-${modifier.lowercase()}" else ""
                    val bucketDir = File(outputDir, "drawable$suffix-$bucket")
                    bucketDir.mkdirs()
                    val originalFormat = imageFile.extension.lowercase()
                    val format = when {
                        ImageIO.getImageWritersByFormatName(originalFormat).hasNext() -> originalFormat
                        else -> FileFormat.PNG.extension
                    }
                    val outputFile = File(bucketDir, "$baseName.$format")
                    ImageIO.write(resized, format, outputFile)
                }
            }
            val project = e.project
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Image Import Notifications")
                .createNotification(
                    Bundle.message("notification.success.title"),
                    Bundle.message("notification.success.description", imageFiles.size),
                    NotificationType.INFORMATION
                )
                .notify(project)
        }
    }

    private fun registerWebPReaderIfNeeded() {
        val registry = IIORegistry.getDefaultInstance()
        val isRegistered = registry.getServiceProviders(javax.imageio.spi.ImageReaderSpi::class.java, true)
            .asSequence()
            .any { it.javaClass.name.contains(FileFormat.WEBP.extension, ignoreCase = true) }

        if (!isRegistered) {
            registry.registerServiceProvider(WebPImageReaderSpi())
        }
    }
}

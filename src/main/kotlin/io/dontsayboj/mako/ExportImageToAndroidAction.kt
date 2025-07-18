package io.dontsayboj.mako

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer
import kotlin.math.roundToInt

class ExportImageToAndroidAction : AnAction("Export Images to Android Densities") {
    override fun actionPerformed(e: AnActionEvent) {
        val dialog = ImageExportDialog()
        if (dialog.showAndGet()) {
            val imageFiles = dialog.getDroppedFiles()
            val outputDir = dialog.getOutputDirectory()

            if (imageFiles.isEmpty()) {
                Messages.showErrorDialog("No images dropped.", "Error")
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
                    val resized = BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB)
                    val g2d = resized.createGraphics()
                    g2d.drawImage(image, 0, 0, scaledWidth, scaledHeight, null)
                    g2d.dispose()

                    val bucketDir = File(outputDir, "drawable-$bucket")
                    bucketDir.mkdirs()
                    val outputFile = File(bucketDir, "$baseName.png")
                    ImageIO.write(resized, "png", outputFile)
                }
            }

            Messages.showInfoMessage(
                "All images have been exported successfully.",
                "Export Completed ✅",
            )
        }
    }
}

class ImageExportDialog : DialogWrapper(true) {
    private val outputDirField = ExtendableTextField().apply {
        columns = 50
        emptyText.text = "Select output directory"
        border = JBUI.Borders.empty(2)

        addExtension(
            ExtendableTextComponent.Extension.create(
                AllIcons.Nodes.Folder,
                "Browse",
            ) {
                val projectBase = ProjectManager.getInstance().openProjects.firstOrNull()?.basePath ?: return@create
                val chooser = JFileChooser(File(projectBase)).apply {
                    fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                }
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    text = chooser.selectedFile.absolutePath
                }
            }
        )
    }

    private val dropPanel = JPanel(BorderLayout()).apply {
        minimumSize = Dimension(0, 150)
    }
    private val tableModel = DefaultTableModel(arrayOf("Preview", "Filename", "Size", "Directory"), 0)
    private val table = JBTable(tableModel).apply {
        rowHeight = 40
        autoResizeMode = JBTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        columnModel.getColumn(0).cellRenderer = ImageRenderer()
        columnModel.getColumn(0).preferredWidth = 40
        columnModel.getColumn(1).preferredWidth = 150
        columnModel.getColumn(2).preferredWidth = 100
    }
    private val droppedFiles = mutableListOf<File>()

    init {
        init()
        title = "Batch Export Images to Android Densities"

        val instructionLabel = JLabel("Drag and drop PNG, JPG, or JPEG files here", SwingConstants.CENTER)
        dropPanel.border = BorderFactory.createTitledBorder("Drop images here")
        dropPanel.background = Color(240, 240, 240)
        dropPanel.add(instructionLabel, BorderLayout.CENTER)

        dropPanel.dropTarget = object : DropTarget() {
            override fun drop(evt: DropTargetDropEvent) {
                evt.acceptDrop(DnDConstants.ACTION_COPY)
                val data = evt.transferable.getTransferData(DataFlavor.javaFileListFlavor) as? List<*>
                val images = data?.filterIsInstance<File>()?.filter {
                    it.extension.lowercase() in listOf("png", "jpg", "jpeg")
                } ?: emptyList()
                droppedFiles.clear()
                droppedFiles.addAll(images)
                dropPanel.background = Color(200, 255, 200)
                dropPanel.border = BorderFactory.createTitledBorder("${images.size} image(s) ready to export")

                tableModel.setRowCount(0)
                images.forEach { file ->
                    val sizeKb = "%.2f KB".format(file.length() / 1024.0)
                    val buffered = ImageIO.read(file)
                    val imgIcon = if (buffered != null) {
                        ImageIcon(buffered.getScaledInstance(32, 32, Image.SCALE_SMOOTH))
                    } else {
                        ImageIcon()
                    }
                    tableModel.addRow(arrayOf(imgIcon, file.name, sizeKb, file.parent))
                }

                dropPanel.repaint()
            }
        }

        dropPanel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val chooser = JFileChooser(File(System.getProperty("user.home") + "/Desktop"))
                chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                chooser.isMultiSelectionEnabled = true
                val result = chooser.showOpenDialog(null)
                if (result == JFileChooser.APPROVE_OPTION) {
                    val files = chooser.selectedFiles.toList().filter {
                        it.extension.lowercase() in listOf("png", "jpg", "jpeg")
                    }
                    droppedFiles.clear()
                    droppedFiles.addAll(files)
                    dropPanel.background = Color(200, 255, 200)
                    dropPanel.border = BorderFactory.createTitledBorder("${files.size} image(s) ready to export")
                    tableModel.setRowCount(0)
                    files.forEach { file ->
                        val sizeKb = "%.2f KB".format(file.length() / 1024.0)
                        val buffered = ImageIO.read(file)
                        val imgIcon = if (buffered != null) {
                            ImageIcon(buffered.getScaledInstance(32, 32, Image.SCALE_SMOOTH))
                        } else {
                            ImageIcon()
                        }
                        tableModel.addRow(arrayOf(imgIcon, file.name, sizeKb, file.parent))
                    }
                    dropPanel.repaint()
                }
            }
        })

        val basePath = ProjectManager.getInstance().openProjects.firstOrNull()?.basePath
        basePath?.let {
            outputDirField.text = it
        }
    }

    override fun createCenterPanel(): JComponent = panel {
        row("Output Directory:") {
            cell(outputDirField).resizableColumn().align(Align.FILL)
        }
        row {
            cell(dropPanel).resizableColumn().align(Align.FILL)
        }
        row {
            cell(JScrollPane(table).apply {
                preferredSize = Dimension(0, 200)
            }).resizableColumn().align(Align.FILL)
        }
    }

    fun getDroppedFiles(): List<File> = droppedFiles

    fun getOutputDirectory(): File = File(outputDirField.text)

    private class ImageRenderer : TableCellRenderer {
        override fun getTableCellRendererComponent(
            table: JTable,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ): Component {
            return if (value is Icon) JLabel(value) else JLabel(value?.toString() ?: "")
        }
    }
}

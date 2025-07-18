package io.dontsayboj.mako

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import io.dontsayboj.mako.model.supportedFileFormats
import io.dontsayboj.mako.ui.Bundle
import io.dontsayboj.mako.ui.Theme
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Image
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer

class ImageImportDialog(prefillOutputDir: String? = null) : DialogWrapper(true) {

    private val droppedFiles = mutableListOf<File>()

    private val outputDirField = ExtendableTextField().apply {
        columns = 50
        emptyText.text = Bundle.message("dialog.placeholder.outputDirectory")
        border = JBUI.Borders.empty(2)

        addExtension(
            ExtendableTextComponent.Extension.create(
                AllIcons.Nodes.Folder,
                Bundle.message("dialog.tooltip.outputDirectory"),
            ) {
                val projectBase = ProjectManager.getInstance().openProjects.firstOrNull()?.basePath ?: return@create
                val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                val toSelect = VfsUtil.findFile(File(projectBase).toPath(), true)
                FileChooser.chooseFile(descriptor, null as Project?, null as Component?, toSelect) { file: VirtualFile ->
                    text = file.path
                }
            }
        )
    }

    private val modifierField = ExtendableTextField().apply {
        columns = 20
        emptyText.text = Bundle.message("dialog.placeholder.modifier")
        border = JBUI.Borders.empty(2)
    }

    private val dropPanel = JPanel(BorderLayout()).apply {
        minimumSize = Dimension(0, 150)
    }
    private val tableModel = DefaultTableModel(
        arrayOf(
            Bundle.message("dialog.label.tableRowPreview"),
            Bundle.message("dialog.label.tableRowFilename"),
            Bundle.message("dialog.label.tableRowSize"),
            Bundle.message("dialog.label.tableRowPathDirectory"),
        ), 0
    )
    private val table = JBTable(tableModel).apply {
        rowHeight = 40
        autoResizeMode = JBTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        columnModel.getColumn(0).cellRenderer = ImageRenderer()
        columnModel.getColumn(0).preferredWidth = 40
        columnModel.getColumn(1).preferredWidth = 150
        columnModel.getColumn(2).preferredWidth = 100
    }

    init {
        init()
        initKeyBindings()
        title = Bundle.message("dialog.title")

        dropPanel.border = BorderFactory.createTitledBorder(Bundle.message("dialog.title.dragAndDrop"))
        val instructionLabel = JLabel(Bundle.message("dialog.description.dragAndDrop"), SwingConstants.CENTER)
        dropPanel.add(instructionLabel, BorderLayout.CENTER)
        dropPanel.dropTarget = object : DropTarget() {
            override fun drop(evt: DropTargetDropEvent) {
                evt.acceptDrop(DnDConstants.ACTION_COPY)
                val data = evt.transferable.getTransferData(DataFlavor.javaFileListFlavor) as? List<*>
                val images = data?.filterIsInstance<File>()?.filter {
                    it.extension.lowercase() in supportedFileFormats
                } ?: emptyList()

                val newFiles = images.filterNot { candidate ->
                    droppedFiles.any { it.absolutePath == candidate.absolutePath }
                }
                droppedFiles.addAll(newFiles)

                if (images.isNotEmpty()) {
                    dropPanel.background = Theme.successBackground
                    dropPanel.border = BorderFactory.createTitledBorder(Bundle.message("dialog.label.imagesReadyToImport", droppedFiles.size))
                } else {
                    dropPanel.background = Theme.errorBackground
                    dropPanel.border = BorderFactory.createTitledBorder(Bundle.message("dialog.label.unsupportedFileFormat"))
                }
                dropPanel.repaint()

                tableModel.rowCount = 0
                droppedFiles.forEach { file ->
                    val sizeKb = "%.2f KB".format(file.length() / 1024.0)
                    val buffered = ImageIO.read(file)
                    val imgIcon = if (buffered != null) {
                        ImageIcon(buffered.getScaledInstance(32, 32, Image.SCALE_SMOOTH))
                    } else {
                        ImageIcon()
                    }
                    tableModel.addRow(arrayOf(imgIcon, file.name, sizeKb, file.parent))
                }
            }
        }

        dropPanel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val descriptor = FileChooserDescriptorFactory.createMultipleFilesNoJarsDescriptor()
                    .withFileFilter { it.extension?.lowercase() in supportedFileFormats }
                val toSelect = VfsUtil.findFile(File(System.getProperty("user.home") + "/Desktop").toPath(), true)
                FileChooser.chooseFiles(descriptor, null as Project?, null as Component?, toSelect) { selectedFiles ->
                    val files = selectedFiles.map { VfsUtilCore.virtualToIoFile(it) }
                    val newFiles = files.filterNot { candidate ->
                        droppedFiles.any { it.absolutePath == candidate.absolutePath }
                    }
                    droppedFiles.addAll(newFiles)
                    if (files.isNotEmpty()) {
                        dropPanel.background = Theme.successBackground
                        dropPanel.border = BorderFactory.createTitledBorder(Bundle.message("dialog.label.imagesReadyToImport", droppedFiles.size))
                    } else {
                        dropPanel.background = Theme.errorBackground
                        dropPanel.border = BorderFactory.createTitledBorder(Bundle.message("dialog.label.unsupportedFileFormat"))
                    }
                    dropPanel.repaint()

                    tableModel.rowCount = 0
                    droppedFiles.forEach { file ->
                        val sizeKb = "%.2f KB".format(file.length() / 1024.0)
                        val buffered = ImageIO.read(file)
                        val imgIcon = if (buffered != null) {
                            ImageIcon(buffered.getScaledInstance(32, 32, Image.SCALE_SMOOTH))
                        } else {
                            ImageIcon()
                        }
                        tableModel.addRow(arrayOf(imgIcon, file.name, sizeKb, file.parent))
                    }
                }
            }
        })

        outputDirField.text = prefillOutputDir ?: ProjectManager.getInstance()
            .openProjects.firstOrNull()?.basePath.orEmpty()
    }

    private fun initKeyBindings() {
        val deleteAction = object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                val selectedRows = table.selectedRows.sortedDescending()
                for (rowIndex in selectedRows) {
                    val filePath = tableModel.getValueAt(rowIndex, 3)?.toString()
                    val fileName = tableModel.getValueAt(rowIndex, 1)?.toString()
                    droppedFiles.removeIf { it.parent == filePath && it.name == fileName }
                    tableModel.removeRow(rowIndex)
                }
                dropPanel.border = BorderFactory.createTitledBorder(Bundle.message("dialog.label.imagesReadyToImport", droppedFiles.size))
                dropPanel.background = Theme.defaultBackground
                dropPanel.repaint()
            }
        }
        table.inputMap.put(KeyStroke.getKeyStroke("DELETE"), "deleteRows")
        table.inputMap.put(KeyStroke.getKeyStroke("BACK_SPACE"), "deleteRows")
        table.actionMap.put("deleteRows", deleteAction)
    }

    override fun createCenterPanel(): JComponent = panel {
        row(Bundle.message("dialog.label.outputDirectory")) {
            cell(outputDirField).resizableColumn().align(Align.FILL)
        }
        row(Bundle.message("dialog.label.modifier")) {
            cell(modifierField).resizableColumn().align(Align.FILL)
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

    fun getModifier(): String = modifierField.text.trim()

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
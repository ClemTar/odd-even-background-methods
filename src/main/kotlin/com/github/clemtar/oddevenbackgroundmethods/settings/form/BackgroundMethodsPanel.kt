package com.github.clemtar.oddevenbackgroundmethods.settings.form

import com.github.clemtar.oddevenbackgroundmethods.BackgroundMethodsModifier
import com.intellij.application.options.colors.*
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ui.ColorPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.EventDispatcher
import java.awt.Color
import java.awt.event.ActionListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel


class BackgroundMethodsPanel(
        private val options: ColorAndFontOptions,
        private val schemesProvider: SchemesPanel,
        private val category: String
) : OptionsPanel {

    private lateinit var rootPanel: JPanel

    private lateinit var background: JBCheckBox

    private lateinit var colorLabel1: JLabel
    private lateinit var colorLabel2: JLabel

    private val colorLabels: Array<JLabel>

    private lateinit var color1: ColorPanel
    private lateinit var color2: ColorPanel

    private val colors: Array<ColorPanel>

    private lateinit var gradientLabel: JLabel

    private val properties: PropertiesComponent = PropertiesComponent.getInstance()
    private val eventDispatcher: EventDispatcher<ColorAndFontSettingsListener> =
            EventDispatcher.create(ColorAndFontSettingsListener::class.java)

    init {
        colors = arrayOf(color1, color2)
        colorLabels = arrayOf(colorLabel1, colorLabel2)

        val actionListener = ActionListener {
            eventDispatcher.multicaster.settingsChanged()
            options.stateChanged()
        }
        background.addActionListener(actionListener)
        for (c in colors) {
            c.addActionListener(actionListener)
        }

        options.addListener(object : ColorAndFontSettingsListener.Abstract() {
            override fun settingsChanged() {
                if (!schemesProvider.areSchemesLoaded()) return
            }
        })
    }

    override fun getPanel(): JPanel = rootPanel

    override fun addListener(listener: ColorAndFontSettingsListener) {
        eventDispatcher.addListener(listener)
    }

    override fun updateOptionsList() {
        fillOptionsList()
    }

    private data class DescriptionsNode(val backgroundName: String, val descriptions: List<TextAttributesDescription>) {
        override fun toString(): String = backgroundName
    }

    private fun fillOptionsList() {
        val nodes = options.currentDescriptions.asSequence()
                .filter { it is TextAttributesDescription && it.group == category }
                .map {
                    val description = it as TextAttributesDescription
                    val backgroundName = description.toString().split(":")[0]
                    backgroundName to description
                }
                .groupBy { it.first }
                .map { (backgroundName, descriptions) ->
                    DefaultMutableTreeNode(DescriptionsNode(backgroundName,
                            descriptions.asSequence().map { it.second }.toList().sortedBy { it.toString() }))
                }
        val root = DefaultMutableTreeNode()
        for (node in nodes) {
            root.add(node)
        }
    }

    private fun resetDefault() {
        background.isEnabled = false
        background.isSelected = false
        gradientLabel.text = "Assign each brackets its own color from the spectrum below:"

        for (i in 0 until minRange()) {
            colors[i].isEnabled = false
            colors[i].selectedColor = null
            colorLabels[i].isEnabled = false
        }
    }

    private fun reset(backgroundName: String, descriptions: List<TextAttributesDescription>) {
        background.isEnabled = true
        gradientLabel.text = "Assign each ${backgroundName.toLowerCase()} its own color from the spectrum below:"

        for (i in 0 until minRange()) {
            colors[i].isEnabled = true
            colorLabels[i].isEnabled = true
            colors[i].selectedColor = descriptions.indexOfOrNull(i)?.backgroundColor
            descriptions.indexOfOrNull(i)?.let { eventDispatcher.multicaster.selectedOptionChanged(it) }
        }
    }

    private val Tree.selectedDescriptions: DescriptionsNode?
        get() = selectedValue as? DescriptionsNode

    private fun minRange() = minOf(2, 5)

    override fun processListOptions(): MutableSet<String> = mutableSetOf(
            BackgroundMethodsModifier.NAME_METHODS,
    )

    companion object {
        private const val SELECTED_COLOR_OPTION_PROPERTY = "background.selected.color.option.name"

        private var TextAttributesDescription.backgroundColor: Color?
            get() = externalForeground
            set(value) {
                externalForeground = value
            }

        private val Tree.selectedValue: Any?
            get() = (lastSelectedPathComponent as? DefaultMutableTreeNode)?.userObject

        private val Tree.selectedDescriptions: DescriptionsNode?
            get() = selectedValue as? DescriptionsNode

        private fun Tree.findOption(nodeObject: Any, matcher: (Any) -> Boolean): TreePath? {
            val model = model as DefaultTreeModel
            for (i in 0 until model.getChildCount(nodeObject)) {
                val childObject = model.getChild(nodeObject, i)
                if (childObject is DefaultMutableTreeNode) {
                    val data = childObject.userObject
                    if (matcher(data)) {
                        return TreePath(model.getPathToRoot(childObject))
                    }
                }

                val pathInChild = findOption(childObject, matcher)
                if (pathInChild != null) return pathInChild
            }

            return null
        }

        private fun Tree.selectOptionByRainbowName(backgroundName: String) {
            selectPath(findOption(model.root) { data ->
                data is DescriptionsNode
                        && backgroundName.isNotBlank()
                        && data.backgroundName.contains(backgroundName, ignoreCase = true)
            })
        }

        private fun Tree.selectOptionByType(attributeType: String) {
            selectPath(findOption(model.root) { data ->
                data is DescriptionsNode && data.descriptions.any { it.type == attributeType }
            })
        }

        private fun Tree.selectPath(path: TreePath?) {
            if (path != null) {
                selectionPath = path
                scrollPathToVisible(path)
            }
        }
    }
}

private fun <E> List<E>.indexOfOrNull(idx: Int): E? = if (idx < this.size) this[idx] else null

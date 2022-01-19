package com.github.clemtar.oddevenbackgroundmethods.settings

import com.github.clemtar.oddevenbackgroundmethods.BackgroundMethodsModifier
import com.github.clemtar.oddevenbackgroundmethods.settings.form.BackgroundMethodsPanel
import com.intellij.application.options.colors.*
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorAndFontDescriptorsProvider
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.psi.codeStyle.DisplayPriority
import com.intellij.psi.codeStyle.DisplayPrioritySortable


class BackgroundMethodsPageFactory : ColorAndFontPanelFactory, ColorAndFontDescriptorsProvider, DisplayPrioritySortable {

    override fun getDisplayName(): String = BACKGROUND_METHODS_GROUP

    override fun getPanelDisplayName(): String = BACKGROUND_METHODS_GROUP

    override fun getPriority(): DisplayPriority = DisplayPriority.COMMON_SETTINGS

    override fun createPanel(options: ColorAndFontOptions): NewColorAndFontPanel {
        val emptyPreview = PreviewPanel.Empty()
        val schemesPanel = SchemesPanel(options)
        val optionsPanel = BackgroundMethodsPanel(options, schemesPanel, BACKGROUND_METHODS_GROUP)

        schemesPanel.addListener(object : ColorAndFontSettingsListener.Abstract() {
            override fun schemeChanged(source: Any) {
                optionsPanel.updateOptionsList()
            }
        })

        return NewColorAndFontPanel(schemesPanel, optionsPanel, emptyPreview, BACKGROUND_METHODS_GROUP, null, null)
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTRIBUTE_DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = emptyArray()

    companion object {
        private const val BACKGROUND_METHODS_GROUP = "Background methods"
        private val ATTRIBUTE_DESCRIPTORS: Array<AttributesDescriptor> by lazy {
            createDescriptors(BackgroundMethodsModifier.NAME_METHODS)
        }

        private fun createDescriptors(name: String): Array<AttributesDescriptor> {
            return BackgroundMethodsModifier.getRainbowAttributesKeys(name)
                    .map { key -> AttributesDescriptor("$name:$key", key) }
                    .toTypedArray()
        }
    }

}

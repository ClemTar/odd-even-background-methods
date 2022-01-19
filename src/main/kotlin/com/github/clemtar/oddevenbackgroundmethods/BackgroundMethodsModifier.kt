package com.github.clemtar.oddevenbackgroundmethods

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesScheme
import com.intellij.openapi.editor.colors.impl.AbstractColorsScheme
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.TestOnly
import java.awt.Color
import java.awt.Font

object BackgroundMethodsModifier {

    val DEFAULT_KOTLIN_LABEL_COLOR = JBColor(0x4a86e8, 0x467cda)

    const val NAME_METHODS = "Background methods"
    private const val KEY_METHODS = "BACKGROUND_METHODS_COLOR"


    private val roundBracketsRainbowColorKeys: Array<TextAttributesKey> =
        createRainbowAttributesKeys(KEY_METHODS, 2)

    private val roundBrackets: CharArray = charArrayOf('(', ')')

    private val rainbowElement: HighlightInfoType = HighlightInfoType
            .HighlightInfoTypeImpl(HighlightSeverity.INFORMATION, DefaultLanguageHighlighterColors.CONSTANT)

    private val PsiElement.isMethod get() = roundBrackets.any { textContains(it) }


    public fun getRainbowAttributesKeys(rainbowName: String): Array<TextAttributesKey> {
        return when (rainbowName) {
            NAME_METHODS -> roundBracketsRainbowColorKeys
            else -> throw IllegalArgumentException("Unknown rainbow name: $rainbowName")
        }
    }

    private fun createRainbowAttributesKeys(keyName: String, size: Int): Array<TextAttributesKey> {
        return generateSequence(0) { it + 1 }
                .map { TextAttributesKey.createTextAttributesKey("$keyName$it") }
                .take(size)
                .toList()
                .toTypedArray()
    }

    fun isDarkEditor() = EditorColorsManager.getInstance().isDarkEditor


    private val KEY_HTML_CODE: TextAttributesKey by lazy { TextAttributesKey.createTextAttributesKey("HTML_CODE") }
    private val KEY_KOTLIN_LABEL: TextAttributesKey by lazy { TextAttributesKey.createTextAttributesKey("KOTLIN_LABEL") }
    private val KEY_MATCHED_BRACE_ATTRIBUTES: TextAttributesKey by lazy {
        TextAttributesKey.createTextAttributesKey("MATCHED_BRACE_ATTRIBUTES")
    }
    private val KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW: TextAttributesKey by lazy {
        TextAttributesKey.createTextAttributesKey("KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW")
    }


    fun fixHighlighting(scheme: EditorColorsScheme = EditorColorsManager.getInstance().globalScheme) {
        // html code
        scheme.setInherited(KEY_HTML_CODE, false)

        // kotlin label
        val kotlinLabelColor = DEFAULT_KOTLIN_LABEL_COLOR.takeUnless { true }
        val kotlinLabel = TextAttributes(kotlinLabelColor, null, null, EffectType.BOXED, Font.PLAIN)
        scheme.setAttributes(KEY_KOTLIN_LABEL, kotlinLabel)

        // matched brace
        val matchedBraceAttributes = TextAttributes(null, JBColor(0x99ccbb, 0x3b514d), null, EffectType.BOXED, Font.BOLD)
        scheme.setAttributes(KEY_MATCHED_BRACE_ATTRIBUTES, matchedBraceAttributes)

        scheme.setAttributes(KOTLIN_FUNCTION_LITERAL_BRACES_AND_ARROW,
                //TODO: default foregroundColor ???
                TextAttributes(JBColor(0x89ddff, 0x89ddff), null, null, EffectType.BOXED, Font.BOLD))

    }

    private fun EditorColorsScheme.setInherited(key: TextAttributesKey, inherited: Boolean) {
        setAttributes(key, if (inherited) AbstractColorsScheme.INHERITED_ATTRS_MARKER else TextAttributes())
    }
}

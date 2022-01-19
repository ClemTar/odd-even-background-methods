package com.github.clemtar.oddevenbackgroundmethods.listeners

import com.github.clemtar.oddevenbackgroundmethods.BackgroundMethodsModifier
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsScheme

class BackgroundMethodsSchemeListener : EditorColorsListener {

    override fun globalSchemeChange(scheme: EditorColorsScheme?) {
        scheme?.let { BackgroundMethodsModifier.fixHighlighting(it) }
    }
}

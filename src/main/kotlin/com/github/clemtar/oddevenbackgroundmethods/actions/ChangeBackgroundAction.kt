package com.github.clemtar.oddevenbackgroundmethods.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
//import com.intellij.openapi.ui.
import com.intellij.openapi.ui.Messages
import com.intellij.pom.Navigatable

class ChangeBackgroundAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val currentProject = event.getData(PlatformDataKeys.PROJECT)
        val dlgMsg = StringBuffer(event.presentation.text.toString() + " Selected!")
        val dlgTitle = event.presentation.description
        // If an element is selected in the editor, add info about it.
        val nav: Navigatable? = event.getData(CommonDataKeys.NAVIGATABLE)
        if (nav != null) {
            dlgMsg.append(String.format("\nSelected Element: %s", nav.toString()))
        }
        Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon())
    }

    override fun update(e: AnActionEvent) {
        val currentProject = e.getData(PlatformDataKeys.PROJECT)
        e.getPresentation().setEnabledAndVisible(currentProject != null);
    }
}

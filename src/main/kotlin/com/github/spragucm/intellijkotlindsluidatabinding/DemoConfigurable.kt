package com.github.spragucm.intellijkotlindsluidatabinding

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class DemoConfigurable: Configurable {
    override fun createComponent(): JComponent? {
        return demoBinding({})
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun apply() {
        
    }

    override fun getDisplayName(): String {
        return "DemoConfigurable"
    }
}
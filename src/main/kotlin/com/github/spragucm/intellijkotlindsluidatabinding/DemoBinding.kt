package com.github.spragucm.intellijkotlindsluidatabinding


import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Disposer
import com.intellij.ui.dsl.builder.*
import com.intellij.util.Alarm
import org.jetbrains.annotations.ApiStatus
import java.awt.Font
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.SwingUtilities

/*
 !!! HUUUUUUGE NOTE!!!
 onIsModified is not the right listener for triggering an action when a field has changed.
 This is because the first view in the vertical hierarchy to change, returns "true" and none
 of the remaining views are even checked for modification.

 For example, if the views are:
    checkbox
    textview1
    textview2

 and textview1 is modified, then the output would be:
    checkbox isModified false

 That's it! Which is confusing! Basically, textview1 println indicating modification doesn't even
 print!
 */

@Suppress("DialogTitleCapitalization")
fun demoBinding(parentDisposable: Disposable): DialogPanel {
    lateinit var lbIsModified: JLabel
    lateinit var lbModel: JLabel
    lateinit var panel: DialogPanel
    val alarm = Alarm(parentDisposable)
    val modelOld = Model()
    val model = Model()

    fun initValidation() {
        alarm.addRequest(Runnable {
            val modified = panel.isModified()
            lbIsModified.text = "isModified: $modified"
            lbIsModified.bold(modified)
            lbModel.text = "<html>$model"

            initValidation()
        }, 1000)
    }

    panel = panel {
        row {
            checkBox("Checkbox")
                .bindSelected(model::checkbox)
                .onIsModified {
                    println("checkbox modified ${model.checkbox}")
                    val isMod = modelOld.checkbox != model.checkbox
                    if (isMod) {
                        modelOld.checkbox = model.checkbox
                    }
                    return@onIsModified isMod
                }
        }
        row("textField:") {
            textField()
                .bindText(model::textField)
                .onIsModified {
                    println("textField modified ${model.textField}")
                    val isMod = modelOld.textField != model.textField
                    if (isMod) {
                        modelOld.textField = model.textField
                    }
                    return@onIsModified isMod
                }
        }
        row("intTextField(0..100):") {
            intTextField()
                .bindIntText(model::intTextField)
                .onIsModified {
                    println("IntTextField modified ${model.intTextField}")
                    val isMod = modelOld.intTextField != model.intTextField
                    if (isMod) {
                        modelOld.intTextField = model.intTextField
                    }
                    return@onIsModified isMod
                }
        }
        row("comboBox:") {
            comboBox(Color.values().toList())
                .bindItem(model::comboBoxColor.toNullableProperty())
        }
        row("slider:") {
            slider(0, 100, 10, 50)
                .bindValue(model::slider)
        }
        row("spinner:") {
            spinner(0..100)
                .bindIntValue(model::spinner)
        }
        buttonsGroup(title = "radioButton:") {
            for (value in Color.values()) {
                row {
                    radioButton(value.name, value)
                }
            }
        }.bind(model::radioButtonColor)

        group("DialogPanel Control") {
            row {
                button("Reset") {
                    panel.reset()
                }
                button("Apply") {
                    panel.apply()
                }
                lbIsModified = label("").component
            }
            row {
                lbModel = label("").component
            }
        }
    }

    val disposable = Disposer.newDisposable()
    panel.registerValidators(disposable)
    Disposer.register(parentDisposable, disposable)

    SwingUtilities.invokeLater {
        initValidation()
    }

    return panel
}

private fun JComponent.bold(isBold: Boolean) {
    font = font.deriveFont(if (isBold) Font.BOLD else Font.PLAIN)
}

@ApiStatus.Internal
internal data class Model(
    var checkbox: Boolean = false,
    var textField: String = "",
    var intTextField: Int = 0,
    var comboBoxColor: Color = Color.GREY,
    var slider: Int = 0,
    var spinner: Int = 0,
    var radioButtonColor: Color = Color.GREY,
)

@ApiStatus.Internal
internal enum class Color {
    WHITE,
    GREY,
    BLACK
}
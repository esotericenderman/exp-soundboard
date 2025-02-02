package ca.exp.soundboard.soundboard

import java.awt.Toolkit
import java.awt.event.KeyEvent

object KeyEventIntConverter {

    @JvmStatic
    fun getKeyEventText(keyCode: Int): String {
        if (keyCode in 96..105) {
            val numpad = Toolkit.getProperty("AWT.numpad", "NumPad")

            val character = (keyCode - 96 + 48).toChar()
            return "$numpad $character"
        }

        return KeyEvent.getKeyText(
            keyCode
        )
    }
}

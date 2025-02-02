package ca.exp.soundboard.soundboard

import org.jnativehook.keyboard.NativeKeyEvent
import java.io.File

class SoundboardEntry(var file: File, keys: IntArray?) {
    var activationKeys = keys ?: IntArray(0)

    fun matchesPressed(pressedKeys: ArrayList<Int>): Boolean {
        var keysRemaining = activationKeys.size
        if (keysRemaining == 0) return false

        for (key in activationKeys) {
            val localIterator = pressedKeys.iterator()

            while (localIterator.hasNext()) {
                val pressedKey = localIterator.next()

                if (key == pressedKey) {
                    keysRemaining--
                }
            }
        }

        return keysRemaining <= 0
    }

    fun getMatchCount(pressedKeys: ArrayList<Int>): Int {
        var matches = 0

        for (i in pressedKeys.indices) {
            val key = pressedKeys[i]
            val keyCount = activationKeys.size

            if (i < keyCount) {
                val hotkey = activationKeys[i]

                if (key == hotkey) {
                    matches++
                }
            }
        }

        return matches
    }

    fun play(audioManager: AudioManager, moddedSpeed: Boolean) {
        audioManager.playSoundClip(file, moddedSpeed)
    }

    val activationKeysString: String
        get() {
            var activationKeysString = ""

            if (activationKeys.isEmpty()) {
                return activationKeysString
            }

            for (keyCode in activationKeys) {
                activationKeysString = activationKeysString + NativeKeyEvent.getKeyText(keyCode) + "+"
            }

            activationKeysString = activationKeysString.substring(0, activationKeysString.length - 1)
            return activationKeysString
        }
}

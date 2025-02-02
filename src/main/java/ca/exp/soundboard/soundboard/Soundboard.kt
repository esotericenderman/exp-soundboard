package ca.exp.soundboard.soundboard

import com.google.gson.Gson
import org.jnativehook.keyboard.NativeKeyEvent
import java.io.*
import java.util.*

class Soundboard {
    val entries = ArrayList<SoundboardEntry>()

    val entriesAsObjectArrayForTable: Array<Array<Any?>>
        get() {
            val array = Array(entries.size) { arrayOfNulls<Any>(4) }
            for (i in array.indices) {
                val entry = entries[i]
                array[i][0] = entry.file.name
                array[i][1] = entry.activationKeysString
                array[i][2] = entry.file.absolutePath
                array[i][3] = i
            }

            return array
        }

    fun addEntry(file: File, keyNumbers: IntArray?) {
        entries.add(SoundboardEntry(file, keyNumbers))
    }

    fun removeEntry(index: Int) {
        entries.removeAt(index)
    }

    fun saveAsJsonFile(file: File) {
        val gson = Gson()

        val jsonString = gson.toJson(this)
        val writer: BufferedWriter?

        try {
            writer = BufferedWriter(FileWriter(file))
            writer.write(jsonString)
            writer.flush()
            writer.close()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    fun entriesContainPTTKeys(pttKeys: ArrayList<Int>): Boolean {
        if ((pttKeys != pttKeysClone) || (hasSoundboardChanged())) {
            soundboardEntriesClone = entries.clone() as ArrayList<SoundboardEntry>
            pttKeysClone = pttKeys.clone() as ArrayList<Int>
            var key: String?
            var i: Int

            for (entry in entries) {
                i = 0
                val actKey = entry.activationKeys[i]
                key = NativeKeyEvent.getKeyText(actKey).lowercase()

                for (number in pttKeys) {
                    if (key == KeyEventIntConverter.getKeyEventText(number).lowercase()
                    ) {
                        containsPPTKey = true
                        return true
                    }
                }

                i++
            }

            containsPPTKey = false
            return false
        }
        return containsPPTKey
    }

    private fun hasSoundboardChanged(): Boolean {
        return entries != soundboardEntriesClone
    }

    companion object {
        private var soundboardEntriesClone = ArrayList<SoundboardEntry>()
        private var containsPPTKey = false
        private var pttKeysClone = ArrayList<Int>()

        @JvmStatic
        fun loadFromJsonFile(file: File): Soundboard? {
            val reader: BufferedReader?

            try {
                reader = BufferedReader(FileReader(file))
            } catch (exception: FileNotFoundException) {
                exception.printStackTrace()
                return null
            }

            val gson = Gson()

            val soundboard = gson.fromJson(reader, Soundboard::class.java)

            try {
                reader.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }

            return soundboard
        }
    }
}

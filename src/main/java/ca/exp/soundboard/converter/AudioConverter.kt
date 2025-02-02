package ca.exp.soundboard.converter

import ws.schild.jave.Encoder
import ws.schild.jave.EncoderException
import ws.schild.jave.MultimediaObject
import ws.schild.jave.encode.AudioAttributes
import ws.schild.jave.encode.EncodingAttributes
import ws.schild.jave.progress.EncoderProgressListener
import java.io.File
import javax.swing.JOptionPane

object AudioConverter {
    private const val MP3_BITRATE = 256000
    private const val CHANNELS = 2
    private const val SAMPLE_RATE = 44100
    private const val LISTENER_PERMIL_PROGRESS = 1001

    private const val NEWLINE_CHARACTER = '\n'
    private const val PERIOD_CHARACTER = '.'

    private const val INCOMPATIBLE_INPUT_FILE_MESSAGE_TILE = "Input File incompatible"
    private const val INCOMPATIBLE_INPUT_FILE_MESSAGE = "Input file formatting/encoding is incompatible"

    private const val MP3_FILE_EXTENSION = ".mp3"
    private const val WAV_FILE_EXTENSION = ".wav"

    private const val AUDIO_ATTRIBUTES_MP3_CODEC = "libmp3lame"
    private const val MP3_ENCODING_ATTRIBUTES_FORMAT = "mp3"

    private const val AUDIO_ATTRIBUTES_WAV_CODEC = "pcm_s16le"
    private const val WAV_ENCODING_ATTRIBUTES_FORMAT = "wav"

    @JvmStatic
    fun convertToMp3(inputFiles: Array<File>, outputFolder: File, listener: EncoderProgressListener?) {
        Thread {
            for (input in inputFiles) {
                val output = getFile(input, outputFolder, MP3_FILE_EXTENSION)
                convertToMP3(input, output, listener)
            }
        }.start()
    }

    @JvmStatic
    fun asyncConvertToWAV(inputFiles: Array<File>, outputFolder: File, listener: EncoderProgressListener?) {
        Thread {
            for (input in inputFiles) {
                val output = getFile(input, outputFolder, WAV_FILE_EXTENSION)
                convertToWAV(input, output, listener)
            }
        }.start()
    }

    @JvmStatic
    fun asyncConvertToMP3(inputFile: File, outputFile: File, listener: EncoderProgressListener?) {
        Thread {
            convertToMP3(inputFile, outputFile, listener)
        }.start()
    }

    @JvmStatic
    fun asyncConvertToWAV(inputFile: File, outputFile: File, listener: EncoderProgressListener?) {
        Thread {
            convertToWAV(inputFile, outputFile, listener)
        }.start()
    }

    private fun convertToMP3(inputFile: File, outputFile: File, listener: EncoderProgressListener?) {
        val audioAttributes = AudioAttributes()

        audioAttributes.setCodec(AUDIO_ATTRIBUTES_MP3_CODEC)
        audioAttributes.setBitRate(MP3_BITRATE)
        audioAttributes.setChannels(CHANNELS)
        audioAttributes.setSamplingRate(SAMPLE_RATE)

        val encodingAttributes = EncodingAttributes()
        encodingAttributes.setInputFormat(MP3_ENCODING_ATTRIBUTES_FORMAT)

        encodingAttributes.setAudioAttributes(audioAttributes)

        val encoder = Encoder()

        try {
            encoder.encode(
                MultimediaObject(inputFile),
                outputFile,
                encodingAttributes,
                listener
            )
        } catch (exception: IllegalArgumentException) {
            JOptionPane.showMessageDialog(
                null,
                INCOMPATIBLE_INPUT_FILE_MESSAGE + NEWLINE_CHARACTER + inputFile.name,
                INCOMPATIBLE_INPUT_FILE_MESSAGE_TILE,
                0
            )

            listener?.progress(LISTENER_PERMIL_PROGRESS)
            exception.printStackTrace()
        } catch (exception: EncoderException) {
            JOptionPane.showMessageDialog(
                null,
                INCOMPATIBLE_INPUT_FILE_MESSAGE + NEWLINE_CHARACTER + inputFile.name,
                INCOMPATIBLE_INPUT_FILE_MESSAGE_TILE,
                0
            )

            listener?.progress(LISTENER_PERMIL_PROGRESS)
            exception.printStackTrace()
        }
    }

    private fun convertToWAV(inputFile: File, outputFile: File, listener: EncoderProgressListener?) {
        val audioAttributes = AudioAttributes()
        audioAttributes.setCodec(AUDIO_ATTRIBUTES_WAV_CODEC)

        val encodingAttributes = EncodingAttributes()
        encodingAttributes.setInputFormat(WAV_ENCODING_ATTRIBUTES_FORMAT)
        encodingAttributes.setAudioAttributes(audioAttributes)

        val encoder = Encoder()

        try {
            encoder.encode(
                MultimediaObject(inputFile),
                outputFile,
                encodingAttributes,
                listener
            )
        } catch (exception: IllegalArgumentException) {
            JOptionPane.showMessageDialog(
                null,
                INCOMPATIBLE_INPUT_FILE_MESSAGE + NEWLINE_CHARACTER + inputFile.name,
                INCOMPATIBLE_INPUT_FILE_MESSAGE_TILE,
                0
            )

            listener?.progress(LISTENER_PERMIL_PROGRESS)
            exception.printStackTrace()
        } catch (exception: EncoderException) {
            JOptionPane.showMessageDialog(
                null,
                INCOMPATIBLE_INPUT_FILE_MESSAGE + NEWLINE_CHARACTER + inputFile.name,
                INCOMPATIBLE_INPUT_FILE_MESSAGE_TILE,
                0
            )

            listener?.progress(LISTENER_PERMIL_PROGRESS)
            exception.printStackTrace()
        }
    }

    private fun getFile(inputFile: File, outputFolder: File, dotExtension: String): File {
        var fileName = inputFile.name
        val periodIndex = fileName.lastIndexOf(PERIOD_CHARACTER)

        if (periodIndex > 0) {
            fileName = fileName.substring(0, periodIndex) + dotExtension
        }

        return File(outputFolder.toString() + File.separator + fileName)
    }
}

package ca.exp.soundboard.rewrite.soundboard;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;

public class AudioManager {
    private static float firstOutputGain;
    private static float secondOutputGain;
    public final DataLine.Info standardDataLineInfo;
    Mixer primaryOutput;
    Mixer secondaryOutput;
    private boolean useSecondary = false;

    public AudioManager() {
        this.standardDataLineInfo = new DataLine.Info(SourceDataLine.class, Utils.format, 2048);
    }

    public static float getFirstOutputGain() {
        return firstOutputGain;
    }

    public static void setFirstOutputGain(float gain) {
        firstOutputGain = gain;
    }

    public static float getSecondOutputGain() {
        return secondOutputGain;
    }

    public static void setSecondOutputGain(float gain) {
        secondOutputGain = gain;
    }

    void playSoundClip(File file, boolean halfspeed) {
        AudioFormat format;

        if (halfspeed) {
            format = Utils.modifiedPlaybackFormat;
        } else {
            format = Utils.format;
        }
        if ((file.exists()) && (file.canRead())) {
            SourceDataLine primarySpeaker = null;
            SourceDataLine secondarySpeaker = null;
            try {
                primarySpeaker = (SourceDataLine) this.primaryOutput.getLine(this.standardDataLineInfo);
                primarySpeaker.open(format, 8192);
                FloatControl gain = (FloatControl) primarySpeaker.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(firstOutputGain);
                primarySpeaker.start();
            } catch (LineUnavailableException ex) {
                JOptionPane.showMessageDialog(null, "Selected Output Line: Primary Speaker is currently unavailable.",
                        "Line Unavailable Exception", 0);
            }
            if ((this.secondaryOutput != null) && (this.useSecondary)) {
                try {
                    secondarySpeaker = (SourceDataLine) this.secondaryOutput.getLine(this.standardDataLineInfo);
                    secondarySpeaker.open(format, 8192);
                    FloatControl gain = (FloatControl) secondarySpeaker.getControl(FloatControl.Type.MASTER_GAIN);
                    gain.setValue(secondOutputGain);
                    secondarySpeaker.start();
                } catch (LineUnavailableException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Selected Output Line: Secondary Speaker is currently unavailable.",
                            "Line Unavailable Exception", 0);
                }
            }
            Utils.playNewSoundClipThreaded(file, primarySpeaker, secondarySpeaker);
        }
    }

    public synchronized void setPrimaryOutputMixer(String mixerName) {
        String[] mixers = Utils.getMixerNames(this.standardDataLineInfo);
        String[] arrayOfString1;
        int j = (arrayOfString1 = mixers).length;
        for (int i = 0; i < j; i++) {
            Mixer.Info[] arrayOfInfo;
            int m = (arrayOfInfo = AudioSystem.getMixerInfo()).length;
            for (int k = 0; k < m; k++) {
                Mixer.Info mixerInfo = arrayOfInfo[k];
                if (mixerName.equals(mixerInfo.getName())) {
                    this.primaryOutput = AudioSystem.getMixer(mixerInfo);
                    return;
                }
            }
        }
    }

    public void setUseSecondary(boolean use) {
        this.useSecondary = use;
    }

    public boolean useSecondary() {
        return this.useSecondary;
    }

    public synchronized void setSecondaryOutputMixer(String mixerName) {
        String[] mixers = Utils.getMixerNames(this.standardDataLineInfo);
        String[] arrayOfString1;
        int j = (arrayOfString1 = mixers).length;
        for (int i = 0; i < j; i++) {
            Mixer.Info[] arrayOfInfo;
            int m = (arrayOfInfo = AudioSystem.getMixerInfo()).length;
            for (int k = 0; k < m; k++) {
                Mixer.Info mixerInfo = arrayOfInfo[k];
                if (mixerName.equals(mixerInfo.getName())) {
                    this.secondaryOutput = AudioSystem.getMixer(mixerInfo);
                    return;
                }
            }
        }
    }
}

package ca.exp.soundboard.remake.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

public class SoundboardModel {

    private AudioMaster audio;
    private ObservableList<Entry> entryList;
    private Preferences modelPreferences;
    private Logger logger;

    public SoundboardModel(int speakerCount) {
        audio = new AudioMaster(speakerCount);
        entryList = FXCollections.observableArrayList();
        logger = Logger.getLogger(this.getClass().getName());
        modelPreferences = Preferences.userRoot().node(this.getClass().getCanonicalName());

        logger.info( "Initialized " + this.getClass().getName() + " with " + speakerCount + " outputs");
    }

    public AudioMaster getAudio() {
        return audio;
    }

    public ObservableList<Entry> getEntries() {
        return entryList;
    }

}

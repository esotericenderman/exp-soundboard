package ca.exp.soundboard.remake.model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SoundPlayer implements Runnable {

    private Logger logger;

    private File sound;
    private SourceDataLine output;
    private AudioInputStream clip;
    private AudioFormat clipFormat;
    private String name;
    public final int index;

    public final AtomicReference<PlayerState> state;
    private boolean running;

    public SoundPlayer(AudioMaster master, File sound, SourceDataLine output, int index)
            throws UnsupportedAudioFileException, IOException {
        this.sound = sound;
        this.output = output;
        this.index = index;
        logger = Logger.getLogger(this.getClass().getName());
        state = new AtomicReference<PlayerState>(PlayerState.RESETTING);

        // grab formats from file
        try {
            clip = AudioSystem.getAudioInputStream(sound);
            clipFormat = clip.getFormat();
            if (!clipFormat.equals(AudioMaster.decodeFormat)) {
                clip = AudioSystem.getAudioInputStream(AudioMaster.decodeFormat, clip);
            }
        } catch (UnsupportedAudioFileException uafe) {
            logger.info( "Target file: \"" + sound.getName() + "\" is unsupported");
            throw uafe;
        }

        logger.info( "Initialized sound player on: \"" + sound.getName() + "\"");
    }

    public void run() {

        // setup buffer for containing samples of audio to be played
        name = Thread.currentThread().getName() + "/" + sound.getName();
        byte[] buffer = new byte[output.getBufferSize()];
        int bytesRead = 0;
        int bytesWritten = 0;
        running = true;

        while (running) {
            switch (state.get()) {
                case RESETTING:
                    buffer = new byte[output.getBufferSize()];
                    bytesRead = 0;
                    bytesWritten = 0;
                    state.compareAndSet(PlayerState.RESETTING, PlayerState.READY);
                    break;
                case READY:
                    state.compareAndSet(PlayerState.READY, PlayerState.PLAYING);
                    logger.info( "Starting playback");
                    /*try {
                        synchronized (state) {
                            state.wait();
                        }
                        state.compareAndSet(PlayerState.READY, PlayerState.PLAYING);
                        logger.info( "Starting playback");
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Failed to wait for starting signal", ie);
                    }*/
                    break;
                case PLAYING:
                    // read a sample from the audio file
                    try {
                        bytesRead = clip.read(buffer, 0, buffer.length);
                    } catch (IOException ioe) {
                        logger.log(Level.SEVERE, "Failed to read from file: \"" + sound.getName() + "\"", ioe);
                        state.compareAndSet(PlayerState.PLAYING, PlayerState.FINISHED);
                    }

                    // if anything was read
                    if (bytesRead >= 0) {
                        // write the sample to the output
                        bytesWritten = output.write(buffer, 0, bytesRead);

                        if (bytesWritten < bytesRead) {
                            logger.warning( "Line closed before stream was finished!");
                            state.compareAndSet(PlayerState.PLAYING, PlayerState.FINISHED);
                            break;
                        }
                    } else {
                        // once there is nothing left to write
                        logger.info( "Reached end of stream");
                        state.compareAndSet(PlayerState.PLAYING, PlayerState.FINISHED);
                    }
                    break;
                case PAUSED:
                    try {
                        logger.info( "Paused");
                        synchronized (state) {
                            state.wait();
                        }
                        logger.info( "Unpaused");
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Failed to pause thread: ", ie);
                        state.compareAndSet(PlayerState.PAUSED, PlayerState.FINISHED);
                    }
                    break;
                case WAIT:
                    try {
                        logger.info( "Waiting for signal");
                        synchronized (state) {
                            state.compareAndSet(PlayerState.WAIT, PlayerState.WAITING);
                            state.wait();
                        }
                        logger.info( "Finished waiting");
                        // waiting is done, continue playing
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Failed to wait: ", ie);
                        state.compareAndSet(PlayerState.WAITING, PlayerState.FINISHED);
                    }
                    break;
                case WAITING:
                    try {
                        logger.info( "Resuming wait for signal");
                        synchronized (state) {
                            state.wait();
                        }
                        logger.info( "Finished waiting");
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Failed to continue waiting: ", ie);
                        state.compareAndSet(PlayerState.WAITING, PlayerState.FINISHED);
                    }
                    break;
                case FINISHED:
                    // close remaining stream, clean buffer and release access to resource
                    try {
                        logger.info( "Stopping and cleaning up");
                        clip.close();
                        output.drain();
                        output.flush();
                        output.close();
                        output.stop();
                        synchronized (state) {
                            state.wait();
                        }
                    } catch (IOException ioe) {
                        logger.log(Level.SEVERE, "Failed to close stream from file: \"" + sound.getName() + "\"", ioe);
                    } catch (InterruptedException ie) {
                        logger.log(Level.WARNING, "Failed to wait for collection: ", ie);
                    }

                    running = false;
                    break;
            }
        }

        logger.info( "Instance finished: \"" + sound.getName() + "\"");
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean updateGain(float gain) {
        if (state.get() == PlayerState.WAITING) {
            AudioMaster.getMasterGain(output).setValue(gain);
            return true;
        } else {
            return false;
        }
    }

}

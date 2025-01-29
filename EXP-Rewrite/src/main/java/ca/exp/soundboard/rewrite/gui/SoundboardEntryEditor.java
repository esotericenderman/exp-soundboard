package ca.exp.soundboard.rewrite.gui;

import ca.exp.soundboard.rewrite.soundboard.Soundboard;
import ca.exp.soundboard.rewrite.soundboard.SoundboardEntry;
import ca.exp.soundboard.rewrite.soundboard.Utils;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class SoundboardEntryEditor extends JFrame {
    private static final long serialVersionUID = -8420285054567246768L;
    public int[] keyNums;
    SoundboardFrame soundboardframe;
    Soundboard soundboard;
    SoundboardEntry soundboardEntry = null;
    File soundfile;
    private JTextField keysTextField;
    private NativeKeyInputGetter inputGetter;
    private JLabel selectedSoundClipLabel;

    public SoundboardEntryEditor(SoundboardFrame soundboardframe) {
        this.soundboardframe = soundboardframe;
        this.soundboard = SoundboardFrame.soundboard;
        this.inputGetter = new NativeKeyInputGetter();// new NativeKeyInputGetter(null);

        setDefaultCloseOperation(2);
        setTitle("SoundboardStage Entry Editor");
        setIconImage(SoundboardFrame.icon);

        JLabel lblSoundClip = new JLabel("Sound clip:");

        this.selectedSoundClipLabel = new JLabel("None selected");

        JButton btnSelect = new JButton("Select");
        btnSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser filechooser = Utils.getFileChooser();
                filechooser.setMultiSelectionEnabled(true);
                filechooser.setFileFilter(
                        // new SoundboardEntryEditor.AudioClipFileFilter(SoundboardEntryEditor.this,
                        // null));
                        new SoundboardEntryEditor.AudioClipFileFilter());
                int session = filechooser.showDialog(null, "Select");
                if (session == 0) {
                    File[] selected = filechooser.getSelectedFiles();
                    if (selected.length > 1) {
                        SoundboardEntryEditor.this.multiAdd(selected);
                    } else {
                        SoundboardEntryEditor.this.soundfile = selected[0];
                    }
                    filechooser.setMultiSelectionEnabled(false);
                    if (Utils.isFileSupported(SoundboardEntryEditor.this.soundfile)) {
                        SoundboardEntryEditor.this.selectedSoundClipLabel
                                .setText(SoundboardEntryEditor.this.soundfile.getAbsolutePath());
                    } else {
                        SoundboardEntryEditor.this.soundfile = null;
                        JOptionPane.showMessageDialog(null,
                                SoundboardEntryEditor.this.soundfile.getName() + " uses an unsupported codec format.",
                                "Unsupported Format", 0);
                    }
                }
                filechooser.setMultiSelectionEnabled(false);
                SoundboardEntryEditor.this.pack();
            }

        });
        JSeparator separator = new JSeparator();

        JLabel lblMacroKeys = new JLabel("HotKeys:");

        this.keysTextField = new JTextField();
        this.keysTextField.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 1) {
                    SoundboardEntryEditor.this.keysTextField.setBackground(Color.CYAN);
                    //GlobalScreen.getInstance().addNativeKeyListener(SoundboardEntryEditor.this.inputGetter);
                    GlobalScreen.addNativeKeyListener(SoundboardEntryEditor.this.inputGetter);
                    SoundboardEntryEditor.this.inputGetter.clearPressedKeys();
                } else if (e.getButton() == 3) {
                    SoundboardEntryEditor.this.keysTextField.setBackground(Color.WHITE);
                    //GlobalScreen.getInstance().removeNativeKeyListener(SoundboardEntryEditor.this.inputGetter);
                    GlobalScreen.removeNativeKeyListener(SoundboardEntryEditor.this.inputGetter);
                    SoundboardEntryEditor.this.inputGetter.clearPressedKeys();
                    SoundboardEntryEditor.this.keyNums = new int[0];
                    SoundboardEntryEditor.this.keysTextField.setText("none");
                }
            }
        });
        this.keysTextField.setText("none");
        this.keysTextField.setEditable(false);
        this.keysTextField.setColumns(10);

        JButton btnDone = new JButton("Done");
        btnDone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                SoundboardEntryEditor.this.submit();
            }

        });
        JLabel lblRightclickTo = new JLabel("* Right-click to clear hotkeys");
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(separator, -1, 414, 32767)
                        .addComponent(this.selectedSoundClipLabel, -1, 414, 32767)
                        .addGroup(groupLayout.createSequentialGroup().addComponent(lblSoundClip)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(btnSelect))
                        .addComponent(lblMacroKeys).addComponent(this.keysTextField, -1, 414, 32767)
                        .addGroup(GroupLayout.Alignment.TRAILING,
                                groupLayout.createSequentialGroup().addComponent(lblRightclickTo)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 311, 32767)
                                        .addComponent(btnDone)))
                        .addContainerGap()));

        groupLayout
                .setVerticalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblSoundClip).addComponent(btnSelect))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(this.selectedSoundClipLabel).addGap(13)
                                .addComponent(separator, -2, -1, -2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(lblMacroKeys)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(this.keysTextField, -2, -1, -2).addGap(19)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnDone).addComponent(lblRightclickTo))
                                .addContainerGap(-1, 32767)));

        getContentPane().setLayout(groupLayout);
        getContentPane().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                SoundboardEntryEditor.this.keysTextField.setBackground(Color.WHITE);
                //GlobalScreen.getInstance().removeNativeKeyListener(SoundboardEntryEditor.this.inputGetter);
                GlobalScreen.removeNativeKeyListener(SoundboardEntryEditor.this.inputGetter);
            }
        });
        pack();
        setLocationRelativeTo(soundboardframe);
        setVisible(true);
    }

    public SoundboardEntryEditor(SoundboardFrame soundboardframe, SoundboardEntry entry) {
        this(soundboardframe);

        this.soundboardEntry = entry;
        this.soundfile = new File(entry.getFileString());
        this.keyNums = entry.activationKeysNumbers;
        this.selectedSoundClipLabel.setText(entry.getFileString());
        this.keysTextField.setText(entry.getActivationKeysAsReadableString());
        pack();
    }

    private void submit() {
        if (this.soundfile != null) {
            if (this.soundboardEntry == null) {
                this.soundboard.addEntry(this.soundfile, this.keyNums);
                this.soundboardframe.updateSoundboardTable();
            } else {
                this.soundboardEntry.setFile(this.soundfile);
                this.soundboardEntry.setActivationKeys(this.keyNums);
                this.soundboardframe.updateSoundboardTable();
            }
        }
        dispose();
    }

    private void multiAdd(File[] files) {
        File[] arrayOfFile;

        int j = (arrayOfFile = files).length;
        for (int i = 0; i < j; i++) {
            File file = arrayOfFile[i];
            this.soundboard.addEntry(file, null);
        }
        this.soundboardframe.updateSoundboardTable();
        dispose();
    }

    public void dispose() {
        super.dispose();
        //GlobalScreen.getInstance().removeNativeKeyListener(this.inputGetter);
        GlobalScreen.removeNativeKeyListener(this.inputGetter);
    }

    private class NativeKeyInputGetter implements NativeKeyListener {
        int pressedKeys = 0;
        ArrayList<Integer> pressedKeyNums = new ArrayList<Integer>();
        ArrayList<String> pressedKeyNames = new ArrayList<String>();

        private NativeKeyInputGetter() {
        }

        public void nativeKeyPressed(NativeKeyEvent e) {
            if (this.pressedKeys <= 0) {
                this.pressedKeyNames.clear();
                this.pressedKeyNums.clear();
            }
            this.pressedKeys += 1;
            int key = e.getKeyCode();
            String keyname = NativeKeyEvent.getKeyText(key);
            System.out.println("key pressed: " + key + " " + keyname);
            for (Integer i : this.pressedKeyNums) {
                if (i.intValue() == key) {
                    return;
                }
            }
            this.pressedKeyNums.add(Integer.valueOf(key));
            this.pressedKeyNames.add(keyname);
            updateTextField();
            int[] macroKeys = new int[this.pressedKeyNums.size()];
            for (int i = 0; i < macroKeys.length; i++) {
                macroKeys[i] = this.pressedKeyNums.get(i).intValue();
            }
            SoundboardEntryEditor.this.keyNums = macroKeys;
        }

        public void nativeKeyReleased(NativeKeyEvent e) {
            this.pressedKeys -= 1;
            if (this.pressedKeys < 0) {
                this.pressedKeys = 0;
            }
            int key = e.getKeyCode();
            this.pressedKeyNums.remove(new Integer(key));
            this.pressedKeyNames.remove(NativeKeyEvent.getKeyText(key));
        }

        public void nativeKeyTyped(NativeKeyEvent arg0) {
        }

        public void clearPressedKeys() {
            this.pressedKeys = 0;
            this.pressedKeyNames.clear();
            this.pressedKeyNums.clear();
        }

        private synchronized void updateTextField() {
            String allKeys = "";
            for (String key : this.pressedKeyNames) {
                allKeys = allKeys.concat(key + "+");
            }
            allKeys = allKeys.substring(0, allKeys.length() - 1);
            SoundboardEntryEditor.this.keysTextField.setText(allKeys);
        }
    }

    private class AudioClipFileFilter extends FileFilter {
        private AudioClipFileFilter() {
        }

        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            String filename = file.getName().toLowerCase();
            return (filename.endsWith(".wav")) || (filename.endsWith(".mp3"));
        }

        public String getDescription() {
            return ".mp3 or uncompressed .wav";
        }
    }
}

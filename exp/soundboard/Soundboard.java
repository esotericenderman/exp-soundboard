package exp.soundboard;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.jnativehook.keyboard.NativeKeyEvent;

public class Soundboard {
	private ArrayList<SoundboardEntry> soundboardEntries;
	private static ArrayList<SoundboardEntry> soundboardEntriesClone = new ArrayList<SoundboardEntry>();
	private static boolean containsPPTKey = false;
	private static ArrayList<Integer> pttKeysClone = new ArrayList<Integer>();

	public Soundboard() {
		this.soundboardEntries = new ArrayList<SoundboardEntry>();
	}

	public Object[][] getEntriesAsObjectArrayForTable() {
		Object[][] array = new Object[this.soundboardEntries.size()][4];
		for (int i = 0; i < array.length; i++) {
			SoundboardEntry entry = (SoundboardEntry) this.soundboardEntries.get(i);
			array[i][0] = entry.getFileName();
			array[i][1] = entry.getActivationKeysAsReadableString();
			array[i][2] = entry.getFileString();
			array[i][3] = Integer.valueOf(i);
		}
		return array;
	}

	public void addEntry(File file, int[] keyNumbers) {
		this.soundboardEntries.add(new SoundboardEntry(file, keyNumbers));
	}

	public SoundboardEntry getEntry(String filename) {
		for (SoundboardEntry entry : this.soundboardEntries) {
			if (entry.getFileName().equals(filename)) {
				return entry;
			}
		}
		return null;
	}

	public void removeEntry(int index) {
		this.soundboardEntries.remove(index);
	}

	public void removeEntry(String filename) {
		for (SoundboardEntry entry : this.soundboardEntries) {
			if (entry.getFileName().equals(filename)) {
				this.soundboardEntries.remove(entry);
				break;
			}
		}
	}

	public ArrayList<SoundboardEntry> getSoundboardEntries() {
		return this.soundboardEntries;
	}

	public File saveAsJsonFile(File file) {
		String filestring = file.getAbsolutePath();
		System.out.println(filestring);
		if (filestring.contains(".")) {
			filestring = filestring.substring(0, filestring.lastIndexOf('.'));
		}
		filestring = filestring + ".json";
		System.out.println("amended: " + filestring);
		Gson gson = new Gson();

		String json = gson.toJson(this);
		File realfile = new File(filestring);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(realfile));
			writer.write(json);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return realfile;
	}

	public static Soundboard loadFromJsonFile(File file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Gson json = new Gson();

		Soundboard sb = (Soundboard) json.fromJson(br, Soundboard.class);
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb;
	}

	public SoundboardEntry getEntry(int index) {
		try {
			return (SoundboardEntry) this.soundboardEntries.get(index);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean entriesContainPTTKeys(ArrayList<Integer> pttkeys)
   {
     if ((!pttkeys.equals(pttKeysClone)) || (hasSoundboardChanged())) {
       soundboardEntriesClone = (ArrayList)this.soundboardEntries.clone();
       pttKeysClone = (ArrayList)pttkeys.clone();
       String key = null;
       int j; int i; for (Iterator localIterator1 = this.soundboardEntries.iterator(); localIterator1.hasNext(); 
           i < j)
       {
         SoundboardEntry entry = (SoundboardEntry)localIterator1.next();
         int[] arrayOfInt; j = (arrayOfInt = entry.getActivationKeys()).length;i = 0; continue;int actKey = arrayOfInt[i];
         key = NativeKeyEvent.getKeyText(actKey).toLowerCase();
         for (Iterator localIterator2 = pttkeys.iterator(); localIterator2.hasNext();) { int i = ((Integer)localIterator2.next()).intValue();
           if (key.equals(KeyEventIntConverter.getKeyEventText(i).toLowerCase())) {
             containsPPTKey = true;
             return true;
           }
         }
         i++;
       }
       
 
 
 
 
 
 
 
       containsPPTKey = false;
       return false; }
     if (containsPPTKey) {
       return true;
     }
     return false;
   }

	public boolean hasSoundboardChanged() {
		if (!this.soundboardEntries.equals(soundboardEntriesClone)) {
			System.out.println("Soundboard changed");
			return true;
		}
		return false;
	}
}

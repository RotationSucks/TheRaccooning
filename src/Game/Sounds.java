package Game;

import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

// FIXME wenn er das erste mal audio abspielt, h�ngts leicht, kA evtl muss der
// dann erst den musicManager initialisieren oder kA
// wenn dann bg music von anfang an is, sollts doch hin haun... kA

public class Sounds {
	
	private static final Sounds	instance	= new Sounds();
	
	public static Sounds getInstance() {
		return instance;
	}
	
	private HashMap<String, Audio>	sounds	= new HashMap<String, Audio>();
	
	private Sounds() {
		try {
			loadAudioFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isPlaying(String sound) {
		return sounds.get(sound).isPlaying();
	}
	
	private void loadAudioFiles() throws SlickException, IOException {
		// itemCollected = new Sound("audio/part_collected.wav");
		// bgMusic = new Sound("audio/Part 1_ Loop _Lang.wav");
		putSound("jump");
		putSound("pigdeath");
		putSound("laser", "audio/laser3.ogg");
		putSound("death");
		putSound("bite");
		putSound("pigaggro");
		putSound("tailwhip");
		putSound("fence");
	}
	
	public void loop(String sound, float pitch, float volume) {
		sounds.get(sound).playAsSoundEffect(pitch, volume, true);
	}
	
	public void play(String sound, float pitch, float volume) {
		sounds.get(sound).playAsSoundEffect(pitch, volume, false);
	}
	
	private void putSound(String name) throws IOException {
		putSound(name, "audio/" + name + ".ogg");
	}
	
	private void putSound(String name, String path) throws IOException {
		sounds.put(name, AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(path)));
	}
	
	public void stop(String sound) {
		sounds.get(sound).stop();
	}
	
}
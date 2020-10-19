package net.colonymc.api;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.colonymc.api.book.survey.BookActionCommand;
import net.colonymc.api.image.Image;
import net.colonymc.api.image.ImageMap;
import net.colonymc.api.player.ColonyPlayer;
import net.colonymc.api.player.PublicHologram;
import net.colonymc.colonyapi.MainDatabase;

public class Main extends JavaPlugin {
	
	static Main instance;
	File names = new File(this.getDataFolder(), "names.yml");
	private File imgs = new File(this.getDataFolder(), "/imgs");
	FileConfiguration namesConfig;
	boolean started = false;
	
	public void onEnable() {
		instance = this;
		if(MainDatabase.isConnected()) {
			Main.this.saveDefaultConfig();
			setupConfigFiles();
			setupListeners();
			setupCommands();
			setupImages();
			new AutomaticRestart();
			started = true;
			System.out.println(" � ColonySpigotAPI has been enabled successfully!");
		}
		else {
			System.out.println(" � ColonySpigotAPI couldn't connect to the main database!");
		}
	}

	public void onDisable() {
		if(started) {
			destroyHolograms();
		}
		System.out.println(" � ColonySpigotAPI has been disabled successfully!");
	}
	
	private void setupImages() {
		if(imgs != null) {
			for(File f : imgs.listFiles()) {
				try {
					BufferedImage img = ImageIO.read(f);
					if(img != null) {
						new Image(FilenameUtils.removeExtension(f.getName()), img);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setupListeners() {
		Bukkit.getPluginManager().registerEvents(new ImageMap(), this);
		Bukkit.getPluginManager().registerEvents(new ColonyPlayer(), this);
		Bukkit.getPluginManager().registerEvents(new PublicHologram(), this);
	}
	
	private void setupCommands() {
		getCommand("bookaction").setExecutor(new BookActionCommand());
	}
	
	private void destroyHolograms() {
		int size = PublicHologram.holos.size();
		for(int i = 0; i < size; i++) {
			PublicHologram p = PublicHologram.holos.get(0);
			p.destroy();
		}
		
	}
	
	private void setupConfigFiles() {
		if(!names.exists()) {
			names.getParentFile().mkdirs();
			saveResource("names.yml", false);
		}
		namesConfig = new YamlConfiguration();
		try {
			namesConfig.load(names);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public FileConfiguration getNamesConfig() {
		return namesConfig;
	}
	
	public File getImgs() {
		return imgs;
	}

}

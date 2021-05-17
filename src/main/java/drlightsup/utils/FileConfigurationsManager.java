package drlightsup.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class FileConfigurationsManager {

	private Set<String> configurationFilesNames;
	private JavaPlugin plugin;
	private Map<String, FileConfiguration> configurations;
	
	private static FileConfigurationsManager instance = null;
	
	private FileConfigurationsManager(JavaPlugin plugin) {
		this.configurationFilesNames = new HashSet<String>();
		this.configurations = new HashMap<String, FileConfiguration>();
		this.plugin = plugin;
	}
	
	public static FileConfigurationsManager getInstance(JavaPlugin plugin) {
		if (instance == null) {
			instance = new FileConfigurationsManager(plugin);
		}
		return instance;
	}
	
	public static FileConfigurationsManager getInstance() {
		return instance;
	}
	
	public void reloadFiles(Collection<String> filesNames) {
		for (String fileName : filesNames) {
			reloadFile(fileName);
		}
	}
	
	public FileConfiguration reloadFile(String fileName) {
		if (!doesFileExist(fileName)) {
			return null;
		}
		configurationFilesNames.add(fileName);
		File configFile = new File(this.plugin.getDataFolder(), fileName);
		FileConfiguration config = new YamlConfiguration();
		try {
			config.load(configFile);
			this.configurations.put(fileName, config);
			return config;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void createConfigurationFiles(Collection<String> fileNames) {
		for (String fileName : fileNames) {
			createConfigurationFile(fileName);
		}
	}
	
	public boolean createConfigurationFile(String name) {
		File configFile = new File(this.plugin.getDataFolder(), name);
		this.configurationFilesNames.add(name);
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			this.plugin.saveResource(name, false);
			return false;
		}
		return true;
	}
	
	private boolean doesFileExist(String name) {
		return new File(this.plugin.getDataFolder(), name).exists();
	}
	
	public void reloadAllFiles() {
		reloadFiles(this.configurationFilesNames);
	}
	
	public FileConfiguration getFileConfiguration(String name) {
		return this.configurations.get(name);
	}
	
	public <T extends ConfigurationSerializable> boolean addConfigurationSerializablesToConfiguration(String name, Map<String, T> serializables) {
		FileConfiguration conf = getFileConfiguration(name);
		for (Entry<String, T> serializable : serializables.entrySet()) {
			conf.set(serializable.getKey(), serializable.getValue());
		}
		try {
			conf.save(new File(this.plugin.getDataFolder(), name));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public <T extends ConfigurationSerializable> Map<String, T> getMapOfConfigurationSerializablesFromConfiguration(String name, Collection<String> paths, Class<ConfigurationSerializable> T) {
		FileConfiguration conf = getFileConfiguration(name);
		Map<String, T> configurationSerializables = new HashMap<>();
		for (String path : paths) {
			@SuppressWarnings("unchecked")
			T configurationSerializable = (T) conf.getSerializable(path, T);
			if (configurationSerializable == null) {
				continue;
			}
			configurationSerializables.put(path, configurationSerializable);
		}
		return configurationSerializables;
	}
	
	public void registerConfigurationSerializables(Collection<Class<? extends ConfigurationSerializable>> classes) {
		for (Class<? extends ConfigurationSerializable> class1 : classes) {
			ConfigurationSerialization.registerClass(class1);
		}
	}
	
	public Map<String, Object> getMapFromPath(String filename, String path) {
		FileConfiguration conf = getFileConfiguration(filename);
		if (conf == null) {
			return null;
		}
		if (path == null) {
			return conf.getValues(false);
		}
		return conf.getConfigurationSection(path).getValues(false);
	}
	
	public void setMapOfSerializables(String filename, Map<String, Object> serializationMap) {
		FileConfiguration conf = getFileConfiguration(filename);
		for (String path : serializationMap.keySet()) {
			conf.set(path, serializationMap.get(path));
		}
	}
	
	public boolean saveFileConfigurationToFile(String filename) {
		FileConfiguration conf = getFileConfiguration(filename);
		try {
			conf.save(new File(this.plugin.getDataFolder(), filename));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void clearFileConfigurationOfFile(String filename) {
		FileConfiguration conf = getFileConfiguration(filename);
		for (String key : conf.getKeys(false)) {
			conf.set(key, null);
		}
	}
}

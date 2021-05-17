package drlightsup;

import org.bukkit.plugin.java.JavaPlugin;

import drlightsup.commands.DRLightsUpCommands;
import drlightsup.listeners.LightsPutListener;
import drlightsup.management.LightsManager;
import drlightsup.utils.FileConfigurationsManager;

public class DRLightsUp extends JavaPlugin {

	@Override
	public void onEnable() {
		super.onEnable();
		
		FileConfigurationsManager fcm = FileConfigurationsManager.getInstance(this);
		fcm.createConfigurationFile("config.yml");
		fcm.createConfigurationFile("lights.yml");
		fcm.reloadAllFiles();
		LightsManager.getInstance().deserialize(fcm.getMapFromPath("lights.yml", null));
		getServer().getPluginManager().registerEvents(LightsPutListener.getInstance(), this);
		LightsPutListener.getInstance().reloadMaterialsLights();
		
		getServer().getPluginCommand("DRLightsUp").setExecutor(new DRLightsUpCommands());
		
		System.out.println("Plugin has been enabled successfully!");
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		FileConfigurationsManager fcm = FileConfigurationsManager.getInstance(this);
		fcm.clearFileConfigurationOfFile("lights.yml");
		fcm.setMapOfSerializables("lights.yml", LightsManager.getInstance().serialize());
		fcm.saveFileConfigurationToFile("lights.yml");
		System.out.println("Plugin has been disabled successfully!");
	}
	
}

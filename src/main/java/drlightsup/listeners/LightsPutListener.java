package drlightsup.listeners;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import drlightsup.management.LightsManager;
import drlightsup.utils.FileConfigurationsManager;

public class LightsPutListener implements Listener {
	
	private static final String configurationFileName = "config.yml";

	private Collection<Material> materialsLights;
	
	private static LightsPutListener instance;
	
	private LightsPutListener() {
		this.materialsLights = new HashSet<>();
	}
	
	public static LightsPutListener getInstance() {
		if (instance == null) {
			instance = new LightsPutListener();
		}
		return instance;
	}
	
	public void reloadMaterialsLights() {
		FileConfiguration fileConfiguration = FileConfigurationsManager.getInstance().reloadFile(configurationFileName);
		List<String> materials = fileConfiguration.getStringList("lightItems");
		for (String materialName : materials) {
			Material material = Material.getMaterial(materialName);
			if (material == null) {
				System.out.println("Invalid material: \"" + materialName + "\"!");
				continue;
			}
			this.materialsLights.add(material);
		}
	}
	
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event) {
		if (LightsManager.getInstance().isInLightMode(event.getPlayer().getUniqueId()) && this.materialsLights.contains(event.getMaterial())) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				onPlayerRightClickWithValidMaterial(event);
			} else {
				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					onPlayerLeftClickWithValidMaterial(event);
				}
			}
		}
	}
	
	public void onPlayerRightClickWithValidMaterial(PlayerInteractEvent event) {
		Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
		LightsManager.getInstance().addLightPowerInLocation(location, 1);
		event.setCancelled(true);
	}
	
	public void onPlayerLeftClickWithValidMaterial(PlayerInteractEvent event) {
		Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
		if (LightsManager.getInstance().doesLightSourceExistInLocation(location)) {
			LightsManager.getInstance().removeLightPowerInLocation(location, 1);
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Location location = event.getBlockPlaced().getLocation();
		if (LightsManager.getInstance().doesLightSourceExistInLocation(location)) {
			LightsManager.getInstance().removeLightSource(location);
		}
	}
	
}

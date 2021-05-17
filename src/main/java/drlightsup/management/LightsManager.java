package drlightsup.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

import drlightsup.management.lightsources.BasicLightSource;
import drlightsup.management.lightsources.LightSource;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

public class LightsManager {

	private Map<Location, LightSource> lightSources;
	
	private Collection<UUID> playersInLightMode;
	
	private static LightsManager instance = null;
	
	private LightsManager() {
		this.lightSources = new HashMap<>();
		this.playersInLightMode = new HashSet<>();
	}
	
	public static LightsManager getInstance() {
		if (instance == null) {
			instance = new LightsManager();
		}
		return instance;
	}
	
	private LightSource createLightSource(Location location, int lightPower) {
		LightSource prevLightSource = removeLightSource(location);
		createLightSourceInLightAPI(location, lightPower, false);
		this.lightSources.put(location, new BasicLightSource(lightPower));
		return prevLightSource;
	}
	
	public LightSource removeLightSource(Location location) {
		if (doesLightSourceExistInLocation(location)) {
			LightAPI.deleteLight(location, LightType.BLOCK, true);
			this.lightSources.remove(location);
		}
		return null;
	}
	
	public boolean doesLightSourceExistInLocation(Location location) {
		return this.lightSources.containsKey(location);
	}
	
	private LightSource getLightSourceInLocation(Location location) {
		return this.lightSources.get(location);
	}
	
	public int addLightPowerInLocation(Location location, int lightPowerDelta) throws IllegalArgumentException {
		if (lightPowerDelta <= 0) {
			throw new IllegalArgumentException("NegativeLightPowerDelta");
		}
		LightSource lightSource = getLightSourceInLocation(location);
		if (lightSource == null) {
			if (lightPowerDelta > 15) {
				lightPowerDelta = 15;
			}
			createLightSource(location, lightPowerDelta);
			lightSource = getLightSourceInLocation(location);
		} else {
			if (lightSource.getLightPower() + lightPowerDelta > 15) {
				lightPowerDelta = 15 - lightSource.getLightPower();
			}
			lightSource.addLightPower(lightPowerDelta);
			createLightSourceInLightAPI(location, lightSource.getLightPower(), true);
		}
		return lightSource.getLightPower();
	}
	
	public int removeLightPowerInLocation(Location location, int lightPowerDelta) throws IllegalArgumentException {
		if (lightPowerDelta <= 0) {
			throw new IllegalArgumentException("NegativeLightPowerDelta");
		}
		LightSource lightSource = getLightSourceInLocation(location);
		if (lightSource == null) {
			throw new IllegalArgumentException("LightSourceDoesntExist");
		}
		if (lightSource.getLightPower() < lightPowerDelta) {
			throw new IllegalArgumentException("NegativeLightPower");
		}
		if (lightSource.getLightPower() == lightPowerDelta) {
			removeLightSource(location);
			return 0;
		}
		lightSource.removeLightPower(lightPowerDelta);
		createLightSourceInLightAPI(location, lightSource.getLightPower(), true);
		return lightSource.getLightPower();
	}
	
	public boolean isInLightMode(UUID uuid) {
		return this.playersInLightMode.contains(uuid);
	}
	
	public boolean toggleLightMode(UUID uuid) {
		if (isInLightMode(uuid)) {
			this.playersInLightMode.remove(uuid);
			return false;
		} else {
			this.playersInLightMode.add(uuid);
			return true;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void createLightSourceInLightAPI(Location location, int lightPower, boolean shouldDeleteBeforeCreating) {
		if (shouldDeleteBeforeCreating) {
			LightAPI.deleteLight(location, LightType.BLOCK, true);
		}
		LightAPI.createLight(location, LightType.BLOCK, lightPower, true);
		for(ChunkInfo info : LightAPI.collectChunks(location, LightType.BLOCK, lightPower)){
			LightAPI.updateChunks(info);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> serialize() {
		Map<String, Object> serializationMap = new HashMap<>();
		for (Map.Entry<Location, LightSource> entry : this.lightSources.entrySet()) {
			String lightPowerStr = Integer.toString(entry.getValue().getLightPower());
			if (!serializationMap.containsKey(lightPowerStr)) {
				serializationMap.put(lightPowerStr, new ArrayList<Location>());
			}
			((List<Location>)serializationMap.get(Integer.toString(entry.getValue().getLightPower()))).add(entry.getKey());
		}
		return serializationMap;
	}
	
	public boolean deserialize(Map<String, Object> serializationMap) {
		if (serializationMap == null) {
			return false;
		}
		for (Map.Entry<String, Object> entry : serializationMap.entrySet()) {
			int lightPower;
			try {
				lightPower = Integer.parseInt(entry.getKey());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return false;
			}
			if (!(entry.getValue() instanceof List)) {
				return false;
			}
			List<?> locations = (List<?>)entry.getValue();
			for (Object object : locations) {
				if (object instanceof Location) {
					createLightSource((Location)object, lightPower);
				}
			}
		}
		return true;
	}

}

package drlightsup.management.lightsources;

public interface LightSource {

	int getLightPower();
	
	LightSource addLightPower(int lightPower);
	
	LightSource removeLightPower(int lightPower);
	
}

package drlightsup.management.lightsources;

public class BasicLightSource implements LightSource {

	private int lightPower;
	
	public BasicLightSource(int lightPower) {
		this.lightPower = lightPower;
	}

	@Override
	public int getLightPower() {
		return this.lightPower;
	}

	@Override
	public LightSource addLightPower(int lightPower) {
		this.lightPower += lightPower;
		return this;
	}

	@Override
	public LightSource removeLightPower(int lightPower) {
		this.lightPower -= lightPower;
		return this;
	}
	
}

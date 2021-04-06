package cc.morgue.lwjgl2x.gl.threed.model.obj.builder;

import cc.morgue.lwjgl2x.gl.threed.model.obj.parser.BuilderInterface;

public class Material {

	public String name;
	public ReflectivityTransmiss ka = new ReflectivityTransmiss();
	public ReflectivityTransmiss kd = new ReflectivityTransmiss();
	public ReflectivityTransmiss ks = new ReflectivityTransmiss();
	public ReflectivityTransmiss tf = new ReflectivityTransmiss();
	public int illumModel = 0;
	public boolean dHalo = false;
	public double dFactor = 0.0;
	public double nsExponent = 0.0;
	public double sharpnessValue = 0.0;
	public double niOpticalDensity = 0.0;
	public String mapKaFilename = null;
	public String mapKdFilename = null;
	public String mapKsFilename = null;
	public String mapNsFilename = null;
	public String mapDFilename = null;
	public String decalFilename = null;
	public String dispFilename = null;
	public String bumpFilename = null;
	public int reflType = BuilderInterface.MTL_REFL_TYPE_UNKNOWN;
	public String reflFilename = null;

	public Material(String name) {
		this.name = name;
	}
}
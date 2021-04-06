package cc.morgue.lwjgl2x.gl.threed.model.obj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelCache {

	private static ModelCache INSTANCE;
	
	private Map<String, List<VBO>> loadedModelVBOs = new HashMap<String, List<VBO>>();
	
	public ModelCache() {
		INSTANCE = this;
	}
	
	public List<VBO> getLoadedModelVBOs(String model) {
		return loadedModelVBOs.get(model);
	}
	
	public void addLoadedModelVBOs(String model, List<VBO> vbos) {
		this.loadedModelVBOs.put(model, vbos);
	}
	
	public Map<String, List<VBO>> getLoadedModelVBOs() {
		return loadedModelVBOs;
	}
	
	public static ModelCache getInstance() {
		return INSTANCE;
	}
}

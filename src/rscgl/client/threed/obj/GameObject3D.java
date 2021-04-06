package rscgl.client.threed.obj;

import cc.morgue.lwjgl2x.gl.threed.Frustum;
import cc.morgue.lwjgl2x.gl.threed.model.obj.Model;
import rscgl.Main;
import rscgl.client.threed.MudClient3D;

public class GameObject3D {

	/**
	 * The 3D game object model.
	 */
	private final Model model;
	
	/**
	 * The plane in which the model exists.
	 */
	private final int plane;
	
	public GameObject3D(MudClient3D client, int id, int x, int z, float tileHeight, int plane) {
		//this.model = geilinor.loadObjModel(Main.ASSET_DIRECTORY + "objs/" + id + ".obj", Main.ASSET_DIRECTORY + "objs/" + id + ".png");
		this.model = client.loadObjModel(Main.ASSET_DIRECTORY + "objs/" + id + ".obj", "");
		this.model.setPosition(x, z, tileHeight);
		this.model.setScale(0.01f);
		this.plane = plane;
	}
	
	public void update() {
		model.update();
	}
	
	public void render(Frustum frustum, int plane) {
		// Make sure the model is on the same plane as the camera.
		//this.model.setVisible(this.plane == plane);
	}
	
	public void cleanup() {
		this.model.destroy();
	}
	
}

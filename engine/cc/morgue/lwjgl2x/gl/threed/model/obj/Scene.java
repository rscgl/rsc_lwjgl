package cc.morgue.lwjgl2x.gl.threed.model.obj;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

public class Scene {

	private ArrayList<Model> models = new ArrayList<Model>();

	public Scene() {
		
	}

	public void addModel(Model model) {
		models.add(model);
	}
	
	public void update() {
		for(Model model : models) {
			model.update();
		}
	}

	public void render(boolean drawWireFrame) {
		if (drawWireFrame) {
			GL11.glLineWidth(1.0f); // width of the outline
			GL11.glPolygonOffset(1.0f, 1.0f); 
			GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);

			GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glColor3f(0, 0, 0); // black
			
			for(Model model : models) {
				model.render();
			}

			GL11.glColor3f(1, 1, 1); // white
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			return;
		}

		for(Model model : models) {
			model.render();
		}
	}
	
	/**
	 * Removes all vbo data from memory.
	 */
	public void cleanup() {
		for(Model model : models) {
			model.destroy();
		}
	}
	
	public int getModelCount() {
		return models.size();
	}
}
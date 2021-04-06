package cc.morgue.lwjgl2x.gl.threed.model.obj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.util.vector.Vector3f;

import cc.morgue.lwjgl2x.gl.texture.TextureManager;
import cc.morgue.lwjgl2x.gl.threed.model.obj.builder.Build;
import cc.morgue.lwjgl2x.gl.threed.model.obj.builder.Face;
import cc.morgue.lwjgl2x.gl.threed.model.obj.builder.FaceVertex;
import cc.morgue.lwjgl2x.gl.threed.model.obj.builder.Material;
import cc.morgue.lwjgl2x.gl.threed.model.obj.parser.Parse;

public class ModelLoader {
	
	private final Vector3f position = new Vector3f(0, 0, 0);
	
	public ModelLoader() {
	}

	public Model loadModel(String filename, String defaultTextureMaterial) {
//		String[] filePath = filename.split("/");
//		String modelName = filePath[filePath.length-1];
//		List<VBO> cachedVBOs = ModelCache.getInstance().getLoadedModelVBOs(modelName);
//		
//		if(cachedVBOs != null) {
//			// Found copy of VBOs, but make a new model instance (for new position and whatever else)
//			if(cachedVBOs.size() > 1) { 
//				return new ComplexModel(cachedVBOs, position);
//			} else {
//				return new BasicModel(cachedVBOs.get(0), position);
//			}
//		}
//		
//		Model model;
//		Build builder = new Build();
//		try {
//			new Parse(builder, filename);
//		} catch (FileNotFoundException e) {
//			System.err.println("Exception loading object!  e=" + e);
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.err.println("Exception loading object!  e=" + e);
//			e.printStackTrace();
//		}
//
//		ArrayList<ArrayList<Face>> facesByTextureList = createFaceListsByMaterial(builder);
//		ArrayList<VBO> vbos = new ArrayList<VBO>();
//		
//		int defaultTextureID = 0; // TODO?
//		if(facesByTextureList.size() > 1) {
//			for (ArrayList<Face> faceList : facesByTextureList) {
//				if (faceList.isEmpty()) {
//					System.err.println("ERROR: got an empty face list.  That shouldn't be possible.");
//					continue;
//				}
//				ArrayList<Face> triangleList = splitQuads(faceList);
//				calcMissingVertexNormals(triangleList);
//	
//				if (triangleList.size() <= 0) {
//					continue;
//				}
//	
//				vbos.add(VBOFactory.build(getMaterialID(faceList.get(0).material, defaultTextureID, builder), triangleList));
//			}
//			
//			model = new ComplexModel(vbos, position);
//			ModelCache.getInstance().addLoadedModelVBOs(modelName, vbos);
//		} else {
//			ArrayList<Face> triangleList = splitQuads(facesByTextureList.get(0));
//			calcMissingVertexNormals(triangleList);
//			
//			vbos.add(VBOFactory.build(getMaterialID(triangleList.get(0).material, defaultTextureID, builder), triangleList));
//			
//			model = new BasicModel(vbos.get(0), position);
//			ModelCache.getInstance().addLoadedModelVBOs(modelName, vbos);
//		}
//		
//		return model;
		Model model = null;
		
		Build builder = new Build();
		try {
			new Parse(builder, filename);
		} catch (java.io.FileNotFoundException e) {
			System.err.println("Exception loading object!  e=" + e);
			e.printStackTrace();
		} catch (java.io.IOException e) {
			System.err.println("Exception loading object!  e=" + e);
			e.printStackTrace();
		}

		ArrayList<ArrayList<Face>> facesByTextureList = createFaceListsByMaterial(builder);

		int defaultTextureID = 0; // TODO?
		
		if(facesByTextureList.size() > 1) {
			List<VBO> createdVBOS = new ArrayList<VBO>();
			
			for (ArrayList<Face> faceList : facesByTextureList) {
				if (faceList.isEmpty()) {
					System.err.println("ERROR: got an empty face list.  That shouldn't be possible.");
					continue;
				}
				ArrayList<Face> triangleList = splitQuads(faceList);
				calcMissingVertexNormals(triangleList);
	
				if (triangleList.size() <= 0) {
					continue;
				}
	
				createdVBOS.add(VBOFactory.build(getMaterialID(faceList.get(0).material, defaultTextureID, builder), triangleList));
			}
			
			model = new ComplexModel(createdVBOS, new Vector3f(0, 0, 0)); // TODO position
		} else {
			ArrayList<Face> triangleList = splitQuads(facesByTextureList.get(0));
			calcMissingVertexNormals(triangleList);
			
			model = new BasicModel(VBOFactory.build(getMaterialID(triangleList.get(0).material, defaultTextureID, builder), triangleList), new Vector3f(0, 0, 0)); // TODO position
		}
		
		return model;
	}
	
	private ArrayList<ArrayList<Face>> createFaceListsByMaterial(Build builder) {
		ArrayList<ArrayList<Face>> list = new ArrayList<ArrayList<Face>>();
		ArrayList<Face> currList;
		
		for(Face face : builder.faces) {
			currList = getFaceListForMaterial(list, face.material);
			
			// No list for this material yet, make one
			if(currList == null) {
				ArrayList<Face> faceList = new ArrayList<Face>();
				
				// Add this face to our new list
				faceList.add(face);
				
				// Add new list to our list of "face lists"
				list.add(faceList);
			} else {
				// Add this face to existing list
				currList.add(face);
			}
		}
		
		return list;
	}
	
	private ArrayList<Face> getFaceListForMaterial(ArrayList<ArrayList<Face>> faceLists, Material mat) {
		for(ArrayList<Face> list : faceLists) {
			if(!list.isEmpty() && list.get(0).material == mat) {
				return list;
			}
		}
		
		return null;
	}

	// @TODO: This is a crappy way to calculate vertex normals if we are missing said normals.  I just wanted 
	// something that would add normals since my simple VBO creation code expects them.  There are better ways
	// to generate normals,  especially given that the .obj file allows specification of "smoothing groups".
	public void calcMissingVertexNormals(ArrayList<Face> triangleList) {
		for (Face face : triangleList) {
			face.calculateTriangleNormal();
			for (int loopv = 0; loopv < face.vertices.size(); loopv++) {
				FaceVertex fv = face.vertices.get(loopv);
				if (face.vertices.get(0).n == null) {
					FaceVertex newFv = new FaceVertex();
					newFv.v = fv.v;
					newFv.t = fv.t;
					newFv.n = face.faceNormal;
					face.vertices.set(loopv, newFv);
				}
			}
		}
	}

	// load and bind the texture we will be using as a default texture for any missing textures, unspecified textures, and/or 
	// any materials that are not textures, since we are pretty much ignoring/not using those non-texture materials.
	//
	// In general in this simple test code we are only using textures, not 'colors' or (so far) any of the other multitude of things that
	// can be specified via 'materials'. 
	public int setUpDefaultTexture(String defaultTextureMaterial) {
		int defaultTextureID = -1;
		
		if(defaultTextureMaterial.equals("")) {
			return defaultTextureID;
		}
		
		try {
			defaultTextureID = TextureManager.getInstance().load("defaultTexture", defaultTextureMaterial, true).getTextureId();
		} catch (IOException ex) {
			Logger.getLogger(ModelLoader.class.getName()).log(Level.SEVERE, null, ex);
			System.err.println("ERROR: Got an exception trying to load default texture material = " + defaultTextureMaterial + " , ex=" + ex);
			ex.printStackTrace();
		}
		//System.err.println("INFO:  default texture ID = " + defaultTextureID);
		return defaultTextureID;
	}

	// Get the specified Material, bind it as a texture, and return the OpenGL ID.  Returns he default texture ID if we can't
	// load the new texture, or if the material is a non texture and hence we ignore it.  
	public int getMaterialID(Material material, int defaultTextureID, Build builder) {
		int currentTextureID;
		if (material == null) {
			currentTextureID = defaultTextureID;
		} else if (material.mapKdFilename == null || material.mapKdFilename.equals("")) {
			currentTextureID = defaultTextureID;
		} else {
			try {
				File mapKdFile = new File(new File(builder.objFilename).getParent(), material.mapKdFilename);
				currentTextureID = TextureManager.getInstance().load(material.mapKdFilename, mapKdFile.getAbsolutePath(), false).getTextureId();
			} catch (IOException ex) {
				Logger.getLogger(ModelLoader.class.getName()).log(Level.SEVERE, null, ex);
				System.err.println("ERROR: Got an exception trying to load  texture material = '" + material.mapKdFilename + "' , ex=" + ex);
				ex.printStackTrace();
				System.err.println("ERROR: Using default texture ID = " + defaultTextureID);
				currentTextureID = defaultTextureID;
			}
		}
		return currentTextureID;
	}

	// VBOFactory can only handle triangles, not faces with more than 3 vertices.  There are much better ways to 'triangulate' polygons, that
	// can be used on polygons with more than 4 sides, but for this simple test code justsplit quads into two triangles 
	// and drop all polygons with more than 4 vertices.  (I was originally just dropping quads as well but then I kept ending up with nothing
	// left to display. :-)  Or at least, not much. )
	public ArrayList<Face> splitQuads(ArrayList<Face> faces) {
		ArrayList<Face> triangleList = new ArrayList<Face>();
		
		for (Face face : faces) {
			if (face.vertices.size() == 3) {
				triangleList.add(face);
			} else if (face.vertices.size() == 4) {
				FaceVertex v1 = face.vertices.get(0);
				FaceVertex v2 = face.vertices.get(1);
				FaceVertex v3 = face.vertices.get(2);
				FaceVertex v4 = face.vertices.get(3);
				
				Face f1 = new Face();
				f1.map = face.map;
				f1.material = face.material;
				f1.add(v1);
				f1.add(v2);
				f1.add(v3);
				triangleList.add(f1);
				
				Face f2 = new Face();
				f2.map = face.map;
				f2.material = face.material;
				f2.add(v3);
				f2.add(v4);
				f2.add(v1);
				triangleList.add(f2);
			} else {
				// More than 4 verts to a face.. oops
			}
		}
		
		return triangleList;
	}

}

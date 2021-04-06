package cc.morgue.lwjgl2x.gl.threed.model.obj;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import cc.morgue.lwjgl2x.gl.threed.model.obj.builder.Face;
import cc.morgue.lwjgl2x.gl.threed.model.obj.builder.FaceVertex;

public class VBOFactory {

	public static VBO build(int textureID, ArrayList<Face> triangles) {
		//	System.err.println("VBOFactory.build: building a vbo!");

		if (triangles.size() <= 0) {
			throw new RuntimeException("Can not build a VBO if we have no triangles with which to build it.");
		}

		// Now sort out the triangle/vertex indices, so we can use a
		// VertexArray in our VBO.  Note the following is NOT the most efficient way
		// to do this, but hopefully it is clear.  

		// First build a map of the unique FaceVertex objects, since Faces may share FaceVertex objects.
		// And while we're at it, assign each unique FaceVertex object an index as we run across them, storing
		// this index in the map, for use later when we build the "index" buffer that refers to the vertice buffer.
		// And lastly, keep a list of the unique vertice objects, in the order that we find them in.  
		HashMap<FaceVertex, Integer> indexMap = new HashMap<FaceVertex, Integer>();
		int nextVertexIndex = 0;
		ArrayList<FaceVertex> faceVertexList = new ArrayList<FaceVertex>();
		for (Face face : triangles) {
			for (FaceVertex vertex : face.vertices) {
				if (!indexMap.containsKey(vertex)) {
					indexMap.put(vertex, nextVertexIndex++);
					faceVertexList.add(vertex);
				}
			}
		}

		// Now build the buffers for the VBO/IBO
		int verticeAttributesCount = nextVertexIndex;
		int indicesCount = triangles.size() * 3;

		@SuppressWarnings("unused")
		int numMIssingNormals = 0;
		@SuppressWarnings("unused")
		int numMissingUV = 0;
		FloatBuffer verticeAttributes;
		//System.out.println("VBOFactory.build: Creating buffer of size " + verticeAttributesCount + " vertices at " + VBO.ATTR_SZ_FLOATS + " floats per vertice for a total of " + (verticeAttributesCount * VBO.ATTR_SZ_FLOATS) + " floats.");
		verticeAttributes = BufferUtils.createFloatBuffer(verticeAttributesCount * VBO.ATTR_SZ_FLOATS);
		if (null == verticeAttributes) {
			System.err.println("VBOFactory.build: ERROR Unable to allocate verticeAttributes buffer of size " + (verticeAttributesCount * VBO.ATTR_SZ_FLOATS) + " floats.");
		}
		for (FaceVertex vertex : faceVertexList) {
			verticeAttributes.put(vertex.v.x);
			verticeAttributes.put(vertex.v.y);
			verticeAttributes.put(vertex.v.z);
			if (vertex.n == null) {
				// @TODO: What's a reasonable default normal?  Maybe add code later to calculate normals if not present in .obj file.
				verticeAttributes.put(1.0f);
				verticeAttributes.put(1.0f);
				verticeAttributes.put(1.0f);
				numMIssingNormals++;
			} else {
				verticeAttributes.put(vertex.n.x);
				verticeAttributes.put(vertex.n.y);
				verticeAttributes.put(vertex.n.z);
			}
			// @TODO: What's a reasonable default texture coord?  
			if (vertex.t == null) {
				//                verticeAttributes.put(0.5f);
				//                verticeAttributes.put(0.5f);
				verticeAttributes.put((float)Math.random());
				verticeAttributes.put((float)Math.random());
				numMissingUV++;
			} else {
				verticeAttributes.put(vertex.t.u);
				verticeAttributes.put(vertex.t.v);
			}
		}
		verticeAttributes.flip();

		//System.out.println("Had " + numMIssingNormals + " missing normals and " + numMissingUV + " missing UV coords");

		IntBuffer indices;    // indices into the vertices, to specify triangles.
		indices = BufferUtils.createIntBuffer(indicesCount);
		for (Face face : triangles) {
			for (FaceVertex vertex : face.vertices) {
				int index = indexMap.get(vertex);
				indices.put(index);
			}
		}
		indices.flip();

		// Allrighty!  Now give them to OpenGL!
		IntBuffer attribs = BufferUtils.createIntBuffer(1);
		GL15.glGenBuffers(attribs);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, attribs.get(0));
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticeAttributes, GL15.GL_STATIC_DRAW);

		IntBuffer indicies = BufferUtils.createIntBuffer(1);
		GL15.glGenBuffers(indicies);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicies.get(0));
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

		// our copy of the data is no longer necessary, it is safe in OpenGL.  
		// We don't need to null this out but it makes the point.
		verticeAttributes = null;
		indices = null;

		return new VBO(textureID, attribs.get(0), indicies.get(0), indicesCount);
	}
}

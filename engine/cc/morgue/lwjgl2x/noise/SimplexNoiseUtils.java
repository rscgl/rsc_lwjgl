package cc.morgue.lwjgl2x.noise;

public class SimplexNoiseUtils {

	public static float[][] generateOctavedSimplexNoise(SimplexNoise simplex, int width, int height, int octaves, float roughness, float scale) {
		float[][] totalNoise = new float[width][height];
		float layerFrequency = scale;
		float layerWeight = 1;

		for (int octave = 0; octave < octaves; octave++) {
			//Calculate single layer/octave of simplex noise, then add it to total noise
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					totalNoise[x][y] += (float) simplex.noise((float) x * layerFrequency, (float) y * layerFrequency) * layerWeight;
				}
			}

			//Increase variables with each incrementing octave
			layerFrequency *= 2;
			layerWeight *= roughness;
		}
		return totalNoise;
	}

	/**
	 * Calculates the average height value between 4 vertex points.
	 * @param divider The lower the divider, the higher the terrain will appear to be.
	 * @param v1 The first vertex point.
	 * @param v2 The second vertex point.
	 * @param v3 The third vertex point.
	 * @param v4 The fourth vertex point.
	 * @return The sum.
	 */
	public static float getVertexAverage(float divider, float v1, float v2, float v3, float v4) {
		return (v1 + v2 + v3 + v4) / divider;
	}

	/**
	 * Calculates the "centered height" value between an array of vertex points.
	 * @param vertexHeights The array of vertex points
	 * @return The sum.
	 */
	public static float getCenteredVertexHeight(float[] vertexHeights) {
		float H = -Integer.MAX_VALUE;
		float L = Integer.MAX_VALUE;
		for (int i = 0; i < vertexHeights.length; i++) {
			if (vertexHeights[i] > H) {
				H = vertexHeights[i];
			}
		}
		for (int i = 0; i < vertexHeights.length; i++) {
			if (vertexHeights[i] < L) {
				L = vertexHeights[i];
			}
		}
		return ((H + L) / 2);
	}

}

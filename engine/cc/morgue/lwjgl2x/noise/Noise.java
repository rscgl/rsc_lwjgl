package cc.morgue.lwjgl2x.noise;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

public class Noise {

	static int width, height;
	static float[][] noiseMap;

	public static float[][] generatreNoise(int width, int height, int seed, float scale, int octaves, float persitance, float lacunarity, Vector2f offset) {
		Noise.width = width;
		Noise.height = height;
		noiseMap = new float[width][height];

		Random rand = new Random(seed);
		Vector2f[] octaveOffsets = new Vector2f[octaves];
		for (int i = 0; i < octaves; i++) {
			float offsetX = rand.nextInt(200000) - 100000 + offset.x;
			float offsetY = rand.nextInt(200000) - 100000 + offset.y;
			octaveOffsets[i] = new Vector2f(offsetX, offsetY);
		}

		if (scale <= 0) {
			scale = 0.00001f;
		}

		float maxNoiseHeight = Float.MIN_VALUE;
		float minNoiseHeight = Float.MAX_VALUE;

		float halfWidth = width / 2f;
		float halfHeight = height / 2f;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				float amplitude = 1f;
				float frequency = 1f;
				float noiseHeight = 0;

				for (int i = 0; i < octaves; i++) {
					float sampleX = (x - halfWidth) / scale * frequency + octaveOffsets[i].x;
					float sampleY = (y - halfHeight) / scale * frequency + octaveOffsets[i].y;

					float perlinValue = noise(sampleX, sampleY); // Mathf.PerlinNoise(sampleX, sampleY);
					noiseHeight += perlinValue * amplitude;

					amplitude *= persitance;
					frequency *= lacunarity;
				}

				if (noiseHeight > maxNoiseHeight) {
					maxNoiseHeight = noiseHeight;
				} else if (noiseHeight < minNoiseHeight) {
					minNoiseHeight = noiseHeight;
				}

				noiseMap[x][y] = noiseHeight;
			}
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				noiseMap[x][y] = inverseLerp(minNoiseHeight, maxNoiseHeight, noiseMap[x][y]); // Mathf.InverseLerp(minNoiseHeight, maxNoiseHeight, noiseMap[x][y]);
			}
		}

		return noiseMap;
	}

	// This is Lerp, need to inverse
	public static float inverseLerp(float start, float end, float percent) {
		return start + percent * (end - start);
	}

	public static float noise(float x, float y) {
		// Grid cell coordinates in integer values.
		int gx0 = (int) (Math.floor(x)); // Top-Left
		int gy0 = (int) (Math.floor(y)); // Top-Left
		int gx1 = gx0 + 1; // Down-Right
		int gy1 = gy0 + 1; // Down-Right

		// Random directions.
		Vector2f g00 = g(gx0, gy0); // Top-Left
		Vector2f g10 = g(gx1, gy0); // Top-Right
		Vector2f g11 = g(gx1, gy1); // Down-Right
		Vector2f g01 = g(gx0, gy1); // Down-Left

		// Subtract grid cells values from the point specified.
		Vector2f delta00 = new Vector2f(x - gx0, y - gy0); // Top-Left
		Vector2f delta10 = new Vector2f(x - gx1, y - gy0); // Top-Right
		Vector2f delta11 = new Vector2f(x - gx1, y - gy1); // Down-Right
		Vector2f delta01 = new Vector2f(x - gx0, y - gy1); // Down-Left

		// Compute a dot product between random directions and corresponding delta values.
		float s = dot(g00, new Vector2f(delta00.x, delta00.y)); // Top-Left
		float t = dot(g10, new Vector2f(delta10.x, delta10.y)); // Top-Right
		float u = dot(g11, new Vector2f(delta11.x, delta11.y)); // Down-Right
		float v = dot(g01, new Vector2f(delta01.x, delta01.y)); // Down-Left

		// Compute the weights for x and y axis.
		float sx = weigh(delta00.x);
		float sy = weigh(delta00.y);

		// Interpolate between values.
		float a = lerp(sy, s, v); // Interpolate Top-Left(s) and Down-Left(v). We can also call this LEFT
		float b = lerp(sy, t, u); // Interpolate Top-Right(t) and Down-Right(u) We can also call this RIGHT
		float h = lerp(sx, a, b); // Interpolate LEFT(a) and RIGHT(b). We can call this height(h)

		System.out.println(h);
		return h;
	}

	/**
	 * Computes a weight using S-curve function f(x) = 3 * (x * x) - 2 * (x * x * x).
	 * @param x NOT as in axis, but as a variable.
	 */
	public static float weigh(float x) {
		return 3 * (x * x) - 2 * (x * x * x);
	}

	public static float lerp(float weight, float a, float b) {
		return a + weight * (b - a);
	}

	/**
	 * Compute a dot product.
	 * Example: dot product between (a, b) and (c, d) is:
	 * a * c + b * d
	 */
	public static float dot(Vector2f v0, Vector2f v1) {
		return (v0.x * v1.x) + (v0.y * v1.y);
	}

	/**
	 * Get the random direction.
	 */
	private static Vector2f g(int x, int y) {
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (x >= width)
			x = width;
		if (y >= height)
			y = height;
		return new Vector2f(x, y); //XXX what
	}

}

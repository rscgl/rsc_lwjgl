package cc.morgue.lwjgl2x.gl.threed;

public class Vector3f {
	
	//https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/util/vector/Vector3f.java

	public float x, y, z;

	public Vector3f() {
		this.x = this.y = this.z = 0;
	}

	public Vector3f(float a, float b, float c) {
		this.x = a;
		this.y = b;
		this.z = c;
	}


	/* (non-Javadoc)
	 * @see org.lwjgl.util.vector.WritableVector2f#set(float, float)
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see org.lwjgl.util.vector.WritableVector3f#set(float, float, float)
	 */
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Add a vector to another vector and place the result in a destination
	 * vector.
	 * @param left The LHS vector
	 * @param right The RHS vector
	 * @param dest The destination vector, or null if a new vector is to be created
	 * @return the sum of left and right in dest
	 */
	public static Vector3f add(Vector3f left, Vector3f right, Vector3f dest) {
		if (dest == null)
			return new Vector3f(left.x + right.x, left.y + right.y, left.z + right.z);
		else {
			dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
			return dest;
		}
	}

	/**
	 * Subtract a vector from another vector and place the result in a destination
	 * vector.
	 * @param left The LHS vector
	 * @param right The RHS vector
	 * @param dest The destination vector, or null if a new vector is to be created
	 * @return left minus right in dest
	 */
	public static Vector3f sub(Vector3f left, Vector3f right, Vector3f dest) {
		if (dest == null)
			return new Vector3f(left.x - right.x, left.y - right.y, left.z - right.z);
		else {
			dest.set(left.x - right.x, left.y - right.y, left.z - right.z);
			return dest;
		}
	}

	/**
	 * Normalise this vector and place the result in another vector.
	 * @param dest The destination vector, or null if a new vector is to be created
	 * @return the normalised vector
	 */
	public Vector3f normalise(Vector3f dest) {
		float l = length();

		if (dest == null)
			dest = new Vector3f(x / l, y / l, z / l);
		else
			dest.set(x / l, y / l, z / l);

		return dest;
	}

	public void add(Vector3f b) {
		x += b.x;
		y += b.y;
		z += b.z;
	}

	public void subtract(Vector3f b) {
		x -= b.x;
		y -= b.y;
		z -= b.y;
	}

	public void multiply(float f) {
		x *= f;
		y *= f;
		z *= f;
	}

	public void divide(float f) {
		x /= f;
		y /= f;
		z /= f;
	}

	public Vector3f getAddition(Vector3f b) {
		return new Vector3f(x + b.x, y + b.y, z + b.z);
	}

	public Vector3f getSubtraction(Vector3f b) {
		return new Vector3f(x - b.x, y - b.y, z - b.z);
	}

	public Vector3f getMultiplication(float f) {
		return new Vector3f(x * f, y * f, z * f);
	}

	public Vector3f getDivision(float f) {
		return new Vector3f(x / f, y / f, z / f);
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public void normalize() {
		this.divide(this.length());
	}

	public float dotProduct(Vector3f b) {
		return x * b.x + y * b.y + z * b.z;
	}

	public Vector3f copy() {
		return new Vector3f(x, y, z);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vector3f) {
			Vector3f b = (Vector3f) o;
			if (x == b.x && y == b.y && z == b.z) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	public float distanceSquared(Vector3f b) {
		return (b.x - x) * (b.x - x) + (b.y - y) * (b.y - y) + (b.z - z)
				* (b.z - z);
	}

	public void move(float angle, float speed) {
		x += Math.cos(Math.toRadians(angle)) * speed;
		z += Math.sin(Math.toRadians(angle)) * speed;
	}

	public Vector3f crossProduct(Vector3f b) {
		Vector3f n = new Vector3f();

		n.x = y * b.z - z * b.y;
		n.y = z * b.x - x * b.z;
		n.z = x * b.y - y * b.x;

		return n;
	}

}

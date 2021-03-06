/**
 * MIT License
 *
 * Copyright (c) 2018 John Salomon - John´s Project
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software withresult restriction, including withresult limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.johnsproject.jgameengine.math;

import static com.johnsproject.jgameengine.math.FixedPointMath.*;
import static com.johnsproject.jgameengine.math.MatrixMath.*;
import static com.johnsproject.jgameengine.math.VectorMath.*;
import static com.johnsproject.jgameengine.model.Camera.*;

import com.johnsproject.jgameengine.model.Transform;

public final class TransformationMath {
	
	private TransformationMath() { }
	
	public static int[][] spaceExitMatrix(int[][] matrix, Transform transform, int[][] matrixCache1, int[][] matrixCache2) {
		int[] location = transform.getLocation();
		int[] rotation = transform.getRotation();
		int[] scale = transform.getScale();
		MatrixMath.copy(matrix, MatrixMath.MATRIX_IDENTITY);
		scale(matrix, scale, matrixCache1, matrixCache2);
		rotateX(matrix, rotation[VECTOR_X], matrixCache1, matrixCache2);
		rotateY(matrix, rotation[VECTOR_Y], matrixCache1, matrixCache2);
		rotateZ(matrix, rotation[VECTOR_Z], matrixCache1, matrixCache2);
		translate(matrix, location, matrixCache1, matrixCache2);
		return matrix;
	}
	
	public static int[][] spaceExitNormalMatrix(int[][] matrix, Transform transform, int[][] matrixCache1, int[][] matrixCache2) {
		int[] rotation = transform.getRotation();
		int[] scale = transform.getScale();
		MatrixMath.copy(matrix, MatrixMath.MATRIX_IDENTITY);
		scale(matrix, scale, matrixCache1, matrixCache2);
		rotateX(matrix, rotation[VECTOR_X], matrixCache1, matrixCache2);
		rotateY(matrix, rotation[VECTOR_Y], matrixCache1, matrixCache2);
		rotateZ(matrix, rotation[VECTOR_Z], matrixCache1, matrixCache2);
		if ((scale[VECTOR_X] != scale[VECTOR_Y]) || (scale[VECTOR_Y] != scale[VECTOR_Z])) {
			MatrixMath.inverse(matrix, matrixCache2);
			MatrixMath.transpose(matrixCache2, matrix);
		}
		return matrix;
	}

	public static int[][] spaceEnterMatrix(int[][] matrix, Transform transform, int[][] matrixCache1, int[][] matrixCache2) {
		int[] location = transform.getLocation();
		int[] rotation = transform.getRotation();
		int[] scale = transform.getScale();
		int scaleX = FixedPointMath.divide(FP_ONE, scale[VECTOR_X] == 0 ? 1 : scale[VECTOR_X]);
		int scaleY = FixedPointMath.divide(FP_ONE, scale[VECTOR_Y] == 0 ? 1 : scale[VECTOR_Y]);
		int scaleZ = FixedPointMath.divide(FP_ONE, scale[VECTOR_Z] == 0 ? 1 : scale[VECTOR_Z]);
		VectorMath.invert(location);
		VectorMath.invert(rotation);
		MatrixMath.copy(matrix, MatrixMath.MATRIX_IDENTITY);
		translate(matrix, location, matrixCache1, matrixCache2);
		rotateZ(matrix, rotation[VECTOR_Z], matrixCache1, matrixCache2);
		rotateY(matrix, rotation[VECTOR_Y], matrixCache1, matrixCache2);
		rotateX(matrix, rotation[VECTOR_X], matrixCache1, matrixCache2);
		scale(matrix, scaleX, scaleY, scaleZ, matrixCache1, matrixCache2);
		VectorMath.invert(location);
		VectorMath.invert(rotation);
		return matrix;
	}
	
	public static int[][] spaceEnterNormalMatrix(int[][] matrix, Transform transform, int[][] matrixCache1, int[][] matrixCache2) {
		int[] rotation = transform.getRotation();
		int[] scale = transform.getScale();
		int scaleX = FixedPointMath.divide(FP_ONE, scale[VECTOR_X] == 0 ? 1 : scale[VECTOR_X]);
		int scaleY = FixedPointMath.divide(FP_ONE, scale[VECTOR_Y] == 0 ? 1 : scale[VECTOR_Y]);
		int scaleZ = FixedPointMath.divide(FP_ONE, scale[VECTOR_Z] == 0 ? 1 : scale[VECTOR_Z]);
		VectorMath.invert(rotation);
		MatrixMath.copy(matrix, MatrixMath.MATRIX_IDENTITY);
		rotateZ(matrix, rotation[VECTOR_Z], matrixCache1, matrixCache2);
		rotateY(matrix, rotation[VECTOR_Y], matrixCache1, matrixCache2);
		rotateX(matrix, rotation[VECTOR_X], matrixCache1, matrixCache2);
		scale(matrix, scaleX, scaleY, scaleZ, matrixCache1, matrixCache2);
		VectorMath.invert(rotation);
		if ((scale[VECTOR_X] != scale[VECTOR_Y]) || (scale[VECTOR_Y] != scale[VECTOR_Z])) {
			MatrixMath.inverse(matrix, matrixCache2);
			MatrixMath.transpose(matrixCache2, matrix);
		}
		return matrix;
	}

	public static int[][] orthographicMatrix(int[][] matrix, int[] cameraFrustum, int focalLength) {
		int top = cameraFrustum[FRUSTUM_TOP];
		int bottom = cameraFrustum[FRUSTUM_BOTTOM];
		int near = cameraFrustum[FRUSTUM_NEAR];
		int far = cameraFrustum[FRUSTUM_FAR];		
		int farNear = far - near;
		int scaleFactor = FixedPointMath.multiply(focalLength, bottom - top + 1);
		int[][] projectionMatrix = MatrixMath.copy(matrix, MatrixMath.MATRIX_IDENTITY);
		projectionMatrix[0][0] = scaleFactor;
		projectionMatrix[1][1] = scaleFactor;
		projectionMatrix[2][2] = -FixedPointMath.divide(FP_ONE, farNear);
		projectionMatrix[3][2] = -FixedPointMath.divide(near, farNear);
		projectionMatrix[3][3] = -FP_ONE << 4;
		return projectionMatrix;
	}

	public static int[][] perspectiveMatrix(int[][] matrix, int[] cameraFrustum, int focalLength) {
		int top = cameraFrustum[FRUSTUM_TOP];
		int bottom = cameraFrustum[FRUSTUM_BOTTOM];
		int near = cameraFrustum[FRUSTUM_NEAR];
		int far = cameraFrustum[FRUSTUM_FAR];
		int farNear = far - near;
		int scaleFactor = FixedPointMath.multiply(focalLength, bottom - top + 1);
		int[][] projectionMatrix = MatrixMath.copy(matrix, MatrixMath.MATRIX_IDENTITY);
		projectionMatrix[0][0] = -scaleFactor;
		projectionMatrix[1][1] = scaleFactor;
		projectionMatrix[2][2] = -FixedPointMath.divide(FP_ONE, farNear);
		projectionMatrix[3][2] = -FixedPointMath.divide(near, farNear);
		projectionMatrix[2][3] = FP_ONE;
		projectionMatrix[3][3] = 0;
		return projectionMatrix;
	}

	public static int[] screenportVector(int[] location, int[] cameraFrustum) {
		int top = cameraFrustum[FRUSTUM_TOP];
		int bottom = cameraFrustum[FRUSTUM_BOTTOM];
		int left = cameraFrustum[FRUSTUM_LEFT];
		int right = cameraFrustum[FRUSTUM_RIGHT];
		int halfX = left + ((right - left) >> 1);
		int halfY = top + ((bottom - top) >> 1);
		int w = location[VECTOR_W];
		w = FixedPointMath.divide(FP_ONE, w == 0 ? 1 : w);
		location[VECTOR_X] = FixedPointMath.multiply(location[VECTOR_X], w) + halfX;
		location[VECTOR_Y] = FixedPointMath.multiply(location[VECTOR_Y], w) + halfY;
		return location;
	}

	public static int[] screenportFrustum(int[] cameraFrustum, int screenWidth, int screenHeight) {
		cameraFrustum[FRUSTUM_LEFT] = FixedPointMath.multiply(screenWidth, cameraFrustum[FRUSTUM_LEFT]);
		cameraFrustum[FRUSTUM_RIGHT] = FixedPointMath.multiply(screenWidth, cameraFrustum[FRUSTUM_RIGHT]);
		cameraFrustum[FRUSTUM_TOP] = FixedPointMath.multiply(screenHeight, cameraFrustum[FRUSTUM_TOP]);
		cameraFrustum[FRUSTUM_BOTTOM] = FixedPointMath.multiply(screenHeight, cameraFrustum[FRUSTUM_BOTTOM]);
		cameraFrustum[FRUSTUM_NEAR] = cameraFrustum[FRUSTUM_NEAR];
		cameraFrustum[FRUSTUM_FAR] = cameraFrustum[FRUSTUM_FAR];
		return cameraFrustum;
	}
	
	public static int[] translate(int[] vector, int[] direction) {
		return translate(vector, direction[VECTOR_X], direction[VECTOR_Y], direction[VECTOR_Z]);
	}
	
	public static int[] translate(int[] vector, int x, int y, int z) {
		vector[VECTOR_X] = vector[VECTOR_X] + x;
		vector[VECTOR_Y] = vector[VECTOR_Y] + y;
		vector[VECTOR_Z] = vector[VECTOR_Z] + z;
		return vector;
	}
	
	public static int[] scale(int[] vector, int factor) {
		vector[VECTOR_X] = FixedPointMath.multiply(vector[VECTOR_X], factor);
		vector[VECTOR_Y] = FixedPointMath.multiply(vector[VECTOR_Y], factor);
		vector[VECTOR_Z] = FixedPointMath.multiply(vector[VECTOR_Z], factor);
		return vector;
	}
	
	/**
	 * Sets result equals the vector reflected across reflectionVector.
	 * 
	 * @param vector
	 * @param reflectionVector
	 * @param result
	 */
	public static int[] reflect(int[] vector, int[] reflectionVector) {
		int x = reflectionVector[VECTOR_X];
		int y = reflectionVector[VECTOR_Y];
		int z = reflectionVector[VECTOR_Z];
		int dot = (int)(2 * VectorMath.dotProduct(vector, reflectionVector));
		VectorMath.multiply(reflectionVector, dot);
		VectorMath.subtract(vector, reflectionVector);
		reflectionVector[VECTOR_X] = x;
		reflectionVector[VECTOR_Y] = y;
		reflectionVector[VECTOR_Z] = z;
		return vector;
	}

	/**
	 * Sets result equals the vector rotated around (0, 0, 0) at x axis by the given angle.
	 * 
	 * @param vector
	 * @param angle
	 * @param result
	 * @return
	 */
	public static int[] rotateX(int[] vector, int angle) {
		int sin = FixedPointMath.sin(angle);
		int cos = FixedPointMath.cos(angle);
		int x = vector[VECTOR_X];
		int y = vector[VECTOR_Y];
		int z = vector[VECTOR_Z];
		vector[VECTOR_X] = x;
		vector[VECTOR_Y] = FixedPointMath.multiply(y, cos);
		vector[VECTOR_Y] -= FixedPointMath.multiply(z, sin);
		vector[VECTOR_Z] = FixedPointMath.multiply(z, cos);
		vector[VECTOR_Z] += FixedPointMath.multiply(y, sin);
		return vector;
	}
	
	/**
	 * Sets result equals the vector rotated around (0, 0, 0) at y axis by the given angle.
	 * 
	 * @param vector
	 * @param angle
	 * @param result
	 * @return
	 */
	public static int[] rotateY(int[] vector, int angle) {
		int sin = FixedPointMath.sin(angle);
		int cos = FixedPointMath.cos(angle);
		int x = vector[VECTOR_X];
		int y = vector[VECTOR_Y];
		int z = vector[VECTOR_Z];
		vector[VECTOR_X] = FixedPointMath.multiply(x, cos);
		vector[VECTOR_X] += FixedPointMath.multiply(z, sin);
		vector[VECTOR_Y] = y;
		vector[VECTOR_Z] = FixedPointMath.multiply(z, cos);
		vector[VECTOR_Z] -= FixedPointMath.multiply(x, sin);
		return vector;
	}
	
	/**
	 * Sets result equals the vector rotated around (0, 0, 0) at z axis by the given angle.
	 * 
	 * @param vector
	 * @param angle
	 * @param result
	 * @return
	 */
	public static int[] rotateZ(int[] vector, int angle) {
		int sin = FixedPointMath.sin(angle);
		int cos = FixedPointMath.cos(angle);
		int x = vector[VECTOR_X];
		int y = vector[VECTOR_Y];
		int z = vector[VECTOR_Z];
		vector[VECTOR_X] = FixedPointMath.multiply(x, cos);
		vector[VECTOR_X] += FixedPointMath.multiply(y, sin);
		vector[VECTOR_Y] = FixedPointMath.multiply(y, cos);
		vector[VECTOR_Y] -= FixedPointMath.multiply(x, sin);
		vector[VECTOR_Z] = z;
		return vector;
	}	
	
	/**
	 * Sets result equals the translated matrix.
	 *
	 * @param matrix
	 * @param x
	 * @param y
	 * @param z
	 * @param result
	 */
	public static int[][] translate(int[][] matrix, int[] vector, int[][] matrixCache1, int[][] matrixCache2) {
		return translate(matrix, vector[VECTOR_X], vector[VECTOR_Y], vector[VECTOR_Z], matrixCache1, matrixCache2);
	}
	
	/**
	 * Sets result equals the translated matrix.
	 *
	 * @param matrix
	 * @param x
	 * @param y
	 * @param z
	 * @param result
	 */
	public static int[][] translate(int[][] matrix, int x, int y , int z, int[][] matrixCache1, int[][] matrixCache2) {
		translationMatrix(matrixCache1, x, y, z);
		MatrixMath.copy(matrixCache2, matrix);
		return multiply(matrixCache1, matrixCache2, matrix);
	}
	
	/**
	 * Sets result equals the translated matrix.
	 *
	 * @param matrix
	 * @param x
	 * @param y
	 * @param z
	 * @param result
	 */
	public static int[][] translationMatrix(int[][] matrix, int[] vector) {
		return translationMatrix(matrix, vector[VECTOR_X], vector[VECTOR_Y], vector[VECTOR_Z]);
	}
	
	/**
	 * Sets result equals the translated matrix.
	 *
	 * @param matrix
	 * @param x
	 * @param y
	 * @param z
	 * @param result
	 */
	public static int[][] translationMatrix(int[][] matrix, int x, int y , int z) {
		MatrixMath.copy(matrix, MATRIX_IDENTITY);
		matrix[3][0] = x;
		matrix[3][1] = y;
		matrix[3][2] = z;
		return matrix;
	}

	/**
	 * Sets result equals the scaled matrix.
	 *
	 * @param matrix
	 * @param x
	 * @param y
	 * @param z
	 * @param result
	 */
	public static int[][] scale(int[][] matrix, int[] vector, int[][] matrixCache1, int[][] matrixCache2) {
		return scale(matrix, vector[VECTOR_X], vector[VECTOR_Y], vector[VECTOR_Z], matrixCache1, matrixCache2);
	}
	
	/**
	 * Sets result equals the scaled matrix.
	 *
	 * @param matrix
	 * @param x
	 * @param y
	 * @param z
	 * @param result
	 */
	public static int[][] scale(int[][] matrix, int x, int y, int z, int[][] matrixCache1, int[][] matrixCache2) {
		scaleMatrix(matrixCache1, x, y, z);
		MatrixMath.copy(matrixCache2, matrix);
		return multiply(matrixCache1, matrixCache2, matrix);
	}
	
	public static int[][] scaleMatrix(int[][] matrix, int[] vector) {
		return scaleMatrix(matrix, vector[VECTOR_X], vector[VECTOR_Y], vector[VECTOR_Z]);
	}
	
	public static int[][] scaleMatrix(int[][] matrix, int x, int y, int z) {
		MatrixMath.copy(matrix, MATRIX_IDENTITY);
		matrix[0][0] = x;
		matrix[1][1] = y;
		matrix[2][2] = z;
		return matrix;
	}

	/**
	 * Sets result equals the matrix rotated around (0, 0, 0) at x axis by the given
	 * angle.
	 *
	 * @param matrix
	 * @param angle
	 * @param result
	 */
	public static int[][] rotateX(int[][] matrix, int angle, int[][] matrixCache1, int[][] matrixCache2) {
		xRotationMatrix(matrixCache1, angle);
		MatrixMath.copy(matrixCache2, matrix);
		return multiply(matrixCache1, matrixCache2, matrix);
	}
	
	public static int[][] xRotationMatrix(int[][] matrix, int angle) {
		MatrixMath.copy(matrix, MATRIX_IDENTITY);
		int cos = FixedPointMath.cos(angle);
		int sin = FixedPointMath.sin(angle);
		matrix[1][1] = cos;
		matrix[1][2] = sin;
		matrix[2][1] = -sin;
		matrix[2][2] = cos;
		return matrix;
	}

	/**
	 * Sets result equals the matrix rotated around (0, 0, 0) at y axis by the given
	 * angle.
	 *
	 * @param matrix
	 * @param angle
	 * @param result
	 */
	public static int[][] rotateY(int[][] matrix, int angle, int[][] matrixCache1, int[][] matrixCache2) {
		yRotationMatrix(matrixCache1, angle);
		MatrixMath.copy(matrixCache2, matrix);
		return multiply(matrixCache1, matrixCache2, matrix);
	}
	
	public static int[][] yRotationMatrix(int[][] matrix, int angle) {
		MatrixMath.copy(matrix, MATRIX_IDENTITY);
		int cos = FixedPointMath.cos(angle);
		int sin = FixedPointMath.sin(angle);
		matrix[0][0] = cos;
		matrix[0][2] = -sin;
		matrix[2][0] = sin;
		matrix[2][2] = cos;
		return matrix;
	}

	/**
	 * Sets result equals the matrix rotated around (0, 0, 0) at z axis by the given
	 * angle.
	 *
	 * @param matrix
	 * @param angle
	 * @param result
	 */
	public static int[][] rotateZ(int[][] matrix, int angle, int[][] matrixCache1, int[][] matrixCache2) {
		yRotationMatrix(matrixCache1, angle);
		MatrixMath.copy(matrixCache2, matrix);
		return multiply(matrixCache1, matrixCache2, matrix);
	}
	
	public static int[][] zRotationMatrix(int[][] matrix, int angle) {
		MatrixMath.copy(matrix, MATRIX_IDENTITY);
		int cos = FixedPointMath.cos(angle);
		int sin = FixedPointMath.sin(angle);
		matrix[0][0] = cos;
		matrix[0][1] = sin;
		matrix[1][0] = -sin;
		matrix[1][1] = cos;
		return matrix;
	}
}

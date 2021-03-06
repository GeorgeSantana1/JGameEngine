/**
 * MIT License
 *
 * Copyright (c) 2018 John Salomon - John´s Project
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
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
package com.johnsproject.jgameengine.io;

import static com.johnsproject.jgameengine.math.VectorMath.*;

import java.io.IOException;
import java.io.InputStream;

import com.johnsproject.jgameengine.math.ColorMath;
import com.johnsproject.jgameengine.math.FixedPointMath;
import com.johnsproject.jgameengine.math.VectorMath;
import com.johnsproject.jgameengine.model.Face;
import com.johnsproject.jgameengine.model.Material;
import com.johnsproject.jgameengine.model.Mesh;
import com.johnsproject.jgameengine.model.Model;
import com.johnsproject.jgameengine.model.Transform;
import com.johnsproject.jgameengine.model.Vertex;
import com.johnsproject.jgameengine.shader.GouraudSpecularShader;
import com.johnsproject.jgameengine.shader.SpecularProperties;

/**
 * The SOMImporter class imports .som (Scene Object Mesh) files exported 
 * by Blender SOMExporter included in the Exporters folder.
 * 
 * @author John Ferraz Salomon
 *
 */
public final class SOMImporter {
	
	private SOMImporter() {	}
	
	/**
	 * Loads the .som file at the given path and returns a {@link Model} 
	 * containing the data of the file.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Model load(String path) throws IOException {
		String content = FileIO.readFile(path);
		return loadFromRaw(content);
	}

	/**
	 * Loads the .som file content from the given {@link InputStream} and returns a {@link Model} 
	 * containing the data of the stream.
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static Model load(InputStream stream) throws IOException {
		String content = FileIO.readStream(stream);
		return loadFromRaw(content);
	}

	/**
	 * Loads the .som file content from the given string and returns a {@link Model} 
	 * containing the data of the string.
	 * 
	 * @param data string containing data of .som file.
	 * @return
	 * @throws IOException
	 */
	public static Model loadFromRaw(String data) throws IOException {
		String rawData = data.replace(" ", "").replace("\n", "");
		Material[] materials = parseMaterials(rawData);
		Vertex[] vertices = parseVertices(rawData, materials);
		Face[] faces = parseFaces(rawData, vertices, materials);
		int[] location = VectorMath.emptyVector();
		int[] rotation = VectorMath.emptyVector();
		int one = FixedPointMath.FP_ONE;
		int[] scale = VectorMath.toVector(one, one, one);
		Transform transform = new Transform(location, rotation, scale);
		Mesh mesh = new Mesh(vertices, faces, materials);
		Model result = new Model("Model", transform, mesh);
		System.gc();
		return result;
	}
	
	private static Vertex[] parseVertices(String rawData, Material[] materials) throws IOException {
		String vCountData = rawData.split("vCount<")[1].split(">vCount", 2)[0];
		Vertex[] vertices = new Vertex[getint(vCountData)];
		String[] vLocationData = rawData.split("vPosition<")[1].split(">vPosition", 2)[0].split(",");
		String[] vNormalData = rawData.split("vNormal<")[1].split(">vNormal", 2)[0].split(",");
		String[] vMaterialData = rawData.split("vMaterial<")[1].split(">vMaterial", 2)[0].split(",");
		for (int i = 0; i < vertices.length * 3; i += 3) {
			int[] location = VectorMath.emptyVector();
			location[VECTOR_X] = -FixedPointMath.toFixedPoint(getFloat(vLocationData[i + VECTOR_X]));
			location[VECTOR_Y] = -FixedPointMath.toFixedPoint(getFloat(vLocationData[i + VECTOR_Y]));
			location[VECTOR_Z] = -FixedPointMath.toFixedPoint(getFloat(vLocationData[i + VECTOR_Z]));
			int[] normal = VectorMath.emptyVector();
			normal[VECTOR_X] = FixedPointMath.toFixedPoint(getFloat(vNormalData[i + VECTOR_X]));
			normal[VECTOR_Y] = FixedPointMath.toFixedPoint(getFloat(vNormalData[i + VECTOR_Y]));
			normal[VECTOR_Z] = FixedPointMath.toFixedPoint(getFloat(vNormalData[i + VECTOR_Z]));
			int material = getint(vMaterialData[i / 3]);
			vertices[i / 3] = new Vertex(i / 3, location, normal, materials[material]);
		}
		return vertices;
	}
	
	private static Face[] parseFaces(String rawData, Vertex[] vertices, Material[] materials) throws IOException {
		String fCountData = rawData.split("fCount<")[1].split(">fCount", 2)[0];
		Face[] faces = new Face[getint(fCountData)];
		String[] fVertex1Data = rawData.split("fVertex1<")[1].split(">fVertex1", 2)[0].split(",");
		String[] fVertex2Data = rawData.split("fVertex2<")[1].split(">fVertex2", 2)[0].split(",");
		String[] fVertex3Data = rawData.split("fVertex3<")[1].split(">fVertex3", 2)[0].split(",");
		String[] fMaterialData = rawData.split("fMaterial<")[1].split(">fMaterial", 2)[0].split(",");
		String[] fNormalData = rawData.split("fNormal<")[1].split(">fNormal", 2)[0].split(",");
		String[] fUV1Data = rawData.split("fUV1<")[1].split(">fUV1", 2)[0].split(",");
		String[] fUV2Data = rawData.split("fUV2<")[1].split(">fUV2", 2)[0].split(",");
		String[] fUV3Data = rawData.split("fUV3<")[1].split(">fUV3", 2)[0].split(",");
		for (int i = 0; i < faces.length * 6; i += 6) {
			int vertex1 = getint(fVertex1Data[i / 6]);
			int vertex2 = getint(fVertex2Data[i / 6]);
			int vertex3 = getint(fVertex3Data[i / 6]);
			int material = getint(fMaterialData[i / 6]);
			int[] normal = VectorMath.emptyVector();
			normal[VECTOR_X] = FixedPointMath.toFixedPoint(getFloat(fNormalData[(i / 2) + VECTOR_X]));
			normal[VECTOR_Y] = FixedPointMath.toFixedPoint(getFloat(fNormalData[(i / 2) + VECTOR_Y]));
			normal[VECTOR_Z] = FixedPointMath.toFixedPoint(getFloat(fNormalData[(i / 2) + VECTOR_Z]));
			int[] uv1 = VectorMath.emptyVector();
			uv1[VECTOR_X] = FixedPointMath.toFixedPoint(getFloat(fUV1Data[(i / 3) + VECTOR_X]));
			uv1[VECTOR_Y] = FixedPointMath.toFixedPoint(getFloat(fUV1Data[(i / 3) + VECTOR_Y]));
			int[] uv2 = VectorMath.emptyVector();
			uv2[VECTOR_X] = FixedPointMath.toFixedPoint(getFloat(fUV2Data[(i / 3) + VECTOR_X]));
			uv2[VECTOR_Y] = FixedPointMath.toFixedPoint(getFloat(fUV2Data[(i / 3) + VECTOR_Y]));
			int[] uv3 = VectorMath.emptyVector();
			uv3[VECTOR_X] = FixedPointMath.toFixedPoint(getFloat(fUV3Data[(i / 3) + VECTOR_X]));
			uv3[VECTOR_Y] = FixedPointMath.toFixedPoint(getFloat(fUV3Data[(i / 3) + VECTOR_Y]));
			faces[i / 6] = new Face(i / 6, normal, vertices[vertex1], vertices[vertex2], vertices[vertex3], materials[material], uv1, uv2, uv3);
		}
		return faces;
	}
	
	private static Material[] parseMaterials(String rawData){
		String mCountData = rawData.split("mCount<")[1].split(">mCount", 2)[0];
		Material[] materials = new Material[getint(mCountData)];
		String[] mDiffuseColorData = rawData.split("mDiffuseColor<")[1].split(">mDiffuseColor", 2)[0].split(",");
		String[] mDiffuseIntensityData = rawData.split("mDiffuseIntensity<")[1].split(">mDiffuseIntensity", 2)[0].split(",");
		String[] mSpecularIntensityData = rawData.split("mSpecularIntensity<")[1].split(">mSpecularIntensity", 2)[0].split(",");
		for (int i = 0; i < materials.length * 4; i+=4) {
			// * 256 to get int rgb values
			int r = FixedPointMath.toFixedPoint(getFloat(mDiffuseColorData[i]) * 256);
			int	g = FixedPointMath.toFixedPoint(getFloat(mDiffuseColorData[i+1]) * 256);
			int	b = FixedPointMath.toFixedPoint(getFloat(mDiffuseColorData[i+2]) * 256);
			int	a = FixedPointMath.toFixedPoint(getFloat(mDiffuseColorData[i+3]) * 256);
			int diffuseIntensity = FixedPointMath.toFixedPoint(getFloat(mDiffuseIntensityData[i / 4]));
			int specularIntensity = FixedPointMath.toFixedPoint(getFloat(mSpecularIntensityData[i / 4]));
			GouraudSpecularShader shader = new GouraudSpecularShader();
			SpecularProperties properties = (SpecularProperties) shader.getProperties();
			properties.setDiffuseColor(ColorMath.toColor(a, r, g, b));
			properties.setDiffuseIntensity(diffuseIntensity);
			properties.setSpecularIntensity(specularIntensity);
			materials[i/4] = new Material(i/4, "", shader);
		}
		return materials;
	}
	
	private static int getint(String string) {
		return Integer.parseInt(string);
	}
	
	private static float getFloat(String string) {
		return Float.parseFloat(string);
	}
}

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
package com.johnsproject.jgameengine.model;

import java.util.ArrayList;

public class Scene {
	
	private Camera mainCamera;
	private Light mainLight;
	private final ArrayList<SceneObject> sceneObjects;
	private final ArrayList<Model> models;
	private final ArrayList<Camera> cameras;
	private final ArrayList<Light> lights;
	
	public Scene() {
		this.sceneObjects = new ArrayList<SceneObject>();
		this.models = new ArrayList<Model>();
		this.cameras = new ArrayList<Camera>();
		this.lights = new ArrayList<Light>();
	}
	
	public ArrayList<SceneObject> getSceneObjects() {
		return sceneObjects;
	}
	
	public SceneObject getSceneObject(String name) {
		for (int i = 0; i < sceneObjects.size(); i++) {
			SceneObject sceneObject = sceneObjects.get(i);
			if(sceneObject.getName().equals(name)) {
				return sceneObject;
			}
		}
		return null;
	}
	
	private void removeSceneObject(String name) {
		for (int i = 0; i < sceneObjects.size(); i++) {
			if(sceneObjects.get(i).getName().equals(name)) {
				sceneObjects.remove(i);
			}
		}
	}

	public void addModel(Model model){
		sceneObjects.add(model);
		models.add(model);
	}
	
	public void removeModel(String name){
		removeSceneObject(name);
		for (int i = 0; i < models.size(); i++) {
			if(models.get(i).getName().equals(name)) {
				models.remove(i);
			}
		}
	}
	
	public ArrayList<Model> getModels() {
		return models;
	}
	
	public Model getModel(String name) {
		return (Model)getSceneObject(name);
	}
	
	public void addLight(Light light){
		if(mainLight == null) {
			setMainDirectionalLight(light);
		}
		sceneObjects.add(light);
		lights.add(light);
	}
	
	public void removeLight(String name){
		removeSceneObject(name);
		for (int i = 0; i < lights.size(); i++) {
			if(lights.get(i).getName().equals(name)) {
				lights.remove(i);
			}
		}
	}
	
	public ArrayList<Light> getLights() {
		return lights;
	}
	
	public Light getLight(String name) {
		return (Light)getSceneObject(name);
	}
	
	public Light getMainDirectionalLight() {
		return mainLight;
	}

	public void setMainDirectionalLight(Light mainLight) {
		if(this.mainLight != null) {
			this.mainLight.setTag(Light.LIGHT_TAG);
		}
		mainLight.setTag(Light.MAIN_DIRECTIONAL_LIGHT_TAG);
		this.mainLight = mainLight;
	}
	
	public void addCamera(Camera camera){
		if(mainCamera == null) {
			setMainCamera(camera);
		}
		sceneObjects.add(camera);
		cameras.add(camera);
	}
	
	public void removeCamera(String name){
		removeSceneObject(name);
		for (int i = 0; i < cameras.size(); i++) {
			if(cameras.get(i).getName().equals(name)) {
				cameras.remove(i);
			}
		}
	}

	public ArrayList<Camera> getCameras() {
		return cameras;
	}
	
	public Camera getCamera(String name) {
		return (Camera)getSceneObject(name);
	}

	public Camera getMainCamera() {
		return mainCamera;
	}

	public void setMainCamera(Camera mainCamera) {
		if(this.mainCamera != null) {
			this.mainCamera.setTag(Camera.CAMERA_TAG);
		}
		mainCamera.setTag(Camera.MAIN_CAMERA_TAG);
		this.mainCamera = mainCamera;
	}
}

package dev.array21.doggobot.gson;

import java.util.List;

public class Metadata {
	private final int version = 1;
	private List<ImageObject> images;
	
	public Metadata(List<ImageObject> images) {
		this.images = images;
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public List<ImageObject> getImages() {
		return this.images;
	}
	
	public void addImage(ImageObject image) {
		this.images.add(image);
	}
}

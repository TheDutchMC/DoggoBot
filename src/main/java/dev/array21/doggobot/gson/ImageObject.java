package dev.array21.doggobot.gson;

import java.util.UUID;

public class ImageObject {
	private UUID id;
	private boolean hasPlayed;
	private String path;
	
	public ImageObject(String path) {
		this.id = UUID.randomUUID();
		this.hasPlayed = false;
		this.path = path;
	}
	
	public UUID getId() {
		return this.id;
	}
	
	public boolean getHasPlayed() {
		return this.hasPlayed;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setHasPlayed(boolean hasPlayed) {
		this.hasPlayed = hasPlayed;
	}
}

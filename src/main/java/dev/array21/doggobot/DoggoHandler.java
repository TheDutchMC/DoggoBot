package dev.array21.doggobot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import dev.array21.doggobot.gson.ImageObject;
import dev.array21.doggobot.gson.Metadata;

public class DoggoHandler {

	private List<ImageObject> used = new ArrayList<>();
	private List<ImageObject> unused = new ArrayList<>();
	private List<String> existingPaths = new ArrayList<>();
	private File metadataFile;
	
	private final Random random = new Random();
	private final Gson gson = new Gson();
	
	private static final Logger LOGGER = LogManager.getLogger(DoggoHandler.class);
	
	
	public ImageObject getDoggo() {
		if(this.unused.isEmpty()) {
			this.unused.addAll(this.used);
			this.used.clear();
		}
		
		int index = this.random.nextInt(this.unused.size());
		ImageObject img = this.unused.remove(index);
		img.setHasPlayed(true);
		this.used.add(img);
		
		updateMetadata();
		
		return img;
	}
	
	private void updateMetadata() {
		new Runnable() {
			@Override
			public void run() {
				DoggoHandler.LOGGER.debug("Updating metadata");

				List<ImageObject> totals = new ArrayList<>();
				totals.addAll(DoggoHandler.this.unused);
				totals.addAll(DoggoHandler.this.used);
				
				Metadata meta = new Metadata(totals);
				
				if(DoggoHandler.this.metadataFile == null) {
					DoggoHandler.LOGGER.error("'metadataFile' field is null. This means that DoggoHandler has not yet been initialized");
					return;
				}
				
				try {
					JsonWriter jw = new JsonWriter(new FileWriter(DoggoHandler.this.metadataFile));
					DoggoHandler.this.gson.toJson(meta, Metadata.class, jw);
					jw.flush();
					jw.close();
				} catch (IOException e) {
					DoggoHandler.LOGGER.error("Failed to write to metadata file", e);
					return;
				}
			}
		}.run();
	}
	
	public void findPictures(String rootPath) throws IOException {
		File f = new File(rootPath);

		if(!f.exists()) {
			throw new IllegalArgumentException(String.format("Path '%s' does not exist", rootPath));
		}
		
		if(!f.isDirectory()) {
			throw new IllegalArgumentException(String.format("Path '%s' is not a directory", rootPath));
		}
		
		File metadataFile = new File(f, ".metada.json");
		this.metadataFile = metadataFile;
		if(!metadataFile.exists()) {
			LOGGER.debug(".metadata.json does not exist. Creating");
			metadataFile.createNewFile();
		}

		LOGGER.debug("Parsing .metada.json");
		JsonReader jr = new JsonReader(new FileReader(metadataFile));
		Metadata meta = gson.fromJson(jr, Metadata.class);
		jr.close();
		
		if(meta == null) {
			meta = new Metadata(new ArrayList<>());
		}
		
		meta.getImages().forEach(img -> {
			File imgFile = new File(img.getPath());
			if(!imgFile.exists()) {
				return;
			}
			
			if(img.getHasPlayed()) {
				LOGGER.debug(String.format("Found played ImageObject: '%s'", imgFile.getAbsolutePath()));
				this.used.add(img);
			} else {
				LOGGER.debug(String.format("Found unplayed ImageObject: '%s'", imgFile.getAbsolutePath()));
				this.unused.add(img);
			}
			
			this.existingPaths.add(imgFile.getAbsolutePath());
		});
				
		LOGGER.debug(".metadata.json parsed");
		LOGGER.debug("Reading pictures folder for potentially undiscovered pictures");
		this.read(f);
		this.updateMetadata();
		
		LOGGER.info(String.format("DoggoHandler initialzied. Found %d unplayed pictures, and %d played pictures", this.unused.size(), this.used.size()));
	}
	
	private void read(File f) {
		File[] children = f.listFiles();
		for(File child : children) {
			LOGGER.debug(String.format("Discovering file: '%s'", child.getAbsolutePath()));

			if(child.isDirectory()) {
				this.read(child);
				continue;
			}
			
			if(child.getName().equals(".metadata.json")) {
				continue;
			}
			
			if(!existingPaths.contains(child.getAbsolutePath())) {
				LOGGER.debug(String.format("Found undiscovered ImageObject: '%s'", child.getAbsolutePath()));

				ImageObject img = new ImageObject(child.getAbsolutePath());
				this.unused.add(img);	
				this.existingPaths.add(child.getAbsolutePath());
			}
		}
	}
}

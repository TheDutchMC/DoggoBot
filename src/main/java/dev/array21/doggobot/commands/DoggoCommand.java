package dev.array21.doggobot.commands;

import java.awt.Color;
import java.io.File;
import java.util.Optional;

import dev.array21.doggobot.DoggoHandler;
import dev.array21.doggobot.gson.ImageObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class DoggoCommand extends ListenerAdapter {
	
	private DoggoHandler doggoHandler;
	
	public DoggoCommand(DoggoHandler doggoHandler) {
		this.doggoHandler = doggoHandler;
	}
	
	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		if(!event.getName().equals("doggo")) {
			return;
		}
		
		event.deferReply(false).queue();
		InteractionHook ih = event.getHook();
		
		ImageObject img = this.doggoHandler.getDoggo();
		File fImg = new File(img.getPath());
	    Optional<String> fType = Optional.ofNullable(fImg.getName())
	    	      .filter(f -> f.contains("."))
	    	      .map(f -> f.substring(fImg.getName().lastIndexOf(".") + 1));
		
		EmbedBuilder eb = new EmbedBuilder()
				.setImage(String.format("attachment://%s.%s", img.getId().toString(), fType.isPresent() ? fType.get() : "png"))
				.setTitle("Doggo!")
				.setColor(Color.BLUE);
		
		ih.sendMessageEmbeds(eb.build())
				.addFile(fImg, String.format("%s.%s", img.getId().toString(), fType.isPresent() ? fType.get() : "png"))
				.setEphemeral(false)
				.queue();
	}
}

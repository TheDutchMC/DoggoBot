package dev.array21.doggobot;

import org.apache.log4j.Logger;

import dev.array21.doggobot.commands.DoggoCommand;
import net.dv8tion.jda.api.JDA;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.apache.log4j.LogManager;

public class DoggoBot {

	private static final Logger LOGGER = LogManager.getLogger(DoggoBot.class);
	public static void main(String[] args) {
		LOGGER.info("Initializing DoggoBot");
		if(args.length < 2) {
			LOGGER.error("Missing arguments");
			System.exit(1);
		}
		
		String token = args[0];
		String path = args[1];
		
		LOGGER.info("Initializing JDA");
		JdaHandler jdaHandler = new JdaHandler();
		try {
			jdaHandler.init(token);
		} catch(LoginException e) {
			LOGGER.error("Failed to log in to Discord", e);
			System.exit(1);
		}
		
		LOGGER.info("Initializing DoggoHandler");
		DoggoHandler doggoHandler = new DoggoHandler();
		try {
			doggoHandler.findPictures(path);
		} catch(IOException e) {
			LOGGER.error("Failed to initialize DoggoHandler", e);
			System.exit(1);
		}
		
		JDA jda = jdaHandler.getJda();
		
		LOGGER.info("Registering event listeners");
		jda.addEventListener(new DoggoCommand(doggoHandler));
		
		LOGGER.info("Registering commands");
		jda.upsertCommand("doggo", "Get a doggo!").queue();
		
		LOGGER.info("Startup complete");
	}
	
}

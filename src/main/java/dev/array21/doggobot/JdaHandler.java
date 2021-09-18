package dev.array21.doggobot;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class JdaHandler {
	
	private JDA jda;
	
	public void init(String token) throws LoginException {
		this.jda = JDABuilder.createDefault(token)
				.setActivity(Activity.playing("Doggo pics for all"))
				.build();
		
		try {
			this.jda.awaitReady();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public JDA getJda() {
		return this.jda;
	}
}

package com.zx.pong;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "gdx-pong";
		cfg.width = 1920;
		cfg.height = 1090;
		
		new LwjglApplication(new Pong(), cfg);
	}
}

package com.zx.pong;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Pong implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private Texture paddleTexture;
	private Texture ballTexture;
	
	private Sprite leftPaddle;
	private Sprite rightPaddle;
	private Sprite ball;
	
	@Override
	public void create() {	
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(w, h);
		camera.setToOrtho(false, w, h);
		batch = new SpriteBatch();
		
		paddleTexture = new Texture(Gdx.files.internal("square.png"));
		//paddleTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		leftPaddle = new Sprite(paddleTexture);
		leftPaddle.setSize(32, 256);
		leftPaddle.setOrigin(leftPaddle.getWidth()/2.f, leftPaddle.getHeight()/2.f); //No effect on translation
		leftPaddle.setPosition(leftPaddle.getWidth() * 2, h/2.f - leftPaddle.getHeight()/2.f);		
		
	}
		
	@Override
	public void dispose() {
		batch.dispose();
		paddleTexture.dispose();
	}
		
	@Override
	public void render() {
		if (Gdx.input.getAccelerometerX() >= 9) {
			leftPaddle.setY(leftPaddle.getY() - 10);
		}
		else if (Gdx.input.getAccelerometerX() <= 5){
			leftPaddle.setY(leftPaddle.getY() + 10);
		}
		
		Gdx.gl.glClearColor(
				40.f / 255.f,
				40.f / 255.f,
				40.f / 255.f,
				255.f / 255.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		leftPaddle.draw(batch);
		batch.end();
	}
		
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}

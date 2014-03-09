package com.zx.pong;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public class Pong implements ApplicationListener {
	private float w;
	private float h;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private Texture paddleTexture;
	private Texture ballTexture;
	
	private Sprite leftPaddle;
	private Sprite rightPaddle;
	private Sprite ball;
	
	private boolean ballMovingLeft;
	private float ballYMovement;
	private float ballSpeed;
	
	private ShapeRenderer shapeRenderer;
	
	@Override
	public void create() {	
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(w, h);
		camera.setToOrtho(false, w, h);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		paddleTexture = new Texture(Gdx.files.internal("square.png"));
		//paddleTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		ballTexture = new Texture(Gdx.files.internal("square.png"));
		
		leftPaddle = new Sprite(paddleTexture);
		leftPaddle.setSize(32, 256);
		leftPaddle.setOrigin(leftPaddle.getWidth()/2.f, leftPaddle.getHeight()/2.f); //No effect on translation
		leftPaddle.setPosition(leftPaddle.getWidth()*2, h/2.f - leftPaddle.getHeight()/2.f);
		
		rightPaddle = new Sprite(paddleTexture);
		rightPaddle.setSize(32, 256);
		rightPaddle.setOrigin(rightPaddle.getWidth()/2.f, rightPaddle.getHeight()/2.f);
		rightPaddle.setPosition(w - rightPaddle.getWidth()*2, h/2.f - rightPaddle.getHeight()/2.f);
		
		ball = new Sprite(ballTexture);
		ball.setOrigin(ball.getWidth()/2.f, ball.getHeight()/2.f);
		ball.setPosition(w/2.f + ball.getWidth()/2.f, h/2.f + ball.getHeight()/2.f);
		
		ballMovingLeft = true;
		ballSpeed = 10;
		ballYMovement = 0;
	}
		
	@Override
	public void dispose() {
		batch.dispose();
		paddleTexture.dispose();
	}
		
	@Override
	public void render() {
		int leftPlayerPointer = -1;
		int rightPlayerPointer = -1;
		
		Rectangle leftPaddleRect = new Rectangle(leftPaddle.getX(), leftPaddle.getY(), leftPaddle.getWidth(), leftPaddle.getHeight());
		Rectangle rightPaddleRect = new Rectangle(rightPaddle.getX(), rightPaddle.getY(), rightPaddle.getWidth(), rightPaddle.getHeight());
		Rectangle ballRect = new Rectangle(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
		
		//Collision Check
		//Issue: Ball can enter paddle
		if (ballRect.overlaps(leftPaddleRect) || ballRect.overlaps(rightPaddleRect)) {
			ballMovingLeft = !ballMovingLeft;
			ballYMovement = (float) ((Math.random()-0.5f) * 20.f);
			ballSpeed += 1;
		}
		
		//Move ball along x-axis
		if (ballMovingLeft) {
			ball.setX(ball.getX() - ballSpeed);
		} else {
			ball.setX(ball.getX() + ballSpeed);
		}
		
		//Reset ball if off the left/right of the screen
		if (ball.getX() + ball.getWidth() <= 0 || ball.getX() >= w) {
			ball.setX(w/2.f + ball.getWidth()/2.f);
			ball.setY(h/2.f + ball.getHeight()/2.f);
			ballMovingLeft = !ballMovingLeft;
			ballYMovement = (float) ((Math.random()-0.5f) * 20.f);
		}
		
		//Inverse Y-Movement if going off the top/bottom of the screen
		if (ball.getY() <= 0 || ball.getY() + ball.getHeight() >= h) {
			ballYMovement = -ballYMovement;
			ballSpeed = 10;
		}
		
		//Move ball along y-axis
		ball.setY(ball.getY() + ballYMovement);
		
		//Set player pointer depending on where the first finger is
		if (Gdx.input.isTouched(0)) {
			if (Gdx.input.getX(0) >= w/2.f) {
				rightPlayerPointer = 0;
			}
			else {
				leftPlayerPointer = 0;
			}
		}
		
		//Set player pointer depending on where second finger is
		if (Gdx.input.isTouched(1)) {
			if (Gdx.input.getX(1) >= w/2.f && rightPlayerPointer == -1) {
				rightPlayerPointer = 1;
			}
			else if (Gdx.input.getX(1) < w/2.f && leftPlayerPointer == -1){
				leftPlayerPointer = 1;
			}
		}
		
		//Detect pointer on respective side and move paddle according to finger position relative to middle of paddle
		if (leftPlayerPointer >= 0) {
			if (h - Gdx.input.getY(leftPlayerPointer) >= leftPaddle.getY() + leftPaddle.getHeight()/2.f) {
				if (leftPaddle.getY() + leftPaddle.getHeight() <= h - 8) {
					leftPaddle.setY(leftPaddle.getY() + 7);
				}
			} else if (leftPaddle.getY() >= 8) {
				leftPaddle.setY(leftPaddle.getY() - 7);
			}
		}
		
		//Detect pointer on respective side and move paddle according to finger position relative to middle of paddle
		if (rightPlayerPointer >= 0) {
			if (h - Gdx.input.getY(rightPlayerPointer) >= rightPaddle.getY() + rightPaddle.getHeight()/2.f) {
				if (rightPaddle.getY() +rightPaddle.getHeight() <= h - 8) {
					rightPaddle.setY(rightPaddle.getY() + 7);
				}
			} else if (rightPaddle.getY() >= 8) {
				rightPaddle.setY(rightPaddle.getY() - 7);
			}
		}
		
		Gdx.gl.glClearColor(
				40.f / 255.f,
				40.f / 255.f,
				40.f / 255.f,
				255.f / 255.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Draw Line Separator
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(100.f / 255.f, 100.f / 255.f, 100.f / 255.f, 255.f / 255.f);
		shapeRenderer.line(w/2.f, 0, w/2.f, h);
		shapeRenderer.end();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		ball.draw(batch);
		leftPaddle.draw(batch);
		rightPaddle.draw(batch);
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

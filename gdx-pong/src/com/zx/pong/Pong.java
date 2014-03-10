package com.zx.pong;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Pong implements ApplicationListener {
	private float w;
	private float h;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private SpriteBatch batchLeftPlayerScore;
	private SpriteBatch batchRightPlayerScore;
	
	private Texture paddleTexture;
	private Texture ballTexture;
	
	private Sprite leftPaddle;
	private Sprite rightPaddle;
	private Sprite ball;
	
	private boolean ballMovingLeft;
	private float ballYMovement;
	private float ballSpeed;
	
	private int leftPlayerScore;
	private int rightPlayerScore;
	
	private ShapeRenderer shapeRenderer;
	
	BitmapFont arial30;
	
	@Override
	public void create() {	
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(w, h);
		camera.setToOrtho(false, w, h);
		
		batch = new SpriteBatch();

		Matrix4 rotMatLeft = new Matrix4();
		Matrix4 rotMatRight = new Matrix4();
		
		batchLeftPlayerScore = new SpriteBatch();
		//rotMat.setToRotation(new Vector3(w/4.f, h/2.f, 0), 180);
		//rotMatLeft.setToRotation(new Vector3(90, 90, 0), 180);
		batchLeftPlayerScore.setTransformMatrix(rotMatLeft);
		
		batchRightPlayerScore = new SpriteBatch();
		//rotMatRight.setToRotation(new Vector3(1700, 700, 0), 180);
		batchRightPlayerScore.setTransformMatrix(rotMatRight);
		
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
		
		leftPlayerScore = 0;
		rightPlayerScore = 0;
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/arial.ttf"));
		FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
		fontParam.size = 30;
		fontParam.flip = false;
		arial30 = generator.generateFont(fontParam);
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
		if (ball.getX() + ball.getWidth() <= 0) {
			ball.setX(w/2.f + ball.getWidth()/2.f);
			ball.setY(h/2.f + ball.getHeight()/2.f);
			ballMovingLeft = !ballMovingLeft;
			ballYMovement = (float) ((Math.random()-0.5f) * 20.f);
			
			rightPlayerScore += 1;
		} else if (ball.getX() >= w) {
			ball.setX(w/2.f + ball.getWidth()/2.f);
			ball.setY(h/2.f + ball.getHeight()/2.f);
			ballMovingLeft = !ballMovingLeft;
			ballYMovement = (float) ((Math.random()-0.5f) * 20.f);
			
			leftPlayerScore += 1;
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
		
		batchLeftPlayerScore.begin();
		arial30.draw(batchLeftPlayerScore, String.valueOf(leftPlayerScore), w/4.f, h/2.f);
		batchLeftPlayerScore.end();
		
		batchRightPlayerScore.begin();
		arial30.draw(batchRightPlayerScore, String.valueOf(rightPlayerScore), w/2.f + w/4.f, h/2.f);
		batchRightPlayerScore.end();
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

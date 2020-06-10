package com.hasanonm.coingirl.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinGirl extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] girl;
    int girlState = 0;
    int pause = 0;
    float gravity = .2f;
    float velocity = 0;
    int girlY = 0; //on screen representation
    Rectangle girlRectangle;
    BitmapFont font;
    Texture sadGirl;
    int result = 0;
    int gameState = 0;

    Random random;

    ArrayList<Integer> coinXs = new ArrayList<Integer>();
    ArrayList<Integer> coinYs = new ArrayList<Integer>();
    ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
    Texture coin;
    int coinCount;

    ArrayList<Integer> bombXs = new ArrayList<Integer>();
    ArrayList<Integer> bombYs = new ArrayList<Integer>();
    ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
    Texture bomb;
    int bombCount;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        girl = new Texture[4];
        girl[0] = new Texture("frame-1.png");
        girl[1] = new Texture("frame-2.png");
        girl[2] = new Texture("frame-3.png");
        girl[3] = new Texture("frame-4.png");
        girlY = Gdx.graphics.getHeight() / 2;

        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        random = new Random();

        sadGirl = new Texture("dizzy-1.png");
        font = new BitmapFont();
        font.setColor(Color.PURPLE);
        font.getData().setScale(10);

    }

    public void makeCoin() {
        //randomly generating coins
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        coinYs.add((int) height);
        coinXs.add(Gdx.graphics.getWidth());


    }

    public void makeBomb() {
        //randomly generating coins
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        bombYs.add((int) height);
        bombXs.add(Gdx.graphics.getWidth());

    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {
            //game is live
            //for bomb
            if (bombCount < 250) {
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }

            bombRectangles.clear();
            for (int i = 0; i < bombXs.size(); i++) {
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombXs.set(i, bombXs.get(i) - 8);
                bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));

            }


            //for coins
            if (coinCount < 100) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }

            coinRectangles.clear();
            for (int i = 0; i < coinXs.size(); i++) {
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinXs.set(i, coinXs.get(i) - 4);
                coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
            }
            if (Gdx.input.justTouched()) {
                velocity = -10;
            }

            if (pause < 8) {
                pause++;
            } else {
                pause = 0;
                if (girlState < 3) {
                    girlState++;
                } else {
                    girlState = 0;
                }
            }

            velocity += gravity;
            girlY -= velocity;

            if (girlY <= 0) {
                girlY = 0;
            }
        } else if (gameState == 0) {
            //waiting to start

            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            //game is over
            if (Gdx.input.justTouched()) {
                gameState = 1;
                girlY = Gdx.graphics.getHeight() / 2;
                result = 0;
                velocity = 0;
                coinXs.clear();
                coinYs.clear();
                coinRectangles.clear();
                coinCount = 0;
                bombXs.clear();
                bombYs.clear();
                bombRectangles.clear();
                bombCount = 0;
            }

        }

        if (gameState == 2) {
            batch.draw(sadGirl, Gdx.graphics.getWidth() / 2 - girl[girlState].getWidth() / 2, girlY);
        } else {
            batch.draw(girl[girlState], Gdx.graphics.getWidth() / 2 - girl[girlState].getWidth() / 2, girlY);
        }
        girlRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - girl[girlState].getWidth() / 2, girlY, girl[girlState].getWidth(), girl[girlState].getHeight());

        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(girlRectangle, coinRectangles.get(i))) {
                result++;
                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }

        for (int i = 0; i < bombRectangles.size(); i++) {
            if (Intersector.overlaps(girlRectangle, bombRectangles.get(i))) {
                gameState = 2;
            }
        }

        font.draw(batch, String.valueOf(result), 100, 200);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();

    }
}

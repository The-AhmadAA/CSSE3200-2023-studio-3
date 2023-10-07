package com.csse3200.game.screens.HelpScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.csse3200.game.GdxGame;

public class MobsDescriptionHelpScreen extends ScreenAdapter {
    private final GdxGame game;
    private Stage stage;
    private SpriteBatch spriteBatch;


    public MobsDescriptionHelpScreen(GdxGame game) {
        this.game = game;
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        spriteBatch = new SpriteBatch();

        // Create a table to organize the image placeholder
        Table table = new Table();
        table.setFillParent(true); // Makes the table the size of the stage

        // Create one image placeholder
        Image image = new Image(new Texture("images/lose-screen/desktop-wallpaper-simple-stars-video-background-loop-black-and-white-aesthetic-space.jpg"));

        // Add the image placeholder to the table
        table.add(image).expand().fill();

        // Add the table to the stage
        stage.addActor(table);

        Skin skin = new Skin(Gdx.files.internal("images/ui/buttons/glass.json"));
        TextButton BackButton = new TextButton("Back", skin);
        BackButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(GdxGame.ScreenType.MAIN_MENU);
            }
        });

        TextButton TowersButton = new TextButton("Towers", skin);
        TowersButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(GdxGame.ScreenType.HELP_TOWER_SCREEN);
            }
        });

        TextButton BossButton = new TextButton("BossMobs", skin);
        BossButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(GdxGame.ScreenType.HELP_BOSS_SCREEN);
            }
        });

        Texture imageTexture = new Texture("images/ui/Sprites/UI_Glass_Arrow_Large_01a.png");
        Drawable drawable = new TextureRegionDrawable(new TextureRegion(imageTexture));
        ImageButton GameDescButton = new ImageButton(drawable);
        GameDescButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(GdxGame.ScreenType.HELP_SCREEN);
            }
        });

        Table buttonTable = new Table();
        buttonTable.add(BackButton).padRight(10);
        Table table1 = new Table();
        table1.setFillParent(true);
        table1.top().right(); // Align to the top-right corner
        table1.pad(20); // Add padding to the top-right corner
        table1.add(buttonTable).row(); // Add button table and move to the next row
        stage.addActor(table1);

        Table buttonTable2 = new Table();
        buttonTable2.add(TowersButton).padRight(10);
        Table table2 = new Table();
        table2.setFillParent(true);
        table2.center().right(); // Align to the top-right corner
        table2.pad(20); // Add padding to the top-right corner
        table2.add(buttonTable2).row(); // Add button table and move to the next row
        stage.addActor(table2);

        Table buttonTable3 = new Table();
        buttonTable3.add(GameDescButton).padRight(10);
        Table table3 = new Table();
        table3.setFillParent(true);
        table3.center().left(); // Align to the top-right corner
        table3.pad(20); // Add padding to the top-right corner
        table3.add(buttonTable3).row(); // Add button table and move to the next row
        stage.addActor(table3);

        Table buttonTable4 = new Table();
        buttonTable4.add(BossButton).padRight(10);
        Table table4 = new Table();
        table4.setFillParent(true);
        table4.center().bottom(); // Align to the top-right corner
        table4.pad(20); // Add padding to the top-right corner
        table4.add(buttonTable4).row(); // Add button table and move to the next row
        stage.addActor(table4);
    }

    @Override
    public void show() {
        // Set this screen as the input processor
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        spriteBatch.begin();
        spriteBatch.end();

        // Draw the stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        spriteBatch.dispose();
    }
}

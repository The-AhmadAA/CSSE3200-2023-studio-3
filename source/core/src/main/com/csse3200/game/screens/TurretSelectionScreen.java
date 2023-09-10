package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.TowerFactory;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TurretSelectionScreen extends ScreenAdapter {

    private static final int MAX_SELECTED_TURRETS = 5;
    private Stage stage;
    private List<TowerType> turretList;
    private TextButton confirmButton;

    private GdxGame game;

    private Label message;
    private Label turretsPicked;
    private Table table;
    private Set<TowerType> selectedTurrets = new HashSet<>();

    private static final Logger logger = LoggerFactory.getLogger(MainMenuScreen.class);

    public TurretSelectionScreen(GdxGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        table = new Table();


        turretList = new ArrayList<>();
        // Add turrets to the list
        turretList.add(TowerType.WEAPON);
        turretList.add(TowerType.TNT);
        turretList.add(TowerType.DROID);
        turretList.add(TowerType.WALL);
        turretList.add(TowerType.FIRE);
        turretList.add(TowerType.STUN);
        turretList.add(TowerType.INCOME);



        Skin skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
        message = new Label("Select your turrets", skin);
        turretsPicked = new Label("Turrets picked: ", skin);

        confirmButton = new TextButton("Continue", skin);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(GdxGame.ScreenType.MAIN_GAME);
            }
        });


        table.add(message).row();
        table.add(turretsPicked).row();
        for (TowerType turret : turretList) {
            TextButton turretButton = new TextButton(turret.getTowerName(), skin);
            turretButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    logger.info(String.valueOf(selectedTurrets.size()));
                    if (selectedTurrets.size() > MAX_SELECTED_TURRETS) {
                        message.setText("You can only select up to 5 turrets.");
                    } else {
                        message.setText("Select your turrets");
                    }
                    if (selectedTurrets.contains(turret)) {
                        // Turret is already selected, unselect it
                        selectedTurrets.remove(turret);
                        // You can also change the button appearance to indicate unselection
                        logger.info(selectedTurrets.toString());
                        turretsPicked.setText("Turrets picked: " + selectedTurrets.toString());
                    } else if (selectedTurrets.size() == MAX_SELECTED_TURRETS) {
                        // Turret is not selected, but the max number of turrets has been reached
                        message.setText("You can only select up to 5 turrets.");
                    } else if (selectedTurrets.size() < MAX_SELECTED_TURRETS) {
                        // Turret is not selected, select it
                        selectedTurrets.add(turret);
                        turretsPicked.setText("Turrets picked: " + selectedTurrets.toString());
                        logger.info(selectedTurrets.toString());
                    }
                    else {
                        // Turret is not selected, select it
                        selectedTurrets.add(turret);
                        turretsPicked.setText("Turrets picked: " + selectedTurrets.toString());
                        logger.info(selectedTurrets.toString());

                        // You can change the button appearance to indicate selection
                    }

                }
            });
            table.add(turretButton).row();
        }
        table.add(confirmButton).padBottom(20).row();

        stage.addActor(table);
        table.setFillParent(true);
        Gdx.input.setInputProcessor(stage);

    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}

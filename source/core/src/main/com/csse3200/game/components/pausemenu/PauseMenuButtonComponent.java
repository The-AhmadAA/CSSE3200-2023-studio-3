package com.csse3200.game.components.pausemenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.GdxGame;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PauseMenuButtonComponent extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(PauseMenuButtonComponent.class);
    private static final float Z_INDEX = 2f;
    private Window window;
    private final GdxGame game;
    private static final float windowSizeX = 300;
    private static final float windowSizeY = 400;
    private final String[] sounds = {
            "sounds/ui/click/click_01.ogg",
            "sounds/ui/open_close/close_01.ogg",
    };
    private Sound click;
    private Sound closeSound;

    public PauseMenuButtonComponent(GdxGame screenSwitchHandle) {
        game = screenSwitchHandle;
    }

    @Override
    public void create() {
        super.create();
        addActors();
        loadSounds();
    }

    /**
     * Initialises the pause menu buttons
     * Positions them on the stage using a window
     */
    private void addActors() {

        window = new Window("Game Paused", new Skin(Gdx.files.internal("images/ui/buttons/glass.json")));

        TextButton continueBtn = ButtonFactory.createButton("Continue");
        continueBtn.pad(20f);
        TextButton settingsBtn = ButtonFactory.createButton("Settings");
        settingsBtn.pad(20f);
        TextButton planetSelectBtn = ButtonFactory.createButton("Planet Select");
        planetSelectBtn.pad(20f);
        TextButton mainMenuBtn = ButtonFactory.createButton("Main Menu");
        mainMenuBtn.pad(20f);

        continueBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Continue button clicked");
                        click.play(0.5f);
                        closeSound.play(0.5f);
                        entity.dispose();
                    }
                });
        settingsBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        click.play(0.5f);
                        logger.debug("Settings button clicked");
                        game.setScreen(GdxGame.ScreenType.SETTINGS);
                    }
                });
        planetSelectBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        click.play(0.5f);
                        logger.debug("Planet select button clicked");
                        game.setScreen(GdxGame.ScreenType.LEVEL_SELECT);
                    }
                });
        mainMenuBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        click.play(0.5f);
                        logger.debug("Main menu button clicked");
                        game.setScreen(GdxGame.ScreenType.MAIN_MENU);
                    }
                });

        window.setKeepWithinStage(true);
        window.setResizable(true);
        window.setModal(true);
        window.setTouchable(Touchable.enabled);
        window.setMovable(true);
        window.setFillParent(false);
        window.center();
        window.add(continueBtn).center();
        window.row();
        window.add(settingsBtn).center();
        window.row();
        window.add(planetSelectBtn).center();
        window.row();
        window.add(mainMenuBtn).center();
        window.setWidth(windowSizeX);
        window.setHeight(windowSizeY);
        window.setX((ServiceLocator.getRenderService().getStage().getWidth() / 2) - (windowSizeX / 2));
        window.setY((ServiceLocator.getRenderService().getStage().getHeight() / 2) - (windowSizeY / 2));

        stage.addActor(window);
    }

    @Override
    protected void draw(SpriteBatch batch) {
        // handled by stage
    }

    /**
     * Loads sound assets for use in the class
     */
    public void loadSounds() {
        ServiceLocator.getResourceService().loadSounds(sounds);
        ServiceLocator.getResourceService().loadAll();
        click = ServiceLocator.getResourceService().getAsset(sounds[0], Sound.class);
        closeSound = ServiceLocator.getResourceService().getAsset(sounds[1], Sound.class);
    }

    @Override
    public float getZIndex() {
        return Z_INDEX;
    }

    @Override
    public void dispose() {
        window.clear();
        window.remove();
        super.dispose();
    }
}
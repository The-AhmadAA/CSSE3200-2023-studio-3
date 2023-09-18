package com.csse3200.game.components.tasks.bosstask;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.csse3200.game.ai.tasks.DefaultTask;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.components.tasks.MovementTask;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemonBossTask extends DefaultTask implements PriorityTask {

    // Constants
    private static final int PRIORITY = 3;
    private static final Vector2 DEMON_JUMP_SPEED = new Vector2(1f, 1f);
    private static final float STOP_DISTANCE = 0.1f;

    // Private variables
    private static final Logger logger = LoggerFactory.getLogger(DemonBossTask.class);
    private Vector2 currentPos;
    private final PhysicsEngine physics;
    private final GameTime gameTime;
    private Vector2 jumpPos;
    private MovementTask jumpTask;
    private boolean isJumping;
    private DEMON_STATE state;
    private Entity demon;
    private enum DEMON_STATE {
        TRANSFORM, IDLE, CAST, CLEAVE, DEATH, BREATH, SMASH, TAKE_HIT, WALK
    }

    public DemonBossTask() {
        physics = ServiceLocator.getPhysicsService().getPhysics();
        gameTime = ServiceLocator.getTimeSource();
    }

    @Override
    public void start() {
        super.start();
        demon = owner.getEntity();
        state = DEMON_STATE.TRANSFORM;
        this.currentPos = owner.getEntity().getPosition();
        jump(getJumpPos());
    }

    @Override
    public void update() {
        animate(state);
        currentPos = owner.getEntity().getPosition();

        // Check if transform is complete
        if (state.equals(DEMON_STATE.TRANSFORM)) {
            if (demon.getComponent(AnimationRenderComponent.class).isFinished()) {
                state = DEMON_STATE.IDLE;
            }
        }

    }

    private void animate(DEMON_STATE state) {
        switch (state) {
            case CAST -> demon.getEvents().trigger("demon_cast_spell");
            case IDLE -> demon.getEvents().trigger("demon_idle");
            case WALK -> demon.getEvents().trigger("demon_walk");
            case DEATH -> demon.getEvents().trigger("demon_death");
            case BREATH -> demon.getEvents().trigger("demon_breath");
            case SMASH -> demon.getEvents().trigger("demon_smash");
            case CLEAVE -> demon.getEvents().trigger("demon_cleave");
            case TAKE_HIT -> demon.getEvents().trigger("demon_take_hit");
            case TRANSFORM -> demon.getEvents().trigger("transform");
        }
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    private void jump(Vector2 finalPos) {
        // Start animation
        demon.getEvents().trigger("demon_walk");
        jumpTask = new MovementTask(finalPos);
        jumpTask.create(owner);
        demon.getComponent(PhysicsMovementComponent.class).setSpeed(DEMON_JUMP_SPEED);
        logger.debug("Demon jump starting");
        jumpTask.start();
        isJumping = true;
    }

    private Vector2 getJumpPos() {
        // check where demon can jump
        float jumpMinX = currentPos.x - 4;
        float jumpMaxX = currentPos.x + 4;
        float jumpMinY = currentPos.y - 4;
        float jumpMaxY = currentPos.y + 4;

        if (jumpMinX < 1) {
            jumpMinX = 1;
        } else if (jumpMinX > 18) {
            jumpMinX = 18;
        } else if (jumpMinY < 1) {
            jumpMinX = 1;
        } else if (jumpMinY > 7) {
            jumpMinX = 7;
        }

        // generate random jump pos
        float randomX = MathUtils.random(jumpMinX, jumpMaxX);
        float randomY = MathUtils.random(jumpMinY, jumpMaxY);
        return jumpPos = new Vector2(randomX, randomY);
    }

    private boolean isAtTarget() {
        return currentPos.dst(jumpPos) <= STOP_DISTANCE;
    }

    private void JumpComplete() {
        if (isAtTarget() && isJumping) {
            isJumping = false;
            jumpTask.stop();
        }
    }
}
package com.csse3200.game.components.tasks;

import com.csse3200.game.ai.tasks.DefaultTask;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.components.CombatStatsComponent;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.currency.Currency;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.DropFactory;
import com.csse3200.game.entities.factories.ProjectileFactory;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.raycast.RaycastHit;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.GameTime;
//import com.csse3200.game.rendering.DebugRenderer;


/**
 * Task that prints a message to the terminal whenever it is called.
 */
public class MobDeathTask extends DefaultTask implements PriorityTask {
    private static final int INTERVAL = 1; // time interval to scan for towers in

    private final int priority;
    private Vector2 mobPosition = new Vector2(10f,10f);
    private final PhysicsEngine physics;
    private GameTime timeSource;
    private long endTime;
    private final RaycastHit hit = new RaycastHit();

    private int mobHealth;

    /**
     * @param priority Task priority when shooting (0 when not chasing).
     */
    public MobDeathTask(int priority) {
        this.priority = priority;

        physics = ServiceLocator.getPhysicsService().getPhysics();

        timeSource = ServiceLocator.getTimeSource();
    }

    @Override
    public void start() {
        super.start();
        // gets starting health
        this.mobHealth = owner.getEntity().getComponent(CombatStatsComponent.class).getHealth();
        //sets mob position
        this.mobPosition = owner.getEntity().getCenterPosition();
        //sets endTime
        endTime = timeSource.getTime() + (INTERVAL * 500);
    }

    @Override
    public void update() {
        if (timeSource.getTime() >= endTime) {
            updateBossState();
            endTime = timeSource.getTime() + (INTERVAL * 1000);
        }
    }

    public void updateBossState() {

        mobHealth = owner.getEntity().getComponent(CombatStatsComponent.class).getHealth();
        // TODO: inset a bit that picks from a list of drop options and drops this

        if (mobIsDead(mobHealth)) {
            killMob();
            dropCurrency();
        }

    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public int getPriority() {
        if (status == Status.ACTIVE) {
            return getActivePriority();
        }

        return getInactivePriority();
    }

    private int getActivePriority() {
        if (mobHealth > 0) {
            return -1;
        }
        return priority;
    }

    private int getInactivePriority() {
        if (mobHealth <= 0) {
            return priority;
        }
        return -1;
    }
    private boolean mobIsDead(int mobhealth) {

        if (mobhealth <= 0) {
            return true;
        }
        return false;
    }

    private void killMob() {
        owner.getEntity().dispose();
    }

    private void dropCurrency() {

        Entity scrap = DropFactory.createScrapDrop();
        scrap.setPosition(mobPosition.x,mobPosition.y);
        ServiceLocator.getEntityService().register(scrap);

    }

}

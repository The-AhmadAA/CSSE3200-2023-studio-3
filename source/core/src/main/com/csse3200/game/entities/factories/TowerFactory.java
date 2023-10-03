package com.csse3200.game.entities.factories;

import com.csse3200.game.components.tasks.DroidCombatTask;
import com.csse3200.game.components.tasks.TNTTowerCombatTask;
import com.csse3200.game.components.tower.*;
import com.csse3200.game.entities.configs.*;
import com.csse3200.game.components.tasks.FireTowerCombatTask;
import com.csse3200.game.components.tasks.StunTowerCombatTask;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.CostComponent;
import com.csse3200.game.components.tasks.TowerCombatTask;
import com.csse3200.game.components.tasks.CurrencyTask;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.input.UpgradeUIComponent;
/**
 * Factory to create a tower entity.
 *
 * Predefined tower properties are loaded from a config stored as a json file and should have
 * the properties stores in 'baseTowerConfigs'.
 */
public class TowerFactory {

    private static final int COMBAT_TASK_PRIORITY = 2;
    private static final int WEAPON_TOWER_MAX_RANGE = 40;
    private static final int TNT_TOWER_MAX_RANGE = 6;
    private static final int TNT_TOWER_RANGE = 6;
    private static final int TNT_KNOCK_BACK_FORCE = 10;
    private static final String WALL_IMAGE = "images/towers/wall_tower.png";
    private static final String RESOURCE_TOWER = "images/towers/mine_tower.png";
    private static final String TURRET_ATLAS = "images/towers/turret01.atlas";
    private static final String FIRE_TOWER_ATLAS = "images/towers/fire_tower_atlas.atlas";
    private static final String STUN_TOWER_ATLAS = "images/towers/stun_tower.atlas";
    private static final String TNT_ATLAS = "images/towers/TNTTower.atlas";
    private static final String DROID_ATLAS = "images/towers/DroidTower.atlas";
    private static final float DROID_SPEED = 0.25f;
    private static final String DEFAULT_ANIM = "default";
    private static final String WALK_ANIM = "walk";
    private static final String DEATH_ANIM = "death";
    private static final String GO_UP = "goUp";
    private static final String GO_DOWN = "goDown";
    private static final String SHOOT_UP = "attackUp";
    private static final String SHOOT_DOWN = "attackDown";
    private static final float DEFAULT_SPEED= 0.2f;
    private static final String DIG_ANIM = "dig";
    private static final float DIG_SPEED = 0.2f;
    private static final String EXPLODE_ANIM = "explode";
    private static final float EXPLODE_SPEED = 0.2f;
    private static final String IDLE_ANIM = "idle";
    private static final float IDLE_SPEED = 0.3f;
    private static final String DEPLOY_ANIM = "deploy";
    private static final float DEPLOY_SPEED = 0.2f;
    private static final String STOW_ANIM = "stow";
    private static final float STOW_SPEED = 0.2f;
    private static final String FIRE_ANIM = "firing";
    private static final float FIRE_SPEED = 0.25f;
    private static final String FIRE_TOWER_IDLE_ANIM = "idle";
    private static final float FIRE_TOWER_IDLE_SPEED = 0.3f;
    private static final String FIRE_TOWER_PREP_ATTACK_ANIM = "prepAttack";
    private static final float FIRE_TOWER_PREP_ATTACK_SPEED = 0.2f;
    private static final String FIRE_TOWER_ATTACK_ANIM = "attack";
    private static final float FIRE_TOWER_ATTACK_SPEED = 0.25f;
    private static final String FIRE_TOWER_DEATH_ANIM = "death";
    private static final float FIRE_TOWER_DEATH_SPEED = 0.12f;
    private static final String STUN_TOWER_IDLE_ANIM = "idle";
    private static final float STUN_TOWER_IDLE_SPEED = 0.33f;
    private static final String STUN_TOWER_ATTACK_ANIM = "attack";
    private static final float STUN_TOWER_ATTACK_SPEED = 0.12f;
    private static final String STUN_TOWER_DEATH_ANIM = "death";
    private static final float STUN_TOWER_DEATH_SPEED = 0.12f;
    private static final int INCOME_INTERVAL = 300;
    private static final int INCOME_TASK_PRIORITY = 1;
    private static final String ECO_ATLAS = "images/economy/econ-tower.atlas";
    private static final String ECO_MOVE = "move1";
    private static final String ECO_IDLE = "idle";
    private static final float ECO_IDLE_SPEED = 0.3f;

    private static final baseTowerConfigs configs =
            FileLoader.readClass(baseTowerConfigs.class, "configs/tower.json");
    /**
     * Creates an income tower that generates scrap
     * @return income
     */
    public static Entity createIncomeTower() {
        Entity income = createBaseTower();
        IncomeTowerConfig config = configs.income;

        // Create the CurrencyIncomeTask and add it to the AITaskComponent
        CurrencyTask currencyTask = new CurrencyTask(INCOME_TASK_PRIORITY, (int) config.incomeRate);

        AITaskComponent aiTaskComponent = new AITaskComponent().addTask(currencyTask);


        // Contains all the animations that the tower will have
        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService()
                                .getAsset(ECO_ATLAS, TextureAtlas.class));
        animator.addAnimation(ECO_IDLE, ECO_IDLE_SPEED, Animation.PlayMode.LOOP);
        animator.addAnimation(ECO_MOVE, ECO_IDLE_SPEED, Animation.PlayMode.NORMAL);

        income
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(new UpgradableStatsComponent(config.attackRate))
                .addComponent(new CostComponent(config.cost))
                .addComponent(new IncomeUpgradeComponent(config.incomeRate))
                .addComponent(aiTaskComponent)
                .addComponent(animator)
                .addComponent(new EconTowerAnimationController());

        return income;
    }

    public static Entity createWallTower() {
        Entity wall = createBaseTower();
        WallTowerConfig config = configs.wall;

        wall
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(new UpgradableStatsComponent(config.attackRate))
                .addComponent(new CostComponent(config.cost))
                .addComponent(new TextureRenderComponent(WALL_IMAGE));
        return wall;
    }


    /**
     * Create a type of TNT that explodes once it detects a mob within a certain range.
     * Upon detonation, the TNT will apply both knock-back and health damage to the affected mobs
     * @return entity
     */
    public static Entity createTNTTower() {
        Entity TNTTower = createBaseTower();
        TNTTowerConfigs config = configs.TNTTower;

        AITaskComponent aiTaskComponent = new AITaskComponent()
                .addTask(new TNTTowerCombatTask(COMBAT_TASK_PRIORITY, TNT_TOWER_MAX_RANGE));

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService()
                                .getAsset(TNT_ATLAS, TextureAtlas.class));

        animator.addAnimation(DIG_ANIM, DIG_SPEED, Animation.PlayMode.NORMAL);
        animator.addAnimation(DEFAULT_ANIM,DEFAULT_SPEED, Animation.PlayMode.NORMAL);
        animator.addAnimation(EXPLODE_ANIM,EXPLODE_SPEED, Animation.PlayMode.NORMAL);

        TNTTower
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(new UpgradableStatsComponent(config.attackRate))
                .addComponent(new CostComponent(config.cost))
                .addComponent(new TNTDamageComponent(PhysicsLayer.NPC,TNT_KNOCK_BACK_FORCE,TNT_TOWER_RANGE))
                .addComponent(aiTaskComponent)
                .addComponent(animator)
                .addComponent(new TNTAnimationController());

        TNTTower.getComponent(AnimationRenderComponent.class).scaleEntity();

        return TNTTower;
    }

    /**
     * This robotic unit is programmed to detect mobs within its vicinity and fire projectiles at them.
     * The droid has the capability to switch its aim from high to low positions, thereby providing a versatile attack strategy.
     * When it detects a mob, the droid releases a projectile that inflicts both physical damage and a slow-down effect on the target.
     * @return entity
     */
    public static Entity createDroidTower() {
        Entity DroidTower = createBaseTower();
        DroidTowerConfig config = configs.DroidTower;

        AITaskComponent aiTaskComponent = new AITaskComponent()
                .addTask(new DroidCombatTask(COMBAT_TASK_PRIORITY, WEAPON_TOWER_MAX_RANGE));

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService()
                                .getAsset(DROID_ATLAS, TextureAtlas.class));

        animator.addAnimation(IDLE_ANIM, DROID_SPEED, Animation.PlayMode.NORMAL);
        animator.addAnimation(SHOOT_UP,DROID_SPEED, Animation.PlayMode.NORMAL);
        animator.addAnimation(SHOOT_DOWN,DROID_SPEED, Animation.PlayMode.NORMAL);
        animator.addAnimation(WALK_ANIM,DROID_SPEED, Animation.PlayMode.NORMAL);
        animator.addAnimation(DEATH_ANIM,DROID_SPEED, Animation.PlayMode.NORMAL);
        animator.addAnimation(GO_UP,DROID_SPEED, Animation.PlayMode.NORMAL);
        animator.addAnimation(GO_DOWN,DROID_SPEED, Animation.PlayMode.NORMAL);



        DroidTower
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(new UpgradableStatsComponent(config.attackRate))
                .addComponent(new CostComponent(config.cost))
                .addComponent(new DroidAnimationController())
                .addComponent(animator)
                .addComponent(aiTaskComponent);

        return DroidTower;
    }


    /**
     * Creates a weaponry tower that shoots at mobs - This will most likely need to be extended
     * once other types of weapon towers are developed
     * @return entity
     */
    public static Entity createWeaponTower() {
        Entity weapon = createBaseTower();
        WeaponTowerConfig config = configs.weapon;

        // AiTaskComponent will run the tower task which carries out detection of targets and trigger events
        AITaskComponent aiTaskComponent = new AITaskComponent()
                .addTask(new TowerCombatTask(COMBAT_TASK_PRIORITY, WEAPON_TOWER_MAX_RANGE));

        // Contains all the animations that the tower will have
        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService()
                                .getAsset(TURRET_ATLAS, TextureAtlas.class));
        animator.addAnimation(IDLE_ANIM, IDLE_SPEED, Animation.PlayMode.LOOP);
        animator.addAnimation(STOW_ANIM, STOW_SPEED, Animation.PlayMode.NORMAL);
        animator.addAnimation(DEPLOY_ANIM, DEPLOY_SPEED, Animation.PlayMode.REVERSED);
        animator.addAnimation(FIRE_ANIM, FIRE_SPEED, Animation.PlayMode.LOOP);

        weapon
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(new UpgradableStatsComponent(config.attackRate))
                .addComponent(new CostComponent(config.cost))
                .addComponent(new UpgradableStatsComponent(config.attackRate))
                .addComponent(aiTaskComponent)
                .addComponent(animator)
                .addComponent(new TowerAnimationController());

        return weapon;

    }

    /**
     * Creates the FireTower entity which shoots at mobs traversing in a straight line.
     * @return FireTower entity with relevant components.
     */
    public static Entity createFireTower() {
        Entity fireTower = createBaseTower();
        FireTowerConfig config = configs.fireTower;

        //Component that handles triggering events and animations
        AITaskComponent aiTaskComponent = new AITaskComponent()
                .addTask(new FireTowerCombatTask(COMBAT_TASK_PRIORITY, WEAPON_TOWER_MAX_RANGE));

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService()
                                .getAsset(FIRE_TOWER_ATLAS, TextureAtlas.class));
        animator.addAnimation(FIRE_TOWER_IDLE_ANIM, FIRE_TOWER_IDLE_SPEED, Animation.PlayMode.LOOP);
        animator.addAnimation(FIRE_TOWER_PREP_ATTACK_ANIM,  FIRE_TOWER_PREP_ATTACK_SPEED, Animation.PlayMode.NORMAL);
        animator.addAnimation(FIRE_TOWER_ATTACK_ANIM, FIRE_TOWER_ATTACK_SPEED+ 0.25f, Animation.PlayMode.LOOP);
        animator.addAnimation(FIRE_TOWER_DEATH_ANIM, FIRE_TOWER_DEATH_SPEED, Animation.PlayMode.NORMAL);

        fireTower
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(new UpgradableStatsComponent(config.attackRate))
                .addComponent(new CostComponent(config.cost))
                .addComponent(aiTaskComponent)
                .addComponent(animator)
                .addComponent(new FireTowerAnimationController());
        fireTower.setScale(1.25f, 1.25f);
        return fireTower;
    }

    /**
     * Creates the StunTower entity which shoots at mobs traversing in a straight line.
     * @return StunTower entity with relevant components.
     */
    public static Entity createStunTower() {
        Entity stunTower = createBaseTower();
        StunTowerConfig config = configs.stunTower;

        AITaskComponent aiTaskComponent = new AITaskComponent()
                .addTask(new StunTowerCombatTask(COMBAT_TASK_PRIORITY, WEAPON_TOWER_MAX_RANGE));

        AnimationRenderComponent animator =
                new AnimationRenderComponent(
                        ServiceLocator.getResourceService()
                                .getAsset(STUN_TOWER_ATLAS, TextureAtlas.class));
        animator.addAnimation(STUN_TOWER_IDLE_ANIM, STUN_TOWER_IDLE_SPEED, Animation.PlayMode.LOOP);
        animator.addAnimation(STUN_TOWER_ATTACK_ANIM, STUN_TOWER_ATTACK_SPEED+ 0.25f, Animation.PlayMode.LOOP);
        animator.addAnimation(STUN_TOWER_DEATH_ANIM, STUN_TOWER_DEATH_SPEED, Animation.PlayMode.NORMAL);

        stunTower
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(new UpgradableStatsComponent(config.attackRate))
                .addComponent((new CostComponent(config.cost)))
                .addComponent(aiTaskComponent)
                .addComponent(animator)
                .addComponent(new StunTowerAnimationController());

        stunTower.setScale(1.5f, 1.5f);
        PhysicsUtils.setScaledCollider(stunTower, 0.5f, 0.5f);
        return stunTower;
    }

    /**
     * Creates a generic tower entity to be used as a base entity by more specific tower creation methods.
     * @return entity
     */
    public static Entity createBaseTower() {
        // we're going to add more components later on
        Entity tower = new Entity()
                .addComponent(new ColliderComponent())
                .addComponent(new HitboxComponent().setLayer(PhysicsLayer.TOWER)) // TODO: we might have to change the names of the layers
                .addComponent(new PhysicsComponent().setBodyType(BodyType.StaticBody))
                .addComponent(new TowerUpgraderComponent());
        tower.setLayer(1); // Set priority to 1, which is 1 below scrap (which is 0)
        return tower;
    }
}
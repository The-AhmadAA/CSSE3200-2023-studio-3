package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.csse3200.game.components.EffectsComponent;
import com.csse3200.game.components.ProjectileEffects;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.components.RicochetComponent;
import com.csse3200.game.components.SplitFireworksComponent;
import com.csse3200.game.components.projectile.MobKingProjectAnimController;
import com.csse3200.game.components.tasks.TrajectTask;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.DeleteOnMapEdgeComponent;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.entities.configs.NPCConfigs;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.projectile.EngineerBulletsAnimationControlller;
import com.csse3200.game.components.projectile.MobProjectileAnimationController;
import com.csse3200.game.components.projectile.ProjectileAnimationController;

/**
 * Responsible for creating projectiles within the game.
 */
public class ProjectileFactory {
  /** Animation constants */
  private static final String BASE_PROJECTILE_ATLAS = "images/projectiles/basic_projectile.atlas";
  private static final String START_ANIM = "projectile";
  private static final String FINAL_ANIM = "projectileFinal";
  private static final float START_SPEED = 0.1f;
  private static final float FINAL_SPEED = 0.1f;

  private static final NPCConfigs configs = 
      FileLoader.readClass(NPCConfigs.class, "configs/NPCs.json");

  /**
   * Creates a single-targeting projectile with specified effect
   *
   * @param targetLayer The enemy layer that the projectile collides with.
   * @param destination The destination the projectile heads towards.
   * @param speed The speed of the projectile.
   * @param effect Specified effect from the ProjectileEffects enums
   * @return Returns a new single-target projectile entity
   */
  public static Entity createEffectProjectile(short targetLayer, Vector2 destination, Vector2 speed, ProjectileEffects effect, boolean aoe) {
    Entity projectile = createFireBall(targetLayer, destination, speed);

    switch(effect) {
      case FIREBALL -> {
        projectile.addComponent(new EffectsComponent(targetLayer, 3, ProjectileEffects.FIREBALL, aoe));
      }
      case BURN -> {
        projectile.addComponent(new EffectsComponent(targetLayer, 3, ProjectileEffects.BURN, aoe));
      }
      case SLOW -> {
        projectile.addComponent(new EffectsComponent(targetLayer, 3, ProjectileEffects.SLOW, aoe));
      }
      case STUN -> {
        projectile.addComponent(new EffectsComponent(targetLayer, 3, ProjectileEffects.STUN, aoe));
      }
    }
      return projectile;
  }

  /**
   * Create a pierce fireball.
   * Pierce fireball is basically a fireball that does damage but won't self destruct on hit.
   */
  public static Entity createPierceFireBall(short targetLayer, Vector2 destination, Vector2 speed) {
    Entity fireBall = createFireBall(targetLayer, destination, speed);
    fireBall.getComponent(TouchAttackComponent.class).setDisposeOnHit(false);

    return fireBall;
  }

  /**
   * Create a ricochet fireball.
   * Ricochet fireball bounces off specified targets while applying intended effects i.e. damage
   */
  public static Entity createRicochetFireball(short targetLayer, Vector2 destination, Vector2 speed, int bounceCount) {
    Entity fireBall = createFireBall(targetLayer, destination, speed);
    fireBall
      .addComponent(new RicochetComponent(targetLayer, bounceCount));
    
    setColliderSize(fireBall, (float) 0.1, (float) 0.1);

    return fireBall;
  }

  public static Entity createSplitFireWorksFireball(short targetLayer, Vector2 destination, Vector2 speed, int amount) {
    Entity fireBall = createFireBall(targetLayer, destination, speed);
    fireBall
      .addComponent(new SplitFireworksComponent(targetLayer, amount));
    
    return fireBall;
  }

  /**
   * Creates a fireball Entity.
   * 
   * @param targetLayer The enemy layer that the projectile collides with.
   * @param destination The destination the projectile heads towards.
   * @param speed The speed of the projectile.
   * @return Returns a new fireball projectile entity.
   */
  public static Entity createFireBall(short targetLayer, Vector2 destination, Vector2 speed) {
    Entity projectile = createBaseProjectile(targetLayer, destination, speed);

    AnimationRenderComponent animator =
            new AnimationRenderComponent(
                    ServiceLocator.getResourceService()
                            .getAsset(BASE_PROJECTILE_ATLAS, TextureAtlas.class));
    animator.addAnimation(START_ANIM, START_SPEED, Animation.PlayMode.NORMAL);
    animator.addAnimation(FINAL_ANIM, FINAL_SPEED, Animation.PlayMode.NORMAL);

    projectile
        .addComponent(animator)
        .addComponent(new ProjectileAnimationController());
        // * TEMPORARY
        // .addComponent(new DeleteOnMapEdgeComponent());
        // .addComponent(new SelfDestructOnHitComponent(PhysicsLayer.OBSTACLE));

    return projectile;
  }
  /**
   * Creates a engineer bullet
   * 
   * @param targetLayer The enemy layer that the projectile collides with.
   * @param destination The destination the projectile heads towards.
   * @param speed The speed of the projectile.
   * @return Returns a new fireball projectile entity.
   */
  public static Entity createEngineerBullet(short targetLayer, Vector2 destination, Vector2 speed) {
    Entity projectile = createBaseProjectile(targetLayer, destination, speed);

    AnimationRenderComponent animator =
            new AnimationRenderComponent(
                    ServiceLocator.getResourceService()
                            .getAsset("images/projectiles/engineer_projectile.atlas", TextureAtlas.class));
    animator.addAnimation("bullet", START_SPEED, Animation.PlayMode.NORMAL);
    animator.addAnimation("bulletFinal", FINAL_SPEED, Animation.PlayMode.NORMAL);

    projectile
        .addComponent(animator)
        .addComponent(new EngineerBulletsAnimationControlller());
        // .addComponent(new SelfDestructOnHitComponent(PhysicsLayer.OBSTACLE));

    return projectile;
  }
  
  /**
   * Creates a projectile specifically for mobs to shoot
   * 
   * @param targetLayer The enemy layer that the projectile collides with.
   * @param destination The destination the projectile heads towards.
   * @param speed The speed of the projectile.
   * @return Returns a new fireball projectile entity.
   */
  public static Entity createMobBall(short targetLayer, Vector2 destination, Vector2 speed) {
    Entity projectile = createBaseProjectile(targetLayer, destination, speed);

    AnimationRenderComponent animator = 
      new AnimationRenderComponent(
        ServiceLocator.getResourceService()
          .getAsset("images/projectiles/mobProjectile.atlas", TextureAtlas.class));

      animator.addAnimation("rotate", 0.15f, Animation.PlayMode.LOOP);

    projectile
        .addComponent(animator)
        .addComponent(new MobProjectileAnimationController());
        // * TEMPORARY
        // .addComponent(new DeleteOnMapEdgeComponent());

    projectile
        .getComponent(AnimationRenderComponent.class).scaleEntity();

    return projectile;
  }

  /**
   * Creates a projectile to be used by the MobKing
   *
   * @param targetLayer The enemy layer that the projectile collides with.
   * @param destination The destination the projectile heads towards.
   * @param speed The speed of the projectile.
   * @return Returns a new fireball projectile entity.
   */
  public static Entity createMobKingBall(short targetLayer, Vector2 destination, Vector2 speed) {
    Entity projectile = createBaseProjectile(targetLayer, destination, speed);

    AnimationRenderComponent animator =
            new AnimationRenderComponent(
                    ServiceLocator.getResourceService()
                            .getAsset("images/projectiles/mobKing_projectile.atlas", TextureAtlas.class));
    animator.addAnimation("mob_boss", 0.17f, Animation.PlayMode.NORMAL);
    animator.addAnimation("mob_bossFinal", 0.17f, Animation.PlayMode.NORMAL);


    projectile
            .addComponent(animator)
            .addComponent(new MobKingProjectAnimController());

    projectile
            .getComponent(AnimationRenderComponent.class).scaleEntity();

    return projectile;
  }

  /**
   * Creates a generic projectile entity that can be used for multiple types of * projectiles.
   * 
   * @param targetLayer The enemy layer that the projectile collides with.
   * @param destination The destination the projectile heads towards.
   * @param speed The speed of the projectile.
   * @return Returns a generic projectile entity.
   */
  public static Entity createBaseProjectile(short targetLayer, Vector2
  destination, Vector2 speed) {
    BaseEntityConfig config = configs.fireBall;

    AITaskComponent aiComponent =
        new AITaskComponent()
            .addTask(new TrajectTask(destination));
    
    Entity projectile = new Entity()
        .addComponent(new PhysicsComponent())
        .addComponent(new PhysicsMovementComponent())
        .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PROJECTILE))
        .addComponent(aiComponent)
        .addComponent(new ColliderComponent().setSensor(true))
        // This is the component that allows the projectile to damage a
        // specified target.
        // Original knockback value: 1.5f
        .addComponent(new TouchAttackComponent(targetLayer, 1.5f, true))
        .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
        // *TEMPORARY
        .addComponent(new DeleteOnMapEdgeComponent());

        projectile
        .getComponent(PhysicsMovementComponent.class).setSpeed(speed);

    return projectile;
  }

  /**
   * Sets the projectile collider so that the collider size can be altered for flexibility.
   * @param projectile Projectile's size collider to be scaled upon.
   * @param x Horizontal scaling of the collider in respect ot the size of the entity
   * @param y Veritcal scaling of the collider in respect to the size of the entity
   */
  public static void setColliderSize(Entity projectile, float x, float y) {
    PhysicsUtils.setScaledCollider(projectile, x, y);
  }

  /**
   * Prevents the creation of a Projectile Factory entity being created
   */
  private ProjectileFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}

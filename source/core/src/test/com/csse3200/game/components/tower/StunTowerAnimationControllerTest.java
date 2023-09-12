package com.csse3200.game.components.tower;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.TowerFactory;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
public class StunTowerAnimationControllerTest {

    private Entity mockEntity;
    private final String[] texture = {"images/towers/stun_tower.png"};
    private final String[] atlas = {"images/towers/stun_tower.atlas"};

    @BeforeEach
    public void setUp() {
        ServiceLocator.registerPhysicsService(new PhysicsService());
        RenderService render = new RenderService();
        render.setDebug(mock(DebugRenderer.class));
        ServiceLocator.registerRenderService(render);
        ResourceService resourceService = new ResourceService();
        ServiceLocator.registerResourceService(resourceService);
        resourceService.loadTextures(texture);
        resourceService.loadTextureAtlases(atlas);
        resourceService.loadAll();

        mockEntity = TowerFactory.createStunTower(); // Replace with actual Droid Tower creation logic
        mockEntity.create();
    }

    @Test
    public void testAnimateWalk() {
        mockEntity.getEvents().trigger("startIdle");
        assertEquals("idle", mockEntity.getComponent(AnimationRenderComponent.class).getCurrentAnimation());
    }

    @Test
    public void testAnimateDefault() {
        mockEntity.getEvents().trigger("startAttack");
        assertEquals("attack", mockEntity.getComponent(AnimationRenderComponent.class).getCurrentAnimation());
    }

    @Test
    public void testAnimateGoUp() {
        mockEntity.getEvents().trigger("startDeath");
        assertEquals("death", mockEntity.getComponent(AnimationRenderComponent.class).getCurrentAnimation());
    }
}

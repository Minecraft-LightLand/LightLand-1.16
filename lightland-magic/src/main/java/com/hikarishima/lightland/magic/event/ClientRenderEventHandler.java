package com.hikarishima.lightland.magic.event;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.MagicRenderState;
import com.hikarishima.lightland.magic.registry.ParticleRegistry;
import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import com.hikarishima.lightland.magic.registry.effect.EmeraldPopeEffect;
import com.hikarishima.lightland.magic.registry.item.combat.IGlowingTarget;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.hikarishima.lightland.proxy.Proxy;
import com.lcy0x1.core.math.AutoAim;
import com.lcy0x1.core.util.SerialClass;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.*;

@SuppressWarnings("unused")
public class ClientRenderEventHandler {

    public static final ResourceLocation RL_ENTITY_BODY_ICON = new ResourceLocation(LightLandMagic.MODID, "textures/arcane_icon.png");
    public static final ResourceLocation WATER_TRAP_ICON = new ResourceLocation(LightLandMagic.MODID, "textures/water_trap_icon.png");

    private static final Map<UUID, Map<Effect, Integer>> EFFECT_MAP = new HashMap<>();
    private static final Set<Effect> TRACKED = new HashSet<>();

    private static Entity target;

    public static void init() {
        TRACKED.add(VanillaMagicRegistry.EFF_ARCANE.get());
        TRACKED.add(VanillaMagicRegistry.EFF_WATER_TRAP.get());
        TRACKED.add(VanillaMagicRegistry.EFF_EMERALD.get());
    }

    public static void clear() {
        target = null;
        EFFECT_MAP.clear();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onLivingEntityRender(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        LivingRenderer<?, ?> renderer = event.getRenderer();
        if (EFFECT_MAP.containsKey(entity.getUUID())) {
            Map<Effect, Integer> map = EFFECT_MAP.get(entity.getUUID());
            if (map.containsKey(VanillaMagicRegistry.EFF_ARCANE.get())) {
                renderIcon(entity, event.getMatrixStack(), event.getBuffers(), renderer.getDispatcher(), RL_ENTITY_BODY_ICON);
            }
            if (map.containsKey(VanillaMagicRegistry.EFF_WATER_TRAP.get())) {
                renderIcon(entity, event.getMatrixStack(), event.getBuffers(), renderer.getDispatcher(), WATER_TRAP_ICON);
            }
            if (map.containsKey(VanillaMagicRegistry.EFF_EMERALD.get())) {
                if (!Minecraft.getInstance().isPaused() && entity != Proxy.getClientPlayer()) {
                    int lv = map.get(VanillaMagicRegistry.EFF_EMERALD.get());
                    int r = EmeraldPopeEffect.RADIUS * (1 + lv);
                    int count = (1 + lv) * (1 + lv) * 4;
                    for (int i = 0; i < count; i++) {
                        addParticle(entity.level, entity.position(), r);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.level.isClientSide()) {
            UUID id = Proxy.getClientPlayer().getUUID();
            if (!Minecraft.getInstance().isPaused() && EFFECT_MAP.containsKey(id) && EFFECT_MAP.get(id).containsKey(VanillaMagicRegistry.EFF_EMERALD.get())) {
                Entity entity = Proxy.getClientPlayer();
                Map<Effect, Integer> map = EFFECT_MAP.get(id);
                int lv = map.get(VanillaMagicRegistry.EFF_EMERALD.get());
                int r = EmeraldPopeEffect.RADIUS * (1 + lv);
                int count = (1 + lv) * (1 + lv) * 4;
                for (int i = 0; i < count; i++) {
                    addParticle(entity.level, entity.position(), r);
                }
            }
        }
    }

    private void addParticle(World w, Vector3d vec, int r) {
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;
        float tpi = (float) (Math.PI * 2);
        Vector3d v0 = new Vector3d(0, r, 0);
        Vector3d v1 = v0.xRot(tpi / 3).yRot((float) (Math.random() * tpi));
        float a0 = (float) (Math.random() * tpi);
        float b0 = (float) Math.acos(2 * Math.random() - 1);
        v0 = v0.xRot(a0).yRot(b0);
        v1 = v1.xRot(a0).yRot(b0);
        w.addAlwaysVisibleParticle(ParticleRegistry.EMERALD.get(),
                vec.x + v0.x,
                vec.y + v0.y,
                vec.z + v0.z,
                vec.x + v1.x,
                vec.y + v1.y,
                vec.z + v1.z);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (target != null) {
            target.setGlowing(false);
        }
        target = null;
        ItemStack stack = Proxy.getClientPlayer().getMainHandItem();
        if (stack.getItem() instanceof IGlowingTarget) {
            int dist = ((IGlowingTarget) stack.getItem()).getDistance(stack);
            EntityRayTraceResult result = AutoAim.rayTraceEntity(Proxy.getClientPlayer(), dist, e -> e instanceof LivingEntity);
            if (result != null) {
                Entity entity = result.getEntity();
                if (!entity.isGlowing()) {
                    target = result.getEntity();
                    target.setGlowing(true);
                }
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onLivingRenderPost(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity() == target) {
            target.setGlowing(false);
            target = null;
        }
    }

    @SubscribeEvent
    public void onPotionAddedEvent(PotionEvent.PotionAddedEvent event) {
        if (TRACKED.contains(event.getPotionEffect().getEffect())) {
            onEffectAppear(event.getPotionEffect().getEffect(), event.getEntityLiving(), event.getPotionEffect().getAmplifier());
        }
    }

    @SubscribeEvent
    public void onPotionExpiryEvent(PotionEvent.PotionExpiryEvent event) {
        if (event.getPotionEffect() != null && TRACKED.contains(event.getPotionEffect().getEffect())) {
            onEffectDisappear(event.getPotionEffect().getEffect(), event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof LivingEntity))
            return;
        LivingEntity le = (LivingEntity) event.getTarget();
        for (Effect eff : le.getActiveEffectsMap().keySet()) {
            if (TRACKED.contains(eff)) {
                onEffectAppear(eff, le, le.getActiveEffectsMap().get(eff).getAmplifier());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerStopTracking(PlayerEvent.StopTracking event) {
        if (!(event.getTarget() instanceof LivingEntity))
            return;
        LivingEntity le = (LivingEntity) event.getTarget();
        for (Effect eff : le.getActiveEffectsMap().keySet()) {
            if (TRACKED.contains(eff)) {
                onEffectDisappear(eff, le);
            }
        }
    }

    @SubscribeEvent
    public void onServerPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity e = (ServerPlayerEntity) event.getPlayer();
        if (e != null) {
            for (Effect eff : e.getActiveEffectsMap().keySet()) {
                if (TRACKED.contains(eff)) {
                    onEffectAppear(eff, e, e.getActiveEffectsMap().get(eff).getAmplifier());
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayerEntity e = (ServerPlayerEntity) event.getPlayer();
        if (e != null) {
            for (Effect eff : e.getActiveEffectsMap().keySet()) {
                if (TRACKED.contains(eff)) {
                    onEffectDisappear(eff, e);
                }
            }
        }
    }

    private static void onEffectAppear(Effect eff, LivingEntity e, int lv) {
        PacketHandler.distribute(e, new EffectToClient(eff, lv, e, true));
    }

    private static void onEffectDisappear(Effect eff, LivingEntity e) {
        PacketHandler.distribute(e, new EffectToClient(eff, 0, e, false));
    }

    public static void handle(EffectToClient eff, NetworkEvent.Context sup) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleImpl(eff));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleImpl(EffectToClient eff) {
        Map<Effect, Integer> set = EFFECT_MAP.get(eff.id);
        if (eff.exists) {
            if (set == null) {
                EFFECT_MAP.put(eff.id, set = new HashMap<>());
            }
            set.put(eff.eff, eff.lv);
        } else if (set != null) {
            set.remove(eff.eff);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void renderIcon(Entity entity, MatrixStack matrix, IRenderTypeBuffer buffer, EntityRendererManager manager, ResourceLocation rl) {
        float f = entity.getBbHeight() / 2;
        matrix.pushPose();
        matrix.translate(0, f, 0);
        matrix.mulPose(manager.cameraOrientation());
        MatrixStack.Entry entry = matrix.last();
        IVertexBuilder ivertexbuilder = buffer.getBuffer(MagicRenderState.get2DIcon(rl));
        iconVertex(entry, ivertexbuilder, 0.5f, -0.5f, 0, 1);
        iconVertex(entry, ivertexbuilder, -0.5f, -0.5f, 1, 1);
        iconVertex(entry, ivertexbuilder, -0.5f, 0.5f, 1, 0);
        iconVertex(entry, ivertexbuilder, 0.5f, 0.5f, 0, 0);
        matrix.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private static void iconVertex(MatrixStack.Entry entry, IVertexBuilder builder, float x, float y, float u, float v) {
        builder.vertex(entry.pose(), x, y, 0)
                .uv(u, v)
                .normal(entry.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @SerialClass
    public static class EffectToClient extends PacketHandler.BaseSerialMsg {

        @SerialClass.SerialField
        public UUID id;
        @SerialClass.SerialField
        public Effect eff;
        @SerialClass.SerialField
        public boolean exists;
        @SerialClass.SerialField
        public int lv;

        @Deprecated
        public EffectToClient() {

        }

        private EffectToClient(Effect eff, int lv, LivingEntity le, boolean appear) {
            id = le.getUUID();
            this.eff = eff;
            exists = appear;
            this.lv = lv;
        }

    }

}

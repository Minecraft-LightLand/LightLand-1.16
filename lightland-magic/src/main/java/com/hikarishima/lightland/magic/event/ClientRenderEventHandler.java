package com.hikarishima.lightland.magic.event;

import com.hikarishima.lightland.magic.LightLandMagic;
import com.hikarishima.lightland.magic.MagicRenderState;
import com.hikarishima.lightland.magic.registry.VanillaMagicRegistry;
import com.hikarishima.lightland.proxy.PacketHandler;
import com.lcy0x1.core.util.SerialClass;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ClientRenderEventHandler {

    public static final ResourceLocation RL_ENTITY_BODY_ICON = new ResourceLocation(LightLandMagic.MODID, "textures/arcane_icon.png");
    public static final ResourceLocation WATER_TRAP_ICON = new ResourceLocation(LightLandMagic.MODID, "textures/water_trap_icon.png");

    private static final Map<UUID, Set<Effect>> EFFECT_MAP = new HashMap<>();
    private static final Set<Effect> TRACKED = new HashSet<>();

    public static void init() {
        TRACKED.add(VanillaMagicRegistry.ARCANE);
        TRACKED.add(VanillaMagicRegistry.WATER_TRAP);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onLivingEntityRender(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity entity = event.getEntity();
        LivingRenderer<?, ?> renderer = event.getRenderer();
        if (EFFECT_MAP.containsKey(entity.getUUID())) {
            Set<Effect> set = EFFECT_MAP.get(entity.getUUID());
            if (set.contains(VanillaMagicRegistry.ARCANE)) {
                renderIcon(entity, event.getMatrixStack(), event.getBuffers(), renderer.getDispatcher(), RL_ENTITY_BODY_ICON);
            }
            if (set.contains(VanillaMagicRegistry.WATER_TRAP)) {
                renderIcon(entity, event.getMatrixStack(), event.getBuffers(), renderer.getDispatcher(), WATER_TRAP_ICON);
            }
        }
    }

    @SubscribeEvent
    public void onPotionAddedEvent(PotionEvent.PotionAddedEvent event) {
        if (TRACKED.contains(event.getPotionEffect().getEffect())) {
            onEffectAppear(event.getPotionEffect().getEffect(), event.getEntityLiving());
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
                onEffectAppear(eff, le);
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

    private static void onEffectAppear(Effect eff, LivingEntity e) {
        PacketHandler.distribute(e, new EffectToClient(eff, e, true));
    }

    private static void onEffectDisappear(Effect eff, LivingEntity e) {
        PacketHandler.distribute(e, new EffectToClient(eff, e, false));
    }

    public static void handle(EffectToClient eff, NetworkEvent.Context sup) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleImpl(eff));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleImpl(EffectToClient eff) {
        Set<Effect> set = EFFECT_MAP.get(eff.id);
        if (eff.exists) {
            if (set == null) {
                EFFECT_MAP.put(eff.id, set = new HashSet<>());
            }
            set.add(eff.eff);
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

        @Deprecated
        public EffectToClient() {

        }

        private EffectToClient(Effect eff, LivingEntity le, boolean appear) {
            id = le.getUUID();
            this.eff = eff;
            exists = appear;
        }

    }

}

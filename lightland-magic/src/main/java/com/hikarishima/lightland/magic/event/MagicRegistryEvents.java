package com.hikarishima.lightland.magic.event;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.arcane.ArcaneRegistry;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.products.MagicProductType;
import com.hikarishima.lightland.magic.profession.Profession;
import com.hikarishima.lightland.magic.skills.Skill;
import com.hikarishima.lightland.magic.skills.SkillRegistry;
import com.hikarishima.lightland.magic.spell.SpellRegistry;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.hikarishima.lightland.registry.RegistryBase;
import com.lcy0x1.core.util.Automator;
import com.lcy0x1.core.util.Serializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("unused")
public class MagicRegistryEvents {

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        MagicRegistry.createRegistries();
        RegistryBase.process(MagicRegistry.class, IForgeRegistry.class, MagicRegistryEvents::regSerializer);
    }

    private static <T extends IForgeRegistryEntry<T>> void regSerializer(IForgeRegistry<T> r) {
        new Serializer.RLClassHandler<>(r.getRegistrySuperType(), () -> r);
        new Automator.RegistryClassHandler<>(r.getRegistrySuperType(), () -> r);
    }

    @SubscribeEvent
    public static void onMagicElementRegistry(RegistryEvent.Register<MagicElement> event) {
        RegistryBase.process(MagicRegistry.class, MagicElement.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onMagicProductTypeRegistry(RegistryEvent.Register<MagicProductType<?, ?>> event) {
        RegistryBase.process(MagicRegistry.class, MagicProductType.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onArcaneTypeRegistry(RegistryEvent.Register<ArcaneType> event) {
        RegistryBase.process(ArcaneType.class, ArcaneType.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onArcaneRegistry(RegistryEvent.Register<Arcane> event) {
        RegistryBase.process(ArcaneRegistry.class, Arcane.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onSpellRegistry(RegistryEvent.Register<Spell<?, ?>> event) {
        RegistryBase.process(SpellRegistry.class, Spell.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onProfessionRegistry(RegistryEvent.Register<Profession> event) {
        RegistryBase.process(MagicRegistry.class, Profession.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onSkillRegistry(RegistryEvent.Register<Skill> event) {
        RegistryBase.process(SkillRegistry.class, Skill.class, event.getRegistry()::register);
    }

}

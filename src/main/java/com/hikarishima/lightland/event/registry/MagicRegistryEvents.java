package com.hikarishima.lightland.event.registry;

import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.arcane.ArcaneRegistry;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.profession.Profession;
import com.hikarishima.lightland.magic.spell.Spell;
import com.hikarishima.lightland.magic.spell.SpellRegistry;
import com.hikarishima.lightland.registry.RegistryBase;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@SuppressWarnings("unused")
public class MagicRegistryEvents {

    @SubscribeEvent
    public static void onMagicElementRegistry(RegistryEvent.Register<MagicElement> event) {
        RegistryBase.process(MagicRegistry.class, MagicElement.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onMagicProductTypeRegistry(RegistryEvent.Register<MagicRegistry.MPTRaw> event) {
        RegistryBase.process(MagicRegistry.class, MagicRegistry.MPTRaw.class, event.getRegistry()::register);
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
    public static void onSpellRegistry(RegistryEvent.Register<Spell> event){
        RegistryBase.process(SpellRegistry.class, Spell.class, event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onProfessionRegistry(RegistryEvent.Register<Profession> event) {
        RegistryBase.process(MagicRegistry.class, Profession.class, event.getRegistry()::register);
    }

}

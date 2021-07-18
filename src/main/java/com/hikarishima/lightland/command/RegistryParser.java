package com.hikarishima.lightland.command;

import com.hikarishima.lightland.config.Translator;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RegistryParser<T extends IForgeRegistryEntry<T>> implements ArgumentType<T> {

    public static final RegistryParser<MagicElement> ELEMENT = new RegistryParser<>(MagicElement.class, () -> MagicRegistry.ELEMENT);
    public static final RegistryParser<MagicRegistry.MPTRaw> PRODUCT_TYPE = new RegistryParser<>(MagicRegistry.MPTRaw.class, () -> MagicRegistry.PRODUCT_TYPE);
    public static final RegistryParser<ArcaneType> ARCANE_TYPE = new RegistryParser<>(ArcaneType.class, () -> MagicRegistry.ARCANE_TYPE);
    public static final RegistryParser<Arcane> ARCANE = new RegistryParser<>(Arcane.class, () -> MagicRegistry.ARCANE);

    public final Class<T> cls;
    public final Supplier<IForgeRegistry<T>> registry;

    public RegistryParser(Class<T> cls, Supplier<IForgeRegistry<T>> registry) {
        this.cls = cls;
        this.registry = registry;
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        ResourceLocation rl = ResourceLocation.read(reader);
        T val = registry.get().getValue(rl);
        if (val == null) {
            reader.setCursor(cursor);
            throw new DynamicCommandExceptionType((obj) -> Translator.get("argument.invalid_id", obj)).createWithContext(reader, rl.toString());
        }
        return val;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(registry.get().getKeys(), builder);
    }

}

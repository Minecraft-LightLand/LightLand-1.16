package com.hikarishima.lightland.magic.command;

import com.google.gson.JsonObject;
import com.hikarishima.lightland.magic.MagicElement;
import com.hikarishima.lightland.magic.MagicRegistry;
import com.hikarishima.lightland.magic.Translator;
import com.hikarishima.lightland.magic.arcane.internal.Arcane;
import com.hikarishima.lightland.magic.arcane.internal.ArcaneType;
import com.hikarishima.lightland.magic.products.MagicProductType;
import com.hikarishima.lightland.magic.profession.Profession;
import com.hikarishima.lightland.magic.spell.internal.Spell;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "rawtypes"})
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RegistryParser<T extends IForgeRegistryEntry<T>> implements ArgumentType<T> {

    public static final Set<RegistryParser<?>> SET = new HashSet<>();

    public static final RegistryParser<MagicElement> ELEMENT = new RegistryParser<>(MagicElement.class, () -> MagicRegistry.ELEMENT);
    public static final RegistryParser<MagicProductType<?, ?>> PRODUCT_TYPE = new RegistryParser(MagicProductType.class, () -> MagicRegistry.PRODUCT_TYPE);
    public static final RegistryParser<ArcaneType> ARCANE_TYPE = new RegistryParser<>(ArcaneType.class, () -> MagicRegistry.ARCANE_TYPE);
    public static final RegistryParser<Arcane> ARCANE = new RegistryParser<>(Arcane.class, () -> MagicRegistry.ARCANE);
    public static final RegistryParser<Spell<?, ?>> SPELL = new RegistryParser(Spell.class, () -> MagicRegistry.SPELL);
    public static final RegistryParser<Profession> PROFESSION = new RegistryParser<>(Profession.class, () -> MagicRegistry.PROFESSION);

    public static void register() {
        ArgumentTypes.register("lightland_registry", (Class<RegistryParser<?>>) (Class) RegistryParser.class, new IArgumentSerializer<RegistryParser<?>>() {
            @Override
            public void serializeToNetwork(RegistryParser<?> e, PacketBuffer packet) {
                IForgeRegistry<?> reg = e.registry.get();
                packet.writeUtf(reg.getRegistryName().toString());
            }

            @Override
            public RegistryParser<?> deserializeFromNetwork(PacketBuffer packet) {
                String name = packet.readUtf();
                return Objects.requireNonNull(SET.stream().filter(e -> e.registry.get().getRegistryName().toString().equals(name)).findFirst().orElse(null));
            }

            @Override
            public void serializeToJson(RegistryParser<?> e, JsonObject json) {
                IForgeRegistry<?> reg = e.registry.get();
                json.addProperty("id", reg.getRegistryName().toString());
            }
        });
    }

    public final Class<T> cls;
    public final Supplier<IForgeRegistry<T>> registry;

    public RegistryParser(Class<T> cls, Supplier<IForgeRegistry<T>> registry) {
        this.cls = cls;
        this.registry = registry;
        SET.add(this);
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

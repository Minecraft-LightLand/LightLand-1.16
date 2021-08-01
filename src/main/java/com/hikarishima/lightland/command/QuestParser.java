package com.hikarishima.lightland.command;

import com.hikarishima.lightland.npc.dialog.Dialog;
import com.hikarishima.lightland.npc.dialog.DialogSelector;
import com.hikarishima.lightland.npc.quest.QuestScene;
import com.hikarishima.lightland.proxy.Proxy;
import com.hikarishima.lightland.recipe.ConfigRecipe;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuestParser<T> implements ArgumentType<String> {

    public static final QuestParser<QuestScene> QUEST = new QuestParser<>(QuestScene.class);
    public static final QuestParser<DialogSelector> SELECTOR = new QuestParser<>(DialogSelector.class);
    public static final QuestParser<Dialog> DIALOG = new QuestParser<>(Dialog.class);

    public static final String ALL = "all";

    private final Class<T> cls;

    private QuestParser(Class<T> cls) {
        this.cls = cls;
    }

    @Override
    public String parse(StringReader reader) {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        World world = Proxy.getWorld();
        Stream<Map.Entry<String, T>> stream = ConfigRecipe.stream(world, ConfigRecipe.DIALOG, cls);
        List<String> list = stream.map(Map.Entry::getKey).collect(Collectors.toList());
        list.add(0, ALL);
        return ISuggestionProvider.suggest(list, builder);
    }
}

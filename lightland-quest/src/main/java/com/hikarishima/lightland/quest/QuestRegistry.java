package com.hikarishima.lightland.quest;

import com.hikarishima.lightland.quest.gui.QuestScreen;
import com.hikarishima.lightland.registry.ItemRegistry;
import com.hikarishima.lightland.registry.item.ScreenBook;
import net.minecraft.item.Item;

import java.util.function.Function;

@SuppressWarnings("unused")
public class QuestRegistry {

    public static final ScreenBook QUEST_BOOK = regItem("quest_book", p -> new ScreenBook(p, () -> QuestScreen::new));

    public static <T extends Item> T regItem(String name, Function<Item.Properties, T> func) {
        T item = func.apply(new Item.Properties().tab(ItemRegistry.ITEM_GROUP));
        item.setRegistryName(LightLandQuest.MODID, name);
        return item;
    }

}

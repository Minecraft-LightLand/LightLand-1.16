package com.hikarishima.lightland.magic.compat.patchouli;

import com.hikarishima.lightland.magic.LightLandMagic;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.patchouli.api.BookContentsReloadEvent;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PatchouliEventListener {

    public static final ResourceLocation RL = new ResourceLocation(LightLandMagic.MODID, "magic_guide_book");

    @SubscribeEvent
    public void reloadEvent(BookContentsReloadEvent event) {
        System.out.println(event.book);
        if (event.book.equals(RL)) {
            Book book = BookRegistry.INSTANCE.books.get(event.book);
            List<ResourceLocation> list = new ArrayList<>();
            BookContentExternalLoader.INSTANCE.findFiles(book, "entries", list);
            list = list.stream().map(e -> new ResourceLocation(LightLandMagic.MODID, e.getPath())).collect(Collectors.toList());
            String bookName = book.id.getPath();
            list.stream().map(id -> loadEntry(id, new ResourceLocation(id.getNamespace(),
                    String.format("%s/%s/%s/entries/%s.json", BookRegistry.BOOKS_LOCATION, bookName, "en_us", id.getPath())), book))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(b -> book.contents.entries.put(b.getId(), b));
        }
    }

    private Optional<BookEntry> loadEntry(ResourceLocation id, ResourceLocation file, Book book) {
        try (Reader stream = loadLocalizedJson(book, file)) {
            BookEntry entry = ClientBookRegistry.INSTANCE.gson.fromJson(stream, BookEntry.class);
            if (entry == null) {
                throw new IllegalArgumentException(file + " does not exist.");
            }

            entry.setBook(book);
            if (entry.canAdd()) {
                BookCategory category = entry.getCategory();
                if (category != null) {
                    category.addEntry(entry);
                } else {
                    String msg = String.format("Entry in file %s does not have a valid category.", file);
                    throw new RuntimeException(msg);
                }

                entry.setId(id);
                return Optional.of(entry);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return Optional.empty();
    }

    private BufferedReader loadLocalizedJson(Book book, ResourceLocation res) {
        ResourceLocation localized = new ResourceLocation(res.getNamespace(),
                res.getPath().replaceAll("en_us", ClientBookRegistry.INSTANCE.currentLang));

        InputStream input = BookContentExternalLoader.INSTANCE.loadJson(book, localized, res);
        if (input == null) {
            throw new IllegalArgumentException(res + " does not exist.");
        }

        return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
    }

}

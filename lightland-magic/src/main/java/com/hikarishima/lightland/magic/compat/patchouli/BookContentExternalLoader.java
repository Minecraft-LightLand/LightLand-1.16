package com.hikarishima.lightland.magic.compat.patchouli;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookFolderLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class BookContentExternalLoader {
	public static final BookContentExternalLoader INSTANCE = new BookContentExternalLoader();

	private BookContentExternalLoader() {}

	public void findFiles(Book book, String dir, List<ResourceLocation> list) {
		File root = new File(BookFolderLoader.loadDir, book.id.getPath());
		File enUs = new File(root, "en_us");
		if (enUs.exists()) {
			File searchDir = new File(enUs, dir);
			if (searchDir.exists()) {
				crawl(searchDir, searchDir, list);
			}
		}
	}

	private void crawl(File realRoot, File root, List<ResourceLocation> list) {
		File[] files = root.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				crawl(realRoot, f, list);
			} else if (f.getName().endsWith(".json")) {
				list.add(relativize(realRoot, f));
			}
		}
	}

	private ResourceLocation relativize(File root, File f) {
		String rootPath = root.getAbsolutePath();
		String filePath = f.getAbsolutePath().substring(rootPath.length() + 1);
		String cleanPath = FilenameUtils.removeExtension(FilenameUtils.separatorsToUnix(filePath));

		return new ResourceLocation(Patchouli.MOD_ID, cleanPath);
	}

	public InputStream loadJson(Book book, ResourceLocation resloc, ResourceLocation fallback) {
		String realPath = resloc.getPath().substring(BookFolderLoader.loadDir.getName().length());
		File targetFile = new File(BookFolderLoader.loadDir, realPath);

		if (targetFile.exists()) {
			FileInputStream stream;
			try {
				stream = new FileInputStream(targetFile);
				return stream;
			} catch (IOException e) {
				Patchouli.LOGGER.catching(e);
			}
		}

		if (fallback != null) {
			Patchouli.LOGGER.warn("Failed to load " + resloc + ". Switching to fallback.");
			return loadJson(book, fallback, null);
		}

		return null;
	}

}

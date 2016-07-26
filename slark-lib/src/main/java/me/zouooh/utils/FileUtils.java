package me.zouooh.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public final class FileUtils {

	private FileUtils() {
	}
	
	public static String fomatSize(long size) {
		if (size < 1024) {
			return size + "B";
		}
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMaximumFractionDigits(2);
		if (size < 1024 * 1024) {
			return numberFormat.format((float) size / 1024) + "K";
		}
		return numberFormat.format((float) size / (1024 * 1024)) + "M";
	}

	public static int size(File base) {
		int temp = 0;
		if (base == null || !base.exists() || base.listFiles() == null) {
			return 0;
		}
		for (File file : base.listFiles()) {
			if (file.isDirectory()) {
				temp += size(file);
			} else {
				temp += file.length();
			}
		}
		return temp;
	}


	public static File createFile(File base, String name) {
		if (base == null)
			return null;
		if (!base.isDirectory()){
			return null;
		}

		File file = new File(base,name);
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		return file;
	}


	public static void clear(File base) {
		if (base == null || !base.exists()) {
			return;
		}
		File[] files = base.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				clear(file);
			}else {
				file.delete();
			}
		}
		base.delete();
	}
	

	public static String readStrFromFile(File file) {
		if (file == null || !file.exists() || file.isDirectory()) {
			return "";
		}
		try {
			return Streams.readAndClose(Streams.utf8r(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
		}
		return "";
	}
}

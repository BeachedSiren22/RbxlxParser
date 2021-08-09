package com.warven22.rbxlxparser.util;

import java.io.File;

/**
 * A static class with a collection of utility functions
 * relating to files.
 */
public class FileUtil {
	/**
	 * Gets the extension from a file.
	 * <br><b>ex:</b> <i>filename.txt</i> would return <i>txt</i>
	 * 
	 * @param file The file to get the extension of
	 * @return The extension past the period
	 */
	public static String getExtension(File file) {
		String filePath = file.getPath();
		return filePath.substring(filePath.lastIndexOf('.')+1);
	}
	
	/**
	 * Gets the name of the file, before the extension.
	 * <br><b>ex:</b> <i>filename.txt</i> would return <i>filename</i>
	 * 
	 * @param file The file to get the name of
	 * @return The name of the file without the extension
	 */
	public static String getNameWithoutExtension(File file) {
		String filePath = file.getPath();
		return filePath.substring(0, filePath.lastIndexOf('.'));
	}
	
	/**
	 * Takes a file and gets a variation of it that does not currently exist.
	 * <br>This is done by taking the file and, if it exists, a number is added
	 * to the path.
	 * <br> For example: if "file.txt" exists, it'll check if "file(1).txt" exists.
	 * It repeats this process until the file does not exist.
	 * 
	 * @param file The original file
	 * @return A non-existent file based on the given file. May return the given file if
	 * the given file does not exist.
	 */
	public static File getNonExistingFile(File file) {
		File workFile = file;
		int index = 1;
		if (workFile.exists()) {
			workFile = new File(String.format("%s/%s(%d).%s", workFile.getParent(), FileUtil.getNameWithoutExtension(workFile), index, FileUtil.getExtension(workFile)));
		}
		return workFile;
	}
}
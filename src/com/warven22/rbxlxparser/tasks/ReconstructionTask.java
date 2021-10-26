package com.warven22.rbxlxparser.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;

import com.warven22.rbxlxparser.element.ParentElement;
import com.warven22.rbxlxparser.roblox.RobloxItem;
import com.warven22.rbxlxparser.roblox.RobloxValue;
import com.warven22.rbxlxparser.util.FileUtil;

import me.tongfei.progressbar.ProgressBar;

/**
 * Represents a task for a {@link ProgressBar} to wrap.
 * <br>
 * This task involves going over a set of {@link File}-{@link ParentElement} pairs and
 * writing the {@link ParentElement} to its associated file.
 */
public class ReconstructionTask implements Iterator<File> {

	private File[] _allFiles;
	private int _curIndex = 0;
	
	public ReconstructionTask(File[] allFiles) {
		_allFiles = allFiles;
	}
	
	private Stack<File> 
	
	/**
	 * Returns true if there are more files that need writing to
	 */
	@Override
	public boolean hasNext() {
		return _curIndex < _allFiles.length;
	}

	/**
	 * gets the next File-ParentElement pair in line, writes the ParentElement to its file, and returns the File-ParentElement pair.
	 */
	@Override
	public File next() {
		
		
		_curIndex++;
		return null;
	}
	
}
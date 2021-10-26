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
public class FileWriterTask implements Iterator<Entry<File, Object>> {

	private LinkedList<Entry<File, Object>> _entries;
	
	private ProgressBar _progressBar;
	/**
	 * Sets the progress bar this task will update as it works
	 * 
	 * @param progressBar The {@link ProgressBar} to update
	 */
	public void setProgressBar(ProgressBar progressBar) {
		_progressBar = progressBar;
	}
	
	public FileWriterTask(Set<Entry<File, Object>> set) {
		_entries = new LinkedList<>();
		_entries.addAll(set);
	}
	
	/**
	 * Returns true if there are more files that need writing to
	 */
	@Override
	public boolean hasNext() {
		return !_entries.isEmpty();
	}

	/**
	 * gets the next File-ParentElement pair in line, writes the ParentElement to its file, and returns the File-ParentElement pair.
	 */
	@Override
	public Entry<File, Object> next() {
		Entry<File, Object> entry = _entries.pop();
		
		String targetFileExt = FileUtil.getExtension(entry.getKey());
		if (targetFileExt.equals("lua")) {

			RobloxValue codeValue = (RobloxValue)entry.getValue();

			entry.getKey().getParentFile().mkdirs();
			try (FileWriter fileWriter = new FileWriter(entry.getKey())) {
				fileWriter.write(codeValue.getValue());
				fileWriter.flush();
			} catch (IOException e) {
				_progressBar.close();
				System.err.printf("%nError while writing '%s': %s%n\t%s%n", entry.getKey().getPath(),
						e.getClass().getName(), e.getMessage());
				return null;
			}

		} else {

			RobloxItem item = (RobloxItem)entry.getValue();
			
			entry.getKey().getParentFile().mkdirs();
			try (FileWriter fileWriter = new FileWriter(entry.getKey())) {
				fileWriter.write(item.toPrettyString(0));
				fileWriter.flush();
			} catch (IOException e) {
				_progressBar.close();
				System.err.printf("%nError while writing '%s': %s%n\t%s%n", entry.getKey().getPath(),
						e.getClass().getName(), e.getMessage());
				return null;
			}

		}
		return entry;
	}
	
}
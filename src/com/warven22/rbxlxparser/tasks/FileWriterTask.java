package com.warven22.rbxlxparser.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;

import com.warven22.rbxlxparser.element.Element;
import com.warven22.rbxlxparser.element.ParentElement;
import com.warven22.rbxlxparser.element.ValueElement;
import com.warven22.rbxlxparser.util.FileUtil;

import me.tongfei.progressbar.ProgressBar;

/**
 * Represents a task for a {@link ProgressBar} to wrap.
 * <br>
 * This task involves going over a set of {@link File}-{@link ParentElement} pairs and
 * writing the {@link ParentElement} to its associated file.
 */
public class FileWriterTask implements Iterator<Entry<File, ParentElement>> {

	private LinkedList<Entry<File, ParentElement>> _entries;
	
	private ProgressBar _progressBar;
	/**
	 * Sets the progress bar this task will update as it works
	 * 
	 * @param progressBar The {@link ProgressBar} to update
	 */
	public void setProgressBar(ProgressBar progressBar) {
		_progressBar = progressBar;
	}
	
	public FileWriterTask(Set<Entry<File, ParentElement>> entrySet) {
		_entries = new LinkedList<>();
		_entries.addAll(entrySet);
	}

	/**
	 * Builds the text content for an item file in recursive form.
	 * <br>
	 * The resulting content for the supplied root will be within the given
	 * StringBuilder.
	 * 
	 * @param contentBuilder The StringBuilder which will be appended to while creating the text content
	 * @param tabString      A string representing a series of tabs for formatting child/parent relationships
	 * @param root           The root element to build the content for. The content will be built for every item within the root in a breadth-first fashion
	 */
	private static void getFileContent(StringBuilder contentBuilder, String tabString, ParentElement root) {
		if (!tabString.equals("")) {
			contentBuilder.append(String.format("%s%s%n", tabString,
					root.getAttribute("name") == null ? root.getName() : root.getAttribute("name")));
		}

		for (Element child : root.getChildren()) {
			if (child instanceof ValueElement) {

				ValueElement valueChild = (ValueElement) child;
				if (valueChild.getAttribute("name") != null && valueChild.getAttribute("name").equals("Source"))
					continue;
				contentBuilder.append(String.format("%s%s = %s%n", tabString,
						valueChild.getAttribute("name") == null ? valueChild.getName()
								: valueChild.getAttribute("name"),
						valueChild.getValue()));

			} else {

				ParentElement parentChild = (ParentElement) child;
				getFileContent(contentBuilder, tabString.concat("\t"), parentChild);

			}
		}
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
	public Entry<File, ParentElement> next() {
		Entry<File, ParentElement> entry = _entries.pop();
		
		ParentElement itemProperties = (ParentElement) entry.getValue().filterByElementName("Properties").get(0);

		String itemName = itemProperties.findValueElementByNameAndAttribute("string", "name", "Name").getValue();
		if (itemName == null) {
			itemName = entry.getValue().getName();
		}
		_progressBar.setExtraMessage(String.format("Writing '%s'", itemName, entry.getKey().getName()));
		
		String targetFileExt = FileUtil.getExtension(entry.getKey());
		if (targetFileExt.equals("lua")) {

			String code = itemProperties.findValueElementByNameAndAttribute("ProtectedString", "name", "Source")
					.getValue();

			entry.getKey().getParentFile().mkdirs();
			try (FileWriter fileWriter = new FileWriter(entry.getKey())) {
				fileWriter.write(code);
				fileWriter.flush();
			} catch (IOException e) {
				_progressBar.close();
				System.err.printf("%nError while writing '%s': %s%n\t%s%n", entry.getKey().getPath(),
						e.getClass().getName(), e.getMessage());
				return null;
			}

		} else {

			StringBuilder contentBuilder = new StringBuilder();
			getFileContent(contentBuilder, "", itemProperties);
			String content = contentBuilder.toString();

			entry.getKey().getParentFile().mkdirs();
			try (FileWriter fileWriter = new FileWriter(entry.getKey())) {
				fileWriter.write(content);
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
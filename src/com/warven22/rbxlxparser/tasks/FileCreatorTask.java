package com.warven22.rbxlxparser.tasks;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import com.warven22.rbxlxparser.element.Element;
import com.warven22.rbxlxparser.element.ParentElement;
import com.warven22.rbxlxparser.roblox.RobloxItem;
import com.warven22.rbxlxparser.roblox.RobloxValue;
import com.warven22.rbxlxparser.util.FileUtil;

import me.tongfei.progressbar.ProgressBar;

/**
 * Represents a task for a {@link ProgressBar} to wrap.
 * <br>
 * This task involves going over a set of {@link Element}s and
 * writing the {@link ParentElement} to its associated file.
 */
public class FileCreatorTask implements Iterator<RobloxItem> {

	private LinkedList<RobloxItem> _rootItems;
	private File _destFolder;
	private HashMap<File, Object> _fileToObject;
	/**
	 * @return The map that represents what {@link File} will take
	 * what {@link ParentElement}'s content
	 */
	public HashMap<File, Object> getFileToObjectMap() {
		return _fileToObject;
	}
	
	private ProgressBar _progressBar;
	/**
	 * Sets the progress bar this task will update as it works
	 * 
	 * @param progressBar The {@link ProgressBar} to update
	 */
	public void setProgressBar(ProgressBar progressBar) {
		this._progressBar = progressBar;
	}

	private HashMap<RobloxItem, File> _itemToFolder;
	private Stack<LinkedList<RobloxItem>> _childrenStack;
	private RobloxItem getNextChild() {
		if (_childrenStack.isEmpty()) return null;
		while(!_childrenStack.isEmpty()) {
			LinkedList<RobloxItem> curList = _childrenStack.peek();
			if (curList.isEmpty()) {
				_childrenStack.pop();
			} else {
				return curList.pop();
			}
		}
		return null;
	}
	
	public FileCreatorTask(File destFolder, LinkedList<RobloxItem> robloxItems) {
		_fileToObject = new HashMap<>();
		_itemToFolder = new HashMap<>();
		_childrenStack = new Stack<>();
		_rootItems = robloxItems;
		_destFolder = destFolder;
		_rootItems.forEach(item -> {
			_itemToFolder.put(item, _destFolder);
		});
	}
	
	/**
	 * Returns true if there are more files that need creating
	 */
	@Override
	public boolean hasNext() {
		return !_rootItems.isEmpty() || !_childrenStack.isEmpty();
	}

	/**
	 * gets the next {@link Element} in line, processes it, and handles its children
	 */
	@Override
	public RobloxItem next() {
		RobloxItem item = getNextChild();
		
		if (item == null) {
			item = _rootItems.pop();
		}
		
		if (!checkParent(item)) {
			process(item);
		}
		
		return item;
	}
	
	/**
	 * Processes a single {@link Element} (expected to be of type Item).
	 * <br>
	 * This includes:
	 * <ul>
	 * <li>Creating a file for the contents
	 * <li>Creating a second lua file if the element contains code
	 * <li>Adding both files to the File-ParentElement map
	 * </ul>
	 * 
	 * @param itemElement The element to process
	 */
	private void process(RobloxItem item) {
		String itemName = (item.getItemName() == null) ? item.getClassName() : item.getItemName();
		
		File targetFolder = _itemToFolder.get(item);
		
		if (item.getChildren().size() > 0) {
			File fileForItem = createNonExistingFilePath(new File(String.format("%s/%s.%s.Parent", targetFolder.getPath(), itemName, item.getClassName())));
			_fileToObject.put(fileForItem, item);
		} else {
			File fileForItem = createNonExistingFilePath(new File(String.format("%s/%s.%s", targetFolder.getPath(), itemName, item.getClassName())));
			_fileToObject.put(fileForItem, item);
		}
		
		RobloxValue sourceValue = item.getCodeValue();
		if (sourceValue != null) {
			File fileForCode = createNonExistingFilePath(new File(String.format("%s/%s.lua", targetFolder.getPath(), itemName)));
			_fileToObject.put(fileForCode, sourceValue);
			_progressBar.step();
		}
	}
	
	/**
	 * Checks if the given element is a parent.
	 * <br>
	 * This also includes, if the element does, in fact, have children:
	 * <ol>
	 * <li> Updating the current folder
	 * <li> Processing the element
	 * <li> Adding the element's children to the stack
	 * </ol>
	 * 
	 * @param itemElement The element to check
	 * @return Whether the element is a parent or not
	 */
	private boolean checkParent(RobloxItem item) {
		if (item.getChildren().size() > 0) {
			String itemName = (item.getItemName() == null) ? item.getClassName() : item.getItemName();
			
			File oldFolder = _itemToFolder.get(item);
			File newFolder = new File(String.format("%s/%s", oldFolder.getPath(), itemName));

			item.getChildren().forEach(child -> {
				_itemToFolder.put(child, newFolder);
			});
			_itemToFolder.put(item, newFolder);
			
			process(item);
			_childrenStack.push(item.getChildren());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Takes a file and gets a variation of it that does not currently exist in the File-ParentElement map.
	 * <br>This is done by taking the file and, if it exists in the File-ParentElement map, a number is added
	 * to the path.
	 * <br> For example: if "file.txt" exists in the File-ParentElement map, it'll check if "file(1).txt" exists in the File-ParentElement map.
	 * It repeats this process until the file does not exist in the File-ParentElement map.
	 * 
	 * @param file The original file
	 * @return A non-existent file path based on the given file. May return the given file if
	 * the given file does not exist in the File-ParentElement map
	 */
	private File createNonExistingFilePath(File file) {
		File workFile = file;
		int index = 1;
		while (_fileToObject.containsKey(workFile)) {
			workFile = new File(String.format("%s/%s(%d).%s", file.getParent(), FileUtil.getNameWithoutExtension(file), index++, FileUtil.getExtension(file)));
		}
		return workFile;
	}
}
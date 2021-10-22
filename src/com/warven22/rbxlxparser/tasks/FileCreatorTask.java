package com.warven22.rbxlxparser.tasks;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import com.warven22.rbxlxparser.element.Element;
import com.warven22.rbxlxparser.element.ParentElement;
import com.warven22.rbxlxparser.element.ValueElement;
import com.warven22.rbxlxparser.util.FileUtil;
import com.warven22.rbxlxparser.util.ItemUtil;

import me.tongfei.progressbar.ProgressBar;

/**
 * Represents a task for a {@link ProgressBar} to wrap.
 * <br>
 * This task involves going over a set of {@link Element}s and
 * writing the {@link ParentElement} to its associated file.
 */
public class FileCreatorTask implements Iterator<Element> {

	private LinkedList<Element> _rootItemElements;
	
	private HashMap<File, ParentElement> _fileToItem;
	/**
	 * @return The map that represents what {@link File} will take
	 * what {@link ParentElement}'s content
	 */
	public HashMap<File, ParentElement> getFileToItemMap() {
		return _fileToItem;
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
	
	private Stack<LinkedList<Element>> _childrenStack;
	private Stack<File> _folderStack;
	
	public FileCreatorTask(File destFolder, LinkedList<Element> itemElements) {
		_fileToItem = new HashMap<>();
		_childrenStack = new Stack<>();
		_folderStack = new Stack<>();
		_folderStack.push(destFolder);
		_rootItemElements = itemElements;
	}
	
	/**
	 * Returns true if there are more files that need creating
	 */
	@Override
	public boolean hasNext() {
		return !_rootItemElements.isEmpty();
	}

	/**
	 * gets the next {@link Element} in line, processes it, and handles its children
	 */
	@Override
	public Element next() {
		Element nextElement = null;
		
		if (_childrenStack.isEmpty()) {
			nextElement = _rootItemElements.pop();
		} else {
			nextElement = _childrenStack.peek().pop();
			if (_childrenStack.peek().isEmpty()) {
				// No more children, all done with this one
				_folderStack.pop();
				_childrenStack.pop();
			}
		}
		
		if (!checkParent(nextElement)) {
			// No children, process individually
			process(nextElement);
		}
			
		return null;
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
	private void process(Element itemElement) {
		ParentElement parentElement = (ParentElement)itemElement;
		
		String itemName = getParentElementName((ParentElement)itemElement);
		
		_progressBar.setExtraMessage(String.format("Creating File For '%s'", itemName));

		String className = itemElement.getAttribute("class");
		File fileForItem = createNonExistingFilePath(new File(String.format("%s/%s.%s", _folderStack.peek().getPath(), itemName, className)));
		_fileToItem.put(fileForItem, parentElement);

		ParentElement itemProperties = (ParentElement) parentElement.filterByElementName("Properties").get(0);
		ValueElement sourceElement = itemProperties.findValueElementByNameAndAttribute("ProtectedString", "name","Source");
		if (sourceElement != null) {
			File fileForCode = createNonExistingFilePath(new File(String.format("%s/%s.lua", _folderStack.peek().getPath(), itemName)));
			_fileToItem.put(fileForCode, parentElement);
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
	private boolean checkParent(Element itemElement) {
		if (!ItemUtil.getChildListWithoutProperties(itemElement).isEmpty()) {
			String itemName = getParentElementName((ParentElement)itemElement);
			File newFolder = new File(String.format("%s/%s", _folderStack.peek().getPath(), itemName));
			_folderStack.push(newFolder);
			process(itemElement);
			_childrenStack.push(((ParentElement)itemElement).filterByElementName("Item"));
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param element The {@link ParentElement} to get the name of
	 * @return The name of the {@link ParentElement} from the
	 * name property
	 */
	private String getParentElementName(ParentElement element) {
		ParentElement itemProperties = (ParentElement)element.filterByElementName("Properties").get(0);
		return itemProperties.findValueElementByNameAndAttribute("string", "name", "Name").getValue();
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
		while (_fileToItem.containsKey(workFile)) {
			workFile = new File(String.format("%s/%s(%d).%s", file.getParent(), FileUtil.getNameWithoutExtension(file), index++, FileUtil.getExtension(file)));
		}
		return workFile;
	}
}
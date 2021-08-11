package com.warven22.rbxlxparser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.warven22.rbxlxparser.element.Element;
import com.warven22.rbxlxparser.element.ParentElement;
import com.warven22.rbxlxparser.element.ValueElement;
import com.warven22.rbxlxparser.util.FileUtil;
import com.warven22.rbxlxparser.util.ProgressBar;
import com.warven22.rbxlxparser.util.RbxlxHandler;

public class RbxlxParserMain {
	public static void main(String[] args) {
		
		// Incorrect args
		if (args.length != 2 && args.length != 1) {
			System.err.println("USAGE: RbxlxParser <Source> <Destination> OR RbxlxParser help");
			return;
		}

		// Manual
		if (args[0].equalsIgnoreCase("help")) {
			System.out.println("--RbxlxParser v1.1.0 Help--\n" + "USAGE: RbxlxParser <Source> <Destination>\n"
					+ "Source: Must be .rbxlx file\n" + "Destination: Must be an empty or non-existing folder\n"
					+ "The Roblox items will be extracted into their own files into the destination folder\n"
					+ "----------------------");
			return;
		}

		// Checking source file
		File rbxlxFile = new File(args[0].replace("\"", ""));

		if (!rbxlxFile.exists()) {
			System.err.printf("The source file '%s' does not exist%n", rbxlxFile.getPath());
			return;
		}
		if (!FileUtil.getExtension(rbxlxFile).equals("rbxlx")) {
			System.err.printf("The source file '%s' must be an rbxlx file%n", rbxlxFile.getPath());
			return;
		}

		// Checking destination folder
		File destFolder = new File(args[1].replace("\"", ""));

		if (destFolder.exists() && destFolder.listFiles().length > 0) {
			System.err.printf("The destination folder '%s' must either not exist or be empty%n", destFolder.getPath());
			return;
		}

		// Parsing .rbxlx
		RbxlxHandler handler = new RbxlxHandler();

		System.out.printf("Parsing '%s'...%n", rbxlxFile.getName());
		
		SAXParser parser;
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(rbxlxFile, handler);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			System.err.printf("Error while parsing '%s': %s%n%s%n", rbxlxFile.getPath(), e.getClass().getName(),
					e.getMessage());
			return;
		}

		System.out.println("Determining files needed...");
		
		// Creating files for every item
		LinkedList<Element> items = handler.getRoot().filterByElementName("Item");
		HashMap<File, ParentElement> fileToItem = new HashMap<>();
		for (Element itemElement : items) {
			createFilesForItem(destFolder, (ParentElement) itemElement, fileToItem);
		}

		ProgressBar progressBar = new ProgressBar();
		int work = 0;
		int totalWork = fileToItem.size();
		// Writing content to files
		for (Entry<File, ParentElement> entry : fileToItem.entrySet()) {
			ParentElement itemProperties = (ParentElement) entry.getValue().filterByElementName("Properties").get(0);

			String itemName = itemProperties.findValueElementByNameAndAttribute("string", "name", "Name").getValue();
			if (itemName == null) {
				itemName = entry.getValue().getName();
			}
			progressBar.update(work, totalWork, String.format("Writing '%s' to '%s'", itemName, entry.getKey().getName()));
			
			String targetFileExt = FileUtil.getExtension(entry.getKey());
			if (targetFileExt.equals("lua")) {

				String code = itemProperties.findValueElementByNameAndAttribute("ProtectedString", "name", "Source")
						.getValue();

				entry.getKey().getParentFile().mkdirs();
				try (FileWriter fileWriter = new FileWriter(FileUtil.getNonExistingFile(entry.getKey()))) {
					fileWriter.write(code);
					fileWriter.flush();
				} catch (IOException e) {
					progressBar.stop();
					System.err.printf("Error while writing '%s': %s%n\t%s%n", entry.getKey().getPath(),
							e.getClass().getName(), e.getMessage());
					return;
				}

			} else {

				StringBuilder contentBuilder = new StringBuilder();
				getFileContent(contentBuilder, "", itemProperties);
				String content = contentBuilder.toString();

				entry.getKey().getParentFile().mkdirs();
				try (FileWriter fileWriter = new FileWriter(FileUtil.getNonExistingFile(entry.getKey()))) {
					fileWriter.write(content);
					fileWriter.flush();
				} catch (IOException e) {
					progressBar.stop();
					System.err.printf("Error while writing '%s': %s%n\t%s%n", entry.getKey().getPath(),
							e.getClass().getName(), e.getMessage());
					return;
				}

			}
			
			work++;
		}
		progressBar.update(work, totalWork);
		progressBar.stop();
		
		System.out.printf("Parsing of '%s' complete.%n", rbxlxFile.getName());
		System.out.printf("Parsed files can be found in '%s'.%n", destFolder.getAbsolutePath());
	}

	/**
	 * Builds the text content for an item file in recursive form. <br>
	 * The resulting content for the supplied root will be within the given
	 * StringBuilder.
	 * 
	 * @param contentBuilder The StringBuilder which will be appended to while
	 *                       creating the text content
	 * @param tabString      A string representing a series of tabs for formatting
	 *                       child/parent relationships
	 * @param root           The root element to build the content for. The content
	 *                       will be built for every item within the root in a
	 *                       breadth-first fashion
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
	 * Create file objects for the given item and all of it's children (with the
	 * element name "Item"). <br>
	 * Results are stored in the given map.
	 * 
	 * @param folder     The root folder the files should be within
	 * @param item       The item to create the files for
	 * @param fileToItem The map of which results will be put into
	 */
	private static void createFilesForItem(File folder, ParentElement item, HashMap<File, ParentElement> fileToItem) {
		// Create for self
		ParentElement itemProperties = (ParentElement) item.filterByElementName("Properties").get(0);
		String itemName = itemProperties.findValueElementByNameAndAttribute("string", "name", "Name").getValue();
		File trueFolder = (item.getChildren().size() == 1) ? folder
				: (new File(String.format("%s/%s", folder.getPath(), itemName)));

		String className = item.getAttribute("class");
		File fileForItem = new File(String.format("%s/%s.%s", trueFolder.getPath(), itemName, className));
		fileToItem.put(fileForItem, item);

		ValueElement sourceElement = itemProperties.findValueElementByNameAndAttribute("ProtectedString", "name",
				"Source");
		if (sourceElement != null) {
			File fileForCode = new File(String.format("%s/%s.lua", trueFolder.getPath(), itemName));
			fileToItem.put(fileForCode, item);
		}

		// Create for child items
		LinkedList<Element> itemElements = item.filterByElementName("Item");
		for (Element itemElement : itemElements) {
			createFilesForItem(trueFolder, (ParentElement) itemElement, fileToItem);
		}
	}
}
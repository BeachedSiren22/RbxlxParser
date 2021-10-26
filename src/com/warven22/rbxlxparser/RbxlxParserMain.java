package com.warven22.rbxlxparser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.warven22.rbxlxparser.element.ParentElement;
import com.warven22.rbxlxparser.roblox.RobloxItem;
import com.warven22.rbxlxparser.tasks.FileCreatorTask;
import com.warven22.rbxlxparser.tasks.FileWriterTask;
import com.warven22.rbxlxparser.util.FileUtil;
import com.warven22.rbxlxparser.util.RbxlxHandler;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import me.tongfei.progressbar.wrapped.ProgressBarWrappedIterator;

/**
 * The RbxlxParser parses a .rbxlx file into individual files
 * for every item, including an additional file for code based items with the
 * extension '.lua'
 * 
 * @author Warven22
 */
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
		
		System.out.print("Parsing XML...");
		
		RbxlxHandler handler = new RbxlxHandler();
		SAXParser parser;
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(rbxlxFile, handler);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			System.out.println("Error.");
			System.out.flush();
			System.err.printf("Error while parsing '%s': %s%n%s%n", rbxlxFile.getPath(), e.getClass().getName(),
					e.getMessage());
			return;
		}
		
		// Creating files for every item
		LinkedList<RobloxItem> robloxItems = new LinkedList<>();
		handler.getRoot().filterByElementName("Item").forEach(itemElement -> {
			robloxItems.add(RobloxItem.fromParentElement((ParentElement)itemElement));
		});
		
		System.out.println("Done.");
		
		FileCreatorTask fileCreatorTask = new FileCreatorTask(destFolder, robloxItems);
		
		ProgressBarWrappedIterator<RobloxItem> creatorProgressBar = (ProgressBarWrappedIterator<RobloxItem>)ProgressBar.wrap(
			fileCreatorTask,
			new ProgressBarBuilder().setTaskName("Creating Files").setStyle(ProgressBarStyle.ASCII).setInitialMax(robloxItems.size()).setUpdateIntervalMillis(10).setMaxRenderedLength(119)
		);
		
		fileCreatorTask.setProgressBar(creatorProgressBar.getProgressBar());
		
		while (creatorProgressBar.hasNext()) {
			creatorProgressBar.next();
		}
		
		FileWriterTask fileWriterTask = new FileWriterTask(fileCreatorTask.getFileToObjectMap().entrySet());
		
		int totalFiles = fileCreatorTask.getFileToObjectMap().entrySet().size();
		ProgressBarWrappedIterator<Entry<File, Object>> writerProgressBar = (ProgressBarWrappedIterator<Entry<File, Object>>)ProgressBar.wrap(
			fileWriterTask,
			new ProgressBarBuilder().setTaskName("Writing Files").setStyle(ProgressBarStyle.ASCII).setInitialMax(totalFiles).setUpdateIntervalMillis(10).setMaxRenderedLength(119)
		);

		fileWriterTask.setProgressBar(writerProgressBar.getProgressBar());
		
		while (writerProgressBar.hasNext()) {
			writerProgressBar.next();
		}
		
		System.out.printf("Parsing of '%s' complete.%n", rbxlxFile.getName());
	}
}
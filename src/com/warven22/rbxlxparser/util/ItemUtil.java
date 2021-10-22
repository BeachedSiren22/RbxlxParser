package com.warven22.rbxlxparser.util;

import java.util.LinkedList;

import com.warven22.rbxlxparser.element.Element;

/**
 * A utility class with static functions to assist
 * in processing Item elements
 */
public class ItemUtil {

	/**
	 * @param element The element to get the children of
	 * @return The list of children of the given element, sans
	 * Property element if it exists within the list of children.
	 */
	public static LinkedList<Element> getChildListWithoutProperties(Element element) {
		LinkedList<Element> initialElements = element.getChildren();
		LinkedList<Element> workElements = new LinkedList<>();
		workElements.addAll(initialElements);
		initialElements.forEach(e -> {
			if (e.getName().equals("Properties")) {
				workElements.remove(e);
			}
		});
		return workElements;
	}
}

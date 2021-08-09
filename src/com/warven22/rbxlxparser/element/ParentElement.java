package com.warven22.rbxlxparser.element;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Represents an {@link com.warven22.rbxlxparser.element.Element Element}
 * that has children, but no value.
 */
public class ParentElement extends Element {
	
	/**
	 * Finds the {@link com.warven22.rbxlxparser.element.Element ValueElement}
	 * that has the given name and given attribute with the given value
	 * 
	 * @param elementName The name of the element to find
	 * @param attributeName The name of the attribute to find
	 * @param attributeValue The expected value of the attribute being searched for
	 * @return The relevant {@link com.warven22.rbxlxparser.element.Element ValueElement}, or null if nothing was found
	 */
	public ValueElement findValueElementByNameAndAttribute(String elementName, String attributeName, String attributeValue) {
		for (Element child : getChildren()) {
			if (!(child instanceof ValueElement)) continue;
			ValueElement valueChild = (ValueElement) child;
			if (!valueChild.getName().equals(elementName)) continue;
			String desiredAttribute = valueChild.getAttribute(attributeName);
			if (desiredAttribute == null) continue;
			if (!desiredAttribute.equals(attributeValue)) continue;
			return valueChild;
		}
		return null;
	}
	
	/**
	 * Filters the children of this element based on the given name.
	 * <br>Any child with the same name as the given name will pass through
	 * the filter.
	 * 
	 * @param elementName The name of the element to filter
	 * @return A list of child elements that passed through the filter
	 */
	public LinkedList<Element> filterByElementName(String elementName) {
		LinkedList<Element> caughtElements = new LinkedList<>();
		
		for (Element child : getChildren()) {
			if (!child.getName().equals(elementName)) continue;
			caughtElements.add(child);
		}
		
		return caughtElements;
	}
	
	public ParentElement(String name, HashMap<String, String> attributes) {
		super(name, attributes);
	}
	
	@Override
	public String toString() {
		return String.format("%s %s", this.getName(), getChildren().toString());
	}
}
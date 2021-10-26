package com.warven22.rbxlxparser.element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

/**
 * Represents an Element of XML with attributes,
 * a name, and possibly child elements.
 */
public abstract class Element {
	
	public abstract String toXML();
	
	private String _name;
	/**
	 * @return The name of this element
	 */
	public String getName() {
		return _name;
	}
	
	private HashMap<String, String> _attributes;
	/**
	 * Gets the value of an attribute with a given name,
	 * or null if this element does not have an attribute
	 * with the given name.
	 * @param attribute The attribute name to get the value of
	 * @return The value of the attribute
	 */
	public String getAttribute(String attribute) {
		return _attributes.get(attribute);
	}
	public boolean hasAttribute(String attributeName) {
		return _attributes.containsKey(attributeName);
	}
	public Iterator<Entry<String, String>> entryIterator() {
		return _attributes.entrySet().iterator();
	}
	public int getAttributeCount() {
		return _attributes.size();
	}
	
	private LinkedList<Element> _children;
	/**
	 * @return all children of this element, contained
	 * within the opening & closing tags.
	 */
	public LinkedList<Element> getChildren() {
		return _children;
	}
	
	public Element(String name, HashMap<String, String> attributes) {
		_name = name;
		_attributes = attributes;
		_children = new LinkedList<>();
	}
}
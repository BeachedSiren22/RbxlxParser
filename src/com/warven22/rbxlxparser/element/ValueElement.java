package com.warven22.rbxlxparser.element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Represents an {@link Element}
 * that has a value, but no children.
 */
public class ValueElement extends Element {
	
	private String _value;
	/**
	 * @return The text value of this ValueElement
	 */
	public String getValue() {
		return _value;
	}
	
	public ValueElement(String name, HashMap<String, String> attributes, String value) {
		super(name, attributes);
		_value = value;
	}
	
	public String toXML() {
		StringBuilder sb = new StringBuilder(" ");
		Iterator<Entry<String, String>> attrIter = entryIterator();
		while(attrIter.hasNext()) {
			Entry<String, String> attr = attrIter.next();
			if (!attrIter.hasNext()) {
				sb.append(String.format("%s=\"%s\"", attr.getKey(), attr.getValue()));
			} else {
				sb.append(String.format("%s=\"%s\" ", attr.getKey(), attr.getValue()));
			}
		}
		if (sb.length() == 1) sb.deleteCharAt(0);
		return String.format("<%s%s>%s</%s>", this.getName(), sb.toString(), _value, this.getName());
	}
	
	@Override
	public String toString() {
		String valueToPrint = _value;
		if (valueToPrint.length() > 50) {
			valueToPrint = valueToPrint.substring(0, 50) + "...";
		}
		return String.format("%s = %s", this.getName(), valueToPrint.trim());
	}
}
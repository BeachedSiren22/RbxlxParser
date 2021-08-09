package com.warven22.rbxlxparser.element;

import java.util.HashMap;

/**
 * Represents an {@link com.warven22.rbxlxparser.element.Element Element}
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
	
	@Override
	public String toString() {
		String valueToPrint = _value;
		if (valueToPrint.length() > 50) {
			valueToPrint = valueToPrint.substring(0, 50) + "...";
		}
		return String.format("%s = %s", this.getName(), valueToPrint.trim());
	}
}
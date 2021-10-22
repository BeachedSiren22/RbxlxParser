package com.warven22.rbxlxparser.util;

import java.util.HashMap;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.warven22.rbxlxparser.element.ParentElement;
import com.warven22.rbxlxparser.element.ValueElement;

/**
 * The SAX XML parsing handler for RBXLX files
 */
public class RbxlxHandler extends DefaultHandler {
	private ParentElement _root;
	/**
	 * @return The root {@link com.warven22.rbxlxparser.element.Element ParentElement} that all
	 * other elements sit within
	 */
	public ParentElement getRoot() {
		return _root;
	}
	
	private StringBuilder _currentValue = new StringBuilder();
	private NameAttrPair _lastNameAttrPair;
	private Stack<ParentElement> _parentStack = new Stack<>();
	
	/**
	 * Represents an XML element's name & attributes for an open tag
	 */
	private class NameAttrPair {
		public String qName;
		public HashMap<String, String> attributes;
		public NameAttrPair(String qName, Attributes attributes) {
			this.qName = qName;
			this.attributes = new HashMap<>();
			for (int i = 0; i < attributes.getLength(); i++) {
				this.attributes.put(attributes.getQName(i), attributes.getValue(i));
			}
		}
	}
	
	@Override
	public void startElement(
			String uri,
			String localName,
			String qName,
			Attributes attributes)
	{	
		_currentValue.setLength(0);
	  
		if (_lastNameAttrPair != null) {
			ParentElement newParent = new ParentElement(_lastNameAttrPair.qName, _lastNameAttrPair.attributes);
			if (!_parentStack.isEmpty()) {
				_parentStack.peek().getChildren().add(newParent);
			}
			_parentStack.push(newParent);
		}
		
		_lastNameAttrPair = new NameAttrPair(qName, attributes);
	}

	@Override
	public void endElement(
			String uri,
			String localName,
			String qName)
	{	
		if (qName.equals(_parentStack.peek().getName())) {
			ParentElement popped = _parentStack.pop();
			if (_parentStack.isEmpty()) {
				_root = popped;
			}
		} else {
			_parentStack.peek().getChildren().add(new ValueElement(_lastNameAttrPair.qName, _lastNameAttrPair.attributes, _currentValue.toString().trim()));
			_lastNameAttrPair = null;
		}
	}

	@Override
	public void characters(char ch[], int start, int length) {
		// The characters() method can be called multiple times for a single text node.
		_currentValue.append(ch, start, length);
	}
	  
}
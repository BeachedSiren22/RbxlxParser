package com.warven22.rbxlxparser.roblox;

import java.util.LinkedList;

import com.warven22.rbxlxparser.element.ParentElement;
import com.warven22.rbxlxparser.element.ValueElement;
import com.warven22.rbxlxparser.util.StringUtil;

public class RobloxItem {
	private String _className;
	public String getClassName() {
		return _className;
	}
	
	private String _referent;
	public String getReferent() {
		return _referent;
	}
	
	private String _itemName;
	public String getItemName() { return _itemName; }
	
	private LinkedList<RobloxBase> _properties;
	public LinkedList<RobloxBase> getProperties() {
		return _properties;
	}
	public RobloxValue getValueProperty(String type, String name) {
		for (RobloxBase property : _properties) {
			if (property instanceof RobloxValue) {
				RobloxValue propertyValue = (RobloxValue)property;
				if (propertyValue.getName().equals(name) && propertyValue.getValueType().equals(type)) {
					return propertyValue;
				}
			}
		}
		return null;
	}
	
	public RobloxValue getCodeValue() {
		for (RobloxBase property : _properties) {
			if (property instanceof RobloxValue && ((RobloxValue)property).isCode()) {
				return (RobloxValue)property;
			}
		}
		return null;
	}
	
	private LinkedList<RobloxItem> _children;
	public LinkedList<RobloxItem> getChildren() {
		return _children;
	}
	
	public RobloxItem(String itemName, String className, String referent) {
		_itemName = itemName;
		_className = className;
		_referent = referent;
		_properties = new LinkedList<>();
		_children = new LinkedList<>();
	}
	
	public static RobloxItem fromParentElement(ParentElement element) {
		ParentElement itemParentElement = (ParentElement)element;
		
		String className = itemParentElement.getAttribute("class");
		String referent = itemParentElement.getAttribute("referent");

		RobloxItem newRobloxItem = new RobloxItem("", className, referent);
		
		ParentElement itemProperties = (ParentElement) itemParentElement.filterByElementName("Properties").get(0);
		
		itemProperties.getChildren().forEach(propertyElement -> {
			if (propertyElement instanceof ValueElement) {
				ValueElement propertyValue = (ValueElement)propertyElement;
				if (propertyValue.getName().equals("string") && propertyValue.hasAttribute("name") && propertyValue.getAttribute("name").equals("Name")) {
					newRobloxItem._itemName = propertyValue.getValue();
				}
				newRobloxItem.getProperties().add(
					RobloxValue.fromValueElement(propertyValue)
				);
			} else if (propertyElement instanceof ParentElement) {
				ParentElement propertyParent = (ParentElement)propertyElement;
				newRobloxItem.getProperties().add(
					RobloxMultiValue.fromParentElement(propertyParent)
				);
			}
		});
		
		itemParentElement.getChildren().forEach(child -> {
			if (!child.getName().equals("Properties")) {
				newRobloxItem.getChildren().add(
					RobloxItem.fromParentElement((ParentElement)child)
				);
			}
		});
		
		return newRobloxItem;
	}
	
	public String toXML(int tabs) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("<Item class=\"%s\" referent=\"%s\">", _className, _referent));
		
		sb.append(StringUtil.createTabString(tabs+1) + "<Properties>");
		
		_properties.forEach(robloxBase -> {
			sb.append(robloxBase.toXML(tabs+2));
		});
		
		sb.append(StringUtil.createTabString(tabs+1) + "</Properties>");
		
		_children.forEach(childItem -> {
			sb.append(childItem.toXML(tabs+1));
		});
		
		sb.append(String.format("</Item>", _className, _referent));
		return sb.toString();
	}
	
	public String toPrettyString(int tabs) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("referent = %s%n%n",_referent));
		
		_properties.forEach(robloxBase -> {
			sb.append(robloxBase.toPrettyString(tabs) + "\n");
		});
		
		if (_properties.size() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return _itemName == null ? _className : _itemName;
	}
}
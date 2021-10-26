package com.warven22.rbxlxparser.roblox;

import java.util.LinkedList;

import com.warven22.rbxlxparser.element.ParentElement;
import com.warven22.rbxlxparser.element.ValueElement;
import com.warven22.rbxlxparser.util.StringUtil;

public class RobloxMultiValue extends RobloxBase {
	private String _valueType;
	public String getValueType() { return _valueType; }
	private String _name;
	public String getName() { return _name; }
	
	private LinkedList<RobloxBase> _children;
	public LinkedList<RobloxBase> getChildren() {
		return _children;
	}
	
	public RobloxMultiValue(String name, String valueType) {
		_name = name;
		_valueType = valueType;
		_children = new LinkedList<>();
	}
	
	public static RobloxMultiValue fromParentElement(ParentElement element) {
		ParentElement parentElement = (ParentElement)element;
		String name = parentElement.getAttribute("name");
		RobloxMultiValue multiValue = new RobloxMultiValue(name, parentElement.getName());
		parentElement.getChildren().forEach(childElement -> {
			if (childElement instanceof ValueElement) {
				multiValue.getChildren().add(RobloxValue.fromValueElement((ValueElement)childElement));
			} else {
				multiValue.getChildren().add(RobloxMultiValue.fromParentElement((ParentElement)childElement));
			}
		});
		return multiValue;
	}
	
	@Override
	public String toXML(int tabs) {
		StringBuilder sb = new StringBuilder();
		
		if (_name == null) {
			sb.append(String.format("%s<%s>%n",
					StringUtil.createTabString(tabs),
					_valueType));
		} else {
			sb.append(String.format("%s<%s name=\"%s\">%n",
					StringUtil.createTabString(tabs),
					_valueType,
					_name));
		}
		
		_children.forEach(robloxBase -> {
			sb.append(robloxBase.toXML(tabs+1) + "\n");
		});
		
		sb.append(String.format("%s</%s>",
				StringUtil.createTabString(tabs),
				_valueType));
		return sb.toString();
	}

	@Override
	public String toPrettyString(int tabs) {
		StringBuilder sb = new StringBuilder();
		
		if (_name == null) {
			sb.append(String.format("%s%s:%n",
					StringUtil.createTabString(tabs),
					_valueType));
		} else {
			sb.append(String.format("%s[%s] %s:%n",
					StringUtil.createTabString(tabs),
					_valueType,
					_name));
		}
		
		_children.forEach(robloxBase -> {
			sb.append(robloxBase.toPrettyString(tabs+1) + "\n");
		});
		
		sb.deleteCharAt(sb.length()-1);
		
		return sb.toString();
	}
}
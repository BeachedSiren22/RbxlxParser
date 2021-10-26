package com.warven22.rbxlxparser.roblox;

import com.warven22.rbxlxparser.element.ValueElement;
import com.warven22.rbxlxparser.util.StringUtil;

public class RobloxValue extends RobloxBase {
	private String _valueType;
	public String getValueType() { return _valueType; }
	private String _value;
	public String getValue() { return _value; }
	private String _name;
	public String getName() { return _name; }
	
	public boolean isCode() {
		//[ProtectedString] Source
		return "ProtectedString".equals(_valueType) && "Source".equals(_name);
	}
	
	public RobloxValue(String name, String valueType, String value) {
		_name = name;
		_valueType = valueType;
		_value = value;
	}
	
	public static RobloxValue fromValueElement(ValueElement element) {
		ValueElement valueElement = (ValueElement)element;
		String name = valueElement.getAttribute("name");
		return new RobloxValue(name, valueElement.getName(), valueElement.getValue());
	}
	
	@Override
	public String toXML(int tabs) {
		if (_name == null) {
			return String.format("%s<%s>%s</%s>",
					StringUtil.createTabString(tabs),
					_valueType,
					_value,
					_valueType);
		} else {
			return String.format("%s<%s name=\"%s\">%s</%s>",
					StringUtil.createTabString(tabs),
					_valueType,
					_name,
					_value,
					_valueType);
		}
	}

	@Override
	public String toPrettyString(int tabs) {
		if (_name == null) {
			if (isCode()) {
				return String.format("%s%s = <CODE>",
						StringUtil.createTabString(tabs),
						_valueType);
			} else {
				return String.format("%s%s = %s",
						StringUtil.createTabString(tabs),
						_valueType,
						_value);
			}
		} else {
			if (isCode()) {
				return String.format("%s[%s] %s = <CODE>",
						StringUtil.createTabString(tabs),
						_valueType,
						_name,
						_value);
			} else {
				return String.format("%s[%s] %s = %s",
						StringUtil.createTabString(tabs),
						_valueType,
						_name,
						_value);
			}
		}
	}
}
package com.warven22.rbxlxparser.util;

public class StringUtil {
	public static String createTabString(int tabCount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tabCount; i++) {
			sb.append("\t");
		}
		return sb.toString();
	}
}
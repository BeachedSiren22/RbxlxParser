package com.warven22.rbxlxparser.util;

import java.util.Arrays;

/**
 * A console based progress bar
 */
public class ProgressBar {
	private String full;
	private String empty;
	private int _charactersLastPrinted;
	private final int LENGTH = 20;
	
	public ProgressBar() {
		char[] arrayForFull = new char[LENGTH];
		Arrays.fill(arrayForFull, '#');
		char[] arrayForEmpty = new char[LENGTH];
		Arrays.fill(arrayForEmpty, ' ');
		full = new String(arrayForFull);
		empty = new String(arrayForEmpty);
		
		_charactersLastPrinted = 0;
	}

	/**
	 * Update this ProgressBar, causing it to print.
	 * 
	 * @param currentWork The current amount of work that's been done
	 * @param totalWork The total amount of work to be done
	 */
	public void update(int currentWork, int totalWork) {
		update(currentWork, totalWork, null);
	}
	
	/**
	 * Update this ProgressBar, causing it to print.
	 * 
	 * @param currentWork The current amount of work that's been done
	 * @param totalWork The total amount of work to be done
	 * @param currentAction What work is currently happening
	 */
	public void update(int currentWork, int totalWork, String currentAction) {
		if (_charactersLastPrinted > 0) {
			char[] charArray = new char[_charactersLastPrinted];
			Arrays.fill(charArray, ' ');
			System.out.print("\r" + new String(charArray));
		}

		String stringToPrint = String.format("[%s] (%s/%s) %s", constructProgressString(currentWork, totalWork), currentWork, totalWork, currentAction == null ? "":currentAction);
		_charactersLastPrinted = stringToPrint.length();
		System.out.print("\r" + stringToPrint);
	}
	
	/**
	 * Constructs the progress string to display. This string will
	 * be a combination of empty space and visible characters to
	 * represent total progress.
	 * 
	 * @param currentWork The current amount of work that's been done
	 * @param totalWork The total amount of work to be done
	 * @return A string which represents the progress
	 */
	private String constructProgressString(int currentWork, int totalWork) {
		double progress = (double)currentWork / totalWork;
		int workIndex = (int)Math.floor(progress * LENGTH);
		if (workIndex-1 == -1) {
			return empty;
		} else if (workIndex == LENGTH) {
			return full;
		}
		return full.substring(0, workIndex) + empty.substring(workIndex);
	}
	
	/**
	 * Finishes the line being used for progress with a new line
	 * to allow for additional printing.
	 * 
	 * <br><br>Using {@link #update(int, int, String)} after this will cause
	 * formatting to break.
	 */
	public void stop () {
		_charactersLastPrinted = 0;
		System.out.println();
	}
}
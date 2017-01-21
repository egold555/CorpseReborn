package me.nrubin29.pastebinapi;

public class PastebinException extends Exception {

	private static final long serialVersionUID = 1L;

	protected PastebinException(String msg) {
		super(msg);
	}
}
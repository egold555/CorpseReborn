package me.nrubin29.pastebinapi;

public enum ExpireDate {

	NEVER("N"),
	TEN_MINUTES("10M"),
	ONE_HOUR("1H"),
	ONE_DAY("1D"),
	ONE_WEEK("1W"),
	TWO_WEEKS("2W"),
	ONE_MONTH("1M");
	
	private String code;
	
	private ExpireDate(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
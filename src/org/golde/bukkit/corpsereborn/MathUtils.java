package org.golde.bukkit.corpsereborn;


public class MathUtils {

	public static int getNearestMultipleOfNumberCeil(int number, int multiple) {
		return (int) (multiple * Math.ceil((double) number / multiple));
	}

}

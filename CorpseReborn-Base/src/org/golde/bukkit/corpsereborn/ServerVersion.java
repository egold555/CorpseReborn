package org.golde.bukkit.corpsereborn;

import org.bukkit.Bukkit;

public enum ServerVersion {
	v1_7,
	v1_7_R4,
	v1_8,
	v1_8_R1,
	v1_8_R2,
	v1_8_R3,
	v1_9,
	v1_9_R1,
	v1_9_R2,
	v1_10,
	v1_10_R1,
	v1_11,
	v1_11_R1,
	v1_12,
	v1_12_R1,
	v1_13,
	v1_13_R1,
	v1_13_R2,
	v1_14,
	v1_14_R1,
	v1_15,
	v1_15_R1,
	UNKNOWN;
	public ServerVersion getNiceVersion(){
		switch(this){
		case v1_7_R4: return v1_7;
		case v1_8_R1: return v1_8;
		case v1_8_R2: return v1_8;
		case v1_8_R3: return v1_8;
		case v1_9_R1: return v1_9;
		case v1_9_R2: return v1_9;
		case v1_10_R1: return v1_10;
		case v1_11_R1: return v1_11;
		case v1_12_R1: return v1_12;
		case v1_13_R1: return v1_13;
		case v1_13_R2: return v1_13;
		case v1_14_R1: return v1_14;
		case v1_15_R1: return v1_15;
		default: return UNKNOWN;
		}
	}
	
	public static String getParenthesesContent(String str){
        return str.substring(str.indexOf('(')+1,str.indexOf(')'));
    }
	
	public static String rawMcVersion = getParenthesesContent(Bukkit.getVersion()).replaceAll("MC: ", "");
	
	public static ServerVersion fromClass(String clazz){ //TODO: Change
		
		switch(clazz){
		case "v1_7_R4": return v1_7_R4;
		case "v1_8_R1": return v1_8_R1;
		case "v1_8_R2": return v1_8_R2;
		case "v1_8_R3": return v1_8_R3;
		case "v1_9_R1": return v1_9_R1;
		case "v1_9_R2": return v1_9_R2;
		case "v1_10_R1": return v1_10_R1;
		case "v1_11_R1": return v1_11_R1;
		case "v1_12_R1": return v1_12_R1;
		case "v1_13_R1": return v1_13_R1;
		case "v1_13_R2": return v1_13_R2;
		case "v1_14_R1": return v1_14_R1;
		case "v1_15_R1": return v1_15_R1;
		default: return UNKNOWN;
		}
	}
}

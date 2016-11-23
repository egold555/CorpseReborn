package org.golde.bukkit.corpsereborn;

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
	UNSUPPORTED_SERVER_VERSION;
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
		default: return UNSUPPORTED_SERVER_VERSION;
		}
	}
	
	public static ServerVersion fromClass(String clazz){
		switch(clazz){
		case "v1_7_R4": return v1_7_R4;
		case "v1_8_R1": return v1_8_R1;
		case "v1_8_R2": return v1_8_R2;
		case "v1_8_R3": return v1_8_R3;
		case "v1_9_R1": return v1_9_R1;
		case "v1_9_R2": return v1_9_R2;
		case "v1_10_R1": return v1_10_R1;
		case "v1_11_R1": return v1_11_R1;
		default: return UNSUPPORTED_SERVER_VERSION;
		}
	}
}

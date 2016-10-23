package org.golde.bukkit.corpsereborn;

public enum ServerVersion {

	
	UNSUPPORTED_SERVER_VERSION,
	v1_8,
	v1_9,
	v1_10,
	
	v1_8_R1,
	v1_8_R2,
	v1_8_R3,
	v1_9_R1,
	v1_9_R2,
	v1_10_R1;
	
	public ServerVersion getNiceVersion(){
		switch(this){
		case v1_8_R1: return v1_8;
		case v1_8_R2: return v1_8;
		case v1_8_R3: return v1_8;
		case v1_9_R1: return v1_9;
		case v1_9_R2: return v1_9;
		case v1_10_R1: return v1_10;
		default: return UNSUPPORTED_SERVER_VERSION;
		}
	}
	
	public static ServerVersion fromClass(String clazz){
		switch(clazz){
		case "v1_8_R1": return v1_8_R1;
		case "v1_8_R2": return v1_8_R2;
		case "v1_8_R3": return v1_8_R3;
		case "v1_9_R1": return v1_9_R1;
		case "v1_9_R2": return v1_9_R2;
		case "v1_10_R1": return v1_10_R1;
		default: return UNSUPPORTED_SERVER_VERSION;
		}
	}
}

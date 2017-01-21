package org.golde.bukkit.corpsereborn.dump;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.golde.bukkit.corpsereborn.Main;

public class DumpTemplate {
	
	private final String NEW_LINE = "\n";
	private final String exception;
	private final boolean isFromDumpCommand;
	
	public DumpTemplate(Exception ex){
		if(ex instanceof DumpException){
			exception = "(Dump Command)";
			isFromDumpCommand = true;
			return;
		}
		StringWriter w = new StringWriter();
		ex.printStackTrace(new PrintWriter(w));
		exception = w.toString();
		isFromDumpCommand = false;
	}
	
	private String getDate(){
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	private String getPlugins(){
		String toReturn = "";
		for(Plugin pl:Bukkit.getPluginManager().getPlugins()){
			toReturn = toReturn + "  Name: " + pl.getName() + NEW_LINE +
					"    Version: " + pl.getDescription().getVersion() + NEW_LINE;
		}
		return toReturn;
	}
	
	@SuppressWarnings("static-access")
	public String output(){
		String formatted = "Date: " + getDate() + NEW_LINE +
				"CorpseReborn Version: " + Main.getPlugin().getDescription().getVersion().split(" ")[0] + NEW_LINE + NEW_LINE + 
				"Server Version: " + Bukkit.getVersion() + NEW_LINE +
				"Server Type: " + Main.getPlugin().serverType.name() +NEW_LINE + 
				"Folder Version: " + Main.getPlugin().serverVersion.name() + NEW_LINE +NEW_LINE +
				"Plugins: " + NEW_LINE + getPlugins() + NEW_LINE +
				"Exception: " + exception;
		
		return formatted;
	}

	public boolean isFromDumpCommand() {
		return isFromDumpCommand;
	}
}

package org.golde.bukkit.corpsereborn.dump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.code.CodeBlock;
import net.steppschuh.markdowngenerator.text.heading.Heading;

public class DumpTemplateMarkdown {

	private final String exception;
	private final boolean isFromDumpCommand;
	
	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	private static final String NL = "\n";

	public DumpTemplateMarkdown(Exception ex){
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
	
	private Table getPlugins() {
		
		Table.Builder builder = new Table.Builder();
		builder.withAlignments(Table.ALIGN_LEFT);
		builder.addRow("Name", "Version", "Author");
		for(Plugin pl : Bukkit.getPluginManager().getPlugins()){
			builder.addRow(pl.getName(), pl.getDescription().getVersion(), String.join(", ", pl.getDescription().getAuthors()));
		}
		
		return builder.build();
	}
	
	private CodeBlock getCommonInfo() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Date: " + dateFormat.format(new Date())).append(NL);
		sb.append("CorpseReborn Version: " + Main.getPlugin().getDescription().getVersion().split(" ")[0]).append(NL);
		sb.append("").append(NL);
		sb.append("Server Version: " + Bukkit.getVersion()).append(NL);
		sb.append("Server Type: " + Main.serverType.name()).append(NL);
		sb.append("NMS Version: " + Main.serverVersion.name()).append(NL);
		
		
		return new CodeBlock(sb.toString());
	}
	
	private CodeBlock getFileAsCodeBlock(String configName) {
		StringBuilder sb = new StringBuilder();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(Main.getPlugin().getDataFolder(), configName)));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line).append(NL);
			}
			br.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			return new CodeBlock(e.getMessage());
		}
		
		
		return new CodeBlock(sb.toString());
	}
	
	private CodeBlock getCorpseData() {
		StringBuilder sb = new StringBuilder();
		
		int tempCounter = 0;
		for(CorpseData d:Main.getPlugin().corpses.getAllCorpses()) {
			sb.append(tempCounter + ":").append(NL);
			sb.append("    location: " + d.getOrigLocation()).append(NL);
			sb.append("    name: " +  d.getCorpseName()).append(NL);
			if(d.getProfilePropertiesJson() != null) {
				sb.append("    skin: " + d.getProfilePropertiesJson()).append(NL);
			}
			sb.append("    rotation: " + d.getRotation()).append(NL);
			sb.append("    slot: " + d.getSelectedSlot()).append(NL);
			Inventory inv = d.getLootInventory();

			for(int slot = 0; slot < inv.getSize(); slot++) {
				ItemStack item = inv.getItem(slot);
				if(item != null) {
					sb.append("    inventory." + slot + ": " + item).append(NL);
				}
			}

			tempCounter++;
		}
		
		
		return new CodeBlock(sb.toString());
	}
	
	private CodeBlock getException() {
		StringBuilder sb = new StringBuilder();
		sb.append(exception).append(NL);
		
		return new CodeBlock(sb.toString());
	}
	
	public String getOutput(){
		StringBuilder sb = new StringBuilder();
		
		sb.append(new Heading("Common Info: ", 1)).append(NL);
		sb.append(getCommonInfo()).append("\n");
		
		sb.append(new Heading("Exception: ", 1)).append(NL);
		sb.append(getException()).append("\n");
		
		sb.append(new Heading("Plugins: ", 1)).append(NL);
		sb.append(getPlugins()).append("\n");
		
		sb.append(new Heading("Config: ", 1)).append(NL);
		sb.append(getFileAsCodeBlock("config.yml")).append("\n");
		
		sb.append(new Heading("Corpse Data (Array): ", 1)).append(NL);
		sb.append(getCorpseData()).append("\n");
		
		sb.append(new Heading("Corpse Data (Config): ", 1)).append(NL);
		sb.append(getFileAsCodeBlock("corpses.yml")).append("\n");
		
		return sb.toString();
	}
	
	public boolean isFromDumpCommand() {
		return isFromDumpCommand;
	}
	
}

package org.golde.bukkit.corpsereborn.CorpseAPI.events;

import java.util.ArrayList;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class CorpseRemoveEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	private boolean fromCmd;
	private ArrayList<CorpseData> corpses = new ArrayList<CorpseData>();

    public CorpseRemoveEvent(ArrayList<CorpseData> corpses, boolean fromCmd) {
        this.corpses = corpses;
        this.fromCmd = fromCmd;
    }
    
    public CorpseRemoveEvent(CorpseData corpse, boolean fromCmd) {
    	ArrayList<CorpseData> temp = new ArrayList<CorpseData>();
    	temp.add(corpse);
        this.corpses = temp;
        this.fromCmd = fromCmd;
    }

    public ArrayList<CorpseData> getCorpses() {
        return corpses;
    }
    
    public int removedAmount(){
    	return corpses.size();
    }
    
    public boolean fromCommand(){
    	return fromCmd;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

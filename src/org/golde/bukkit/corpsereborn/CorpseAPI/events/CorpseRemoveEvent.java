package org.golde.bukkit.corpsereborn.CorpseAPI.events;

import java.util.ArrayList;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

/**
 * This event gets called when a corpse gets removed.
 * @author Eric Golde
 *
 */
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

    /**
     * 
     * @return Returns all the corpses that got removed.
     */
    public ArrayList<CorpseData> getCorpses() {
        return corpses;
    }
    
    /**
     * 
     * @return AMount of corpses that got removed.
     */
    public int removedAmount(){
    	return corpses.size();
    }
    
    /**
     * 
     * @return If the corpse was spawned via /removecorpse then this will return true. Corpses removed automatically with the deathtime option in the config will return false.
     */
    public boolean fromCommand(){
    	return fromCmd;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Returns if the event got cancelled or not.
     */
}

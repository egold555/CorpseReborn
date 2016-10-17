package org.golde.bukkit.corpsereborn.CorpseAPI.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class CorpseSpawnEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	private CorpseData cd;
	private boolean fromCmd;

    public CorpseSpawnEvent(CorpseData cd, boolean fromCmd) {
        this.cd = cd;
        this.fromCmd = fromCmd;
    }

    public CorpseData getCorpse() {
        return cd;
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

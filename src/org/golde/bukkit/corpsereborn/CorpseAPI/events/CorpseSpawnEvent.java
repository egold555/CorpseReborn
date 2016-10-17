package org.golde.bukkit.corpsereborn.CorpseAPI.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class CorpseSpawnEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	private CorpseData cd;
	private boolean fromCmd;
	private Player p;

    public CorpseSpawnEvent(CorpseData cd, Player p, boolean fromCmd) {
        this.cd = cd;
        this.fromCmd = fromCmd;
        this.p = p;
    }

    public CorpseData getCorpse() {
        return cd;
    }
    
    public Player getPlayer(){
    	return p;
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

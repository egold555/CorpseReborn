package org.golde.bukkit.corpsereborn.CorpseAPI.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class CorpseClickEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	private CorpseData cd;
	private Player p;

    public CorpseClickEvent(CorpseData cd, Player p) {
        this.cd = cd;
        this.p = p;
    }

    public CorpseData getCorpse() {
        return cd;
    }
    
    public Player getClicker(){
    	return p;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

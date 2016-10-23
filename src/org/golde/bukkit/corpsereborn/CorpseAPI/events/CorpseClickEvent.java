package org.golde.bukkit.corpsereborn.CorpseAPI.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.golde.bukkit.corpsereborn.nms.TypeOfClick;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class CorpseClickEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	private CorpseData cd;
	private Player clicker;
	private TypeOfClick clickType;

    public CorpseClickEvent(CorpseData cd, Player clicker, TypeOfClick clickType) {
        this.cd = cd;
        this.clicker = clicker;
        this.clickType = clickType;
    }

    public CorpseData getCorpse() {
        return cd;
    }
    
    public TypeOfClick getClickType(){
    	return clickType;
    }
    
    public Player getClicker(){
    	return clicker;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

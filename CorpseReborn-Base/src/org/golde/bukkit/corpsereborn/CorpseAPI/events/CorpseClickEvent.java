package org.golde.bukkit.corpsereborn.CorpseAPI.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.golde.bukkit.corpsereborn.nms.TypeOfClick;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

/**
 * This event is called when ever a corpse gets clicked on.
 * @author Eric Golde
 *
 */
public class CorpseClickEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private CorpseData cd;
	private Player clicker;
	private TypeOfClick clickType;
	private boolean cancelled;

    public CorpseClickEvent(CorpseData cd, Player clicker, TypeOfClick clickType) {
        this.cd = cd;
        this.clicker = clicker;
        this.clickType = clickType;
    }

    /**
     * 
     * @return Returns the corpse that got clicked on.
     */
    public CorpseData getCorpse() {
        return cd;
    }
    
    /**
     * 
     * @return Returns a enum of if it was a left or right click that occurred on the corpse.
     */
    public TypeOfClick getClickType(){
    	return clickType;
    }
    
    /**
     * 
     * @return Returns the player who clicked on the corpse.
     */
    public Player getClicker(){
    	return clicker;
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
    
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Set to true to cancel spawning the corpse.
	 * This will make items drop normally.
	 */
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}

package org.golde.bukkit.corpsereborn.CorpseAPI.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

/**
 * This event gets called every time a corpse gets spawned. 
 * @author Eric Golde
 *
 */
public class CorpseSpawnEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	private CorpseData cd;
	private boolean fromCmd;

    public CorpseSpawnEvent(CorpseData cd, boolean fromCmd) {
        this.cd = cd;
        this.fromCmd = fromCmd;
    }

    /**
     * 
     * @return This is the corpse that was created.
     */
    public CorpseData getCorpse() {
        return cd;
    }
    
    /**
     * 
     * @return The player who killed the corpse. Can be null if the player did not die by the means of another player.
     */
    public Player getKiller(){
    	return cd.getPlayer().getKiller();
    }
    
    /**
     * 
     * @return If the corpse was spawned via /spawncorpse then this will return true. Corpses spawned on death return false.
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

}

package org.golde.bukkit.corpsereborn.nms.nmsclasses.packetlisteners;

import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Field;

import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseClickEvent;
import org.golde.bukkit.corpsereborn.nms.TypeOfClick;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

import net.minecraft.server.v1_7_R4.EnumEntityUseAction;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

public class PcktIn_v1_7_R4 extends ChannelInboundHandlerAdapter {

	private Player p;

	public PcktIn_v1_7_R4(Player p) {
		this.p = p;
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof PacketPlayInUseEntity) {
			final PacketPlayInUseEntity packet = (PacketPlayInUseEntity) msg;
			Bukkit.getServer().getScheduler()
					.runTask(Main.getPlugin(), new Runnable() {
						public void run() {
							if (ConfigData.hasLootingInventory()) {
								if (packet.c() == EnumEntityUseAction.INTERACT) {
									for (CorpseData cd : Main.getPlugin().corpses
											.getAllCorpses()) {
										if (cd.getEntityId() == getId(packet)) {
											CorpseClickEvent cce = new CorpseClickEvent(cd, p, TypeOfClick.UNKNOWN);
											Util.callEvent(cce);
											if(!cce.isCancelled()) {
												InventoryView view = p.openInventory(cd
														.getLootInventory());
												cd.setInventoryView(view);
											}
											break;
										}
									}
								}
							}
						}
					});
		}
		super.channelRead(ctx, msg);
	}

	private int getId(PacketPlayInUseEntity packet) {
		try {
			Field afield = packet.getClass().getDeclaredField("a");
			afield.setAccessible(true);
			int id = afield.getInt(packet);
			afield.setAccessible(false);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static final void registerListener(Player p) {
		Channel c = getChannel(p);
		if (c == null) {
			throw new NullPointerException("Couldn't get channel??");
		}
		c.pipeline().addBefore("packet_handler", "packet_in_listener",
				new PcktIn_v1_7_R4(p));
	}

	public static final Channel getChannel(Player p) {
		NetworkManager nm = ((CraftPlayer) p).getHandle().playerConnection.networkManager;
		try {
			Field ifield = nm.getClass().getDeclaredField("m");
			ifield.setAccessible(true);
			Channel c = (Channel) ifield.get(nm);
			ifield.setAccessible(false);
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

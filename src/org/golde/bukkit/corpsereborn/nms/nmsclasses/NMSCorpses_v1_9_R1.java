package org.golde.bukkit.corpsereborn.nms.nmsclasses;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.ChatMessage;
import net.minecraft.server.v1_9_R1.DataWatcher;
import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.DataWatcherRegistry;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.PacketPlayOutBed;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_9_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_9_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_9_R1.PlayerConnection;
import net.minecraft.server.v1_9_R1.WorldSettings.EnumGamemode;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.nms.Corpses;
import org.golde.bukkit.corpsereborn.nms.nmsclasses.packetlisteners.PcktIn_v1_9_R1;

import com.mojang.authlib.GameProfile;

public class NMSCorpses_v1_9_R1 implements Corpses {

	private List<CorpseData> corpses;

	public NMSCorpses_v1_9_R1() {
		corpses = new ArrayList<CorpseData>();
		Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
					public void run() {
						tick();
					}
				}, 0L, 1L);
	}

	public static DataWatcher clonePlayerDatawatcher(Player player,
			int currentEntId) {
		EntityHuman h = new EntityHuman(
				((CraftWorld) player.getWorld()).getHandle(),
				((CraftPlayer) player).getProfile()) {
			public void sendMessage(IChatBaseComponent arg0) {
				return;
			}

			public boolean a(int arg0, String arg1) {
				return false;
			}

			public BlockPosition getChunkCoordinates() {
				return null;
			}

			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean l_() {
				// TODO Auto-generated method stub
				return false;
			}
		};
		h.f(currentEntId);
		return h.getDataWatcher();
	}

	public GameProfile cloneProfileWithRandomUUID(GameProfile oldProf,
			String name) {
		GameProfile newProf = new GameProfile(UUID.randomUUID(), name);
		newProf.getProperties().putAll(oldProf.getProperties());
		return newProf;
	}

	public Location getNonClippableBlockUnderPlayer(Player p, int addToYPos) {
		Location loc = p.getLocation();
		if (loc.getBlockY() < 0) {
			return null;
		}
		for (int y = loc.getBlockY(); y >= 0; y--) {
			Material m = loc.getWorld()
					.getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getType();
			if (m.isSolid()) {
				return new Location(loc.getWorld(), loc.getX(), y + addToYPos,
						loc.getZ());
			}
		}
		return null;
	}

	public CorpseData spawnCorpse(Player p, Inventory inv) {
		int entityId = getNextEntityId();
		GameProfile prof = cloneProfileWithRandomUUID(
				((CraftPlayer) p).getProfile(),
				ConfigData.showTags() ? ConfigData.getUsername(p) : "");
		DataWatcher dw = clonePlayerDatawatcher(p, entityId);

		DataWatcherObject<Byte> obj2 = new DataWatcherObject<Byte>(12, DataWatcherRegistry.a);
		dw.set(obj2, (byte)0x7F);
		Location locUnder = getNonClippableBlockUnderPlayer(p, 1);
		Location used = locUnder != null ? locUnder : p.getLocation();
		used.setYaw(p.getLocation().getYaw());
		used.setPitch(p.getLocation().getPitch());
		NMSCorpseData data = new NMSCorpseData(prof, used, dw, entityId,
				ConfigData.getCorpseTime() * 20, inv);
		corpses.add(data);
		return data;
	}

	public void removeCorpse(CorpseData data) {
		corpses.remove(data);
		data.destroyCorpseFromEveryone();
		if (data.getLootInventory() != null) {
			data.getLootInventory().clear();
			List<HumanEntity> close = new ArrayList<HumanEntity>(data
					.getLootInventory().getViewers());
			for (HumanEntity p : close) {
				p.closeInventory();
			}
		}
	}

	public int getNextEntityId() {
		try {
			Field entityCount = Entity.class.getDeclaredField("entityCount");
			entityCount.setAccessible(true);
			int id = entityCount.getInt(null);
			entityCount.setInt(null, id + 1);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return (int) Math.round(Math.random() * Integer.MAX_VALUE * 0.25);
		}
	}

	public class NMSCorpseData implements CorpseData {

		private Map<Player, Boolean> canSee;
		private Map<Player, Integer> tickLater;
		private GameProfile prof;
		private Location loc;
		private DataWatcher metadata;
		private int entityId;
		private int ticksLeft;
		private Inventory items;

		public NMSCorpseData(GameProfile prof, Location loc,
				DataWatcher metadata, int entityId, int ticksLeft,
				Inventory items) {
			this.prof = prof;
			this.loc = loc;
			this.metadata = metadata;
			this.entityId = entityId;
			this.ticksLeft = ticksLeft;
			this.canSee = new HashMap<Player, Boolean>();
			this.tickLater = new HashMap<Player, Integer>();
			this.items = items;
		}

		public void setCanSee(Player p, boolean canSee) {
			this.canSee.put(p, Boolean.valueOf(canSee));
		}

		public boolean canSee(Player p) {
			return canSee.get(p).booleanValue();
		}

		public void removeFromMap(Player p) {
			canSee.remove(p);
		}

		public boolean mapContainsPlayer(Player p) {
			return canSee.containsKey(p);
		}

		public Set<Player> getPlayersWhoSee() {
			return canSee.keySet();
		}

		public void removeAllFromMap(Collection<Player> players) {
			canSee.keySet().removeAll(players);
		}

		public void setTicksLeft(int ticksLeft) {
			this.ticksLeft = ticksLeft;
		}

		public int getTicksLeft() {
			return ticksLeft;
		}

		public PacketPlayOutNamedEntitySpawn getSpawnPacket() {
			PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
			try {
				Field a = packet.getClass().getDeclaredField("a");
				a.setAccessible(true);
				a.set(packet, entityId);
				Field b = packet.getClass().getDeclaredField("b");
				b.setAccessible(true);
				b.set(packet, prof.getId());
				Field c = packet.getClass().getDeclaredField("c");
				c.setAccessible(true);
				c.setDouble(packet, loc.getX());
				Field d = packet.getClass().getDeclaredField("d");
				d.setAccessible(true);
				d.setDouble(packet, loc.getY()+ 1.0f/16.0f);
				Field e = packet.getClass().getDeclaredField("e");
				e.setAccessible(true);
				e.setDouble(packet, loc.getZ());
				Field f = packet.getClass().getDeclaredField("f");
				f.setAccessible(true);
				f.setByte(packet, (byte) (int) (loc.getYaw() * 256.0F / 360.0F));
				Field g = packet.getClass().getDeclaredField("g");
				g.setAccessible(true);
				g.setByte(packet,
						(byte) (int) (loc.getPitch() * 256.0F / 360.0F));
				Field i = packet.getClass().getDeclaredField("h");
				i.setAccessible(true);
				i.set(packet, metadata);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			return packet;
		}

		public PacketPlayOutBed getBedPacket() {
			PacketPlayOutBed packet = new PacketPlayOutBed();
			try {
				Field a = packet.getClass().getDeclaredField("a");
				a.setAccessible(true);
				a.setInt(packet, entityId);
				Field b = packet.getClass().getDeclaredField("b");
				b.setAccessible(true);
				b.set(packet,
						new BlockPosition(loc.getBlockX(), loc.getBlockY() - 2,
								loc.getBlockZ()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return packet;
		}

		public PacketPlayOutRelEntityMove getMovePacket() {
			PacketPlayOutRelEntityMove packet = new PacketPlayOutRelEntityMove(
					entityId, (byte) 0, (byte) (-60.8), (byte) 0, false);
			return packet;
		}

		public PacketPlayOutPlayerInfo getInfoPacket() {
			PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
					EnumPlayerInfoAction.ADD_PLAYER);
			try {
				Field b = packet.getClass().getDeclaredField("b");
				b.setAccessible(true);
				@SuppressWarnings("unchecked")
				List<PlayerInfoData> data = (List<PlayerInfoData>) b
						.get(packet);
				data.add(packet.new PlayerInfoData(prof, 0,
						EnumGamemode.SURVIVAL, new ChatMessage("")));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return packet;
		}

		public PacketPlayOutPlayerInfo getRemoveInfoPacket() {
			PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
					EnumPlayerInfoAction.REMOVE_PLAYER);
			try {
				Field b = packet.getClass().getDeclaredField("b");
				b.setAccessible(true);
				@SuppressWarnings("unchecked")
				List<PlayerInfoData> data = (List<PlayerInfoData>) b
						.get(packet);
				data.add(packet.new PlayerInfoData(prof, 0,
						EnumGamemode.SURVIVAL, new ChatMessage("")));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return packet;
		}

		public Location getTrueLocation() {
			return loc.clone().add(0, 0.1, 0);
		}

		@SuppressWarnings("deprecation")
		public void resendCorpseToEveryone() {
			PacketPlayOutNamedEntitySpawn spawnPacket = getSpawnPacket();
			PacketPlayOutBed bedPacket = getBedPacket();
			PacketPlayOutRelEntityMove movePacket = getMovePacket();
			PacketPlayOutPlayerInfo infoPacket = getInfoPacket();
			final PacketPlayOutPlayerInfo removeInfo = getRemoveInfoPacket();
			final List<Player> toSend = loc.getWorld().getPlayers();
			for (Player p : toSend) {
				PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
				p.sendBlockChange(loc.clone().subtract(0, 2, 0),
						Material.BED_BLOCK, (byte) 0);
				conn.sendPacket(infoPacket);
				conn.sendPacket(spawnPacket);
				conn.sendPacket(bedPacket);
				conn.sendPacket(movePacket);
			}
			Bukkit.getServer().getScheduler()
					.scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
						public void run() {
							for (Player p : toSend) {
								((CraftPlayer) p).getHandle().playerConnection
										.sendPacket(removeInfo);
							}
						}
					}, 20L);
		}

		@SuppressWarnings("deprecation")
		public void resendCorpseToPlayer(final Player p) {
			PacketPlayOutNamedEntitySpawn spawnPacket = getSpawnPacket();
			PacketPlayOutBed bedPacket = getBedPacket();
			PacketPlayOutRelEntityMove movePacket = getMovePacket();
			PacketPlayOutPlayerInfo infoPacket = getInfoPacket();
			final PacketPlayOutPlayerInfo removeInfo = getRemoveInfoPacket();
			PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
			p.sendBlockChange(loc.clone().subtract(0, 2, 0),
					Material.BED_BLOCK, (byte) 0);
			conn.sendPacket(infoPacket);
			conn.sendPacket(spawnPacket);
			conn.sendPacket(bedPacket);
			conn.sendPacket(movePacket);
			Bukkit.getServer().getScheduler()
					.scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
						public void run() {
							((CraftPlayer) p).getHandle().playerConnection
									.sendPacket(removeInfo);
						}
					}, 20L);

		}

		@SuppressWarnings("deprecation")
		public void destroyCorpseFromPlayer(Player p) {
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(
					entityId);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			Block b = loc.clone().subtract(0, 2, 0).getBlock();
			boolean removeBed = true;
			for (CorpseData cd : getAllCorpses()) {
				if (cd != this
						&& cd.getOrigLocation().clone().subtract(0, 2, 0)
								.getBlock().getLocation()
								.equals(b.getLocation())) {
					removeBed = false;
					break;
				}
			}
			if (removeBed) {
				p.sendBlockChange(b.getLocation(), b.getType(), b.getData());
			}
		}

		public Location getOrigLocation() {
			return loc;
		}

		@SuppressWarnings("deprecation")
		public void destroyCorpseFromEveryone() {
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(
					entityId);
			Block b = loc.clone().subtract(0, 2, 0).getBlock();
			boolean removeBed = true;
			for (CorpseData cd : getAllCorpses()) {
				if (cd != this
						&& cd.getOrigLocation().clone().subtract(0, 2, 0)
								.getBlock().getLocation()
								.equals(b.getLocation())) {
					removeBed = false;
					break;
				}
			}
			for (Player p : loc.getWorld().getPlayers()) {
				((CraftPlayer) p).getHandle().playerConnection
						.sendPacket(packet);
				if (removeBed) {
					p.sendBlockChange(b.getLocation(), b.getType(), b.getData());
				}
			}
		}

		public void tickPlayerLater(int ticks, Player p) {
			tickLater.put(p, Integer.valueOf(ticks));
		}

		public int getPlayerTicksLeft(Player p) {
			return tickLater.get(p);
		}

		public void stopTickingPlayer(Player p) {
			tickLater.remove(p);
		}

		public boolean isTickingPlayer(Player p) {
			return tickLater.containsKey(p);
		}

		public Set<Player> getPlayersTicked() {
			return tickLater.keySet();
		}

		public Inventory getItemsInventory() {
			return items;
		}

		public int getEntityId() {
			return entityId;
		}

		public Inventory getLootInventory() {
			return items;
		}

	}

	public void tick() {
		List<CorpseData> toRemoveCorpses = new ArrayList<CorpseData>();
		for (CorpseData data : corpses) {
			List<Player> worldPlayers = data.getOrigLocation().getWorld()
					.getPlayers();
			for (Player p : worldPlayers) {
				if (data.isTickingPlayer(p)) {
					int ticks = data.getPlayerTicksLeft(p);
					if (ticks > 0) {
						data.tickPlayerLater(ticks - 1, p);
						continue;
					} else {
						data.stopTickingPlayer(p);
					}
				}
				if (data.mapContainsPlayer(p)) {
					if (isInViewDistance(p, data) && !data.canSee(p)) {
						data.resendCorpseToPlayer(p);
						data.setCanSee(p, true);
					} else if (!isInViewDistance(p, data) && data.canSee(p)) {
						data.destroyCorpseFromPlayer(p);
						data.setCanSee(p, false);
					}
				} else if (isInViewDistance(p, data)) {
					data.resendCorpseToPlayer(p);
					data.setCanSee(p, true);
				} else {
					data.setCanSee(p, false);
				}
			}
			if (data.getTicksLeft() >= 0) {
				if (data.getTicksLeft() == 0) {
					toRemoveCorpses.add(data);
				} else {
					data.setTicksLeft(data.getTicksLeft() - 1);
				}
			}
			List<Player> toRemove = new ArrayList<Player>();
			for (Player pl : data.getPlayersWhoSee()) {
				if (!worldPlayers.contains(pl)) {
					toRemove.add(pl);
				}
			}
			data.removeAllFromMap(toRemove);
			toRemove.clear();
			Set<Player> set = data.getPlayersTicked();
			for (Player pl : set) {
				if (!worldPlayers.contains(pl)) {
					toRemove.add(pl);
				}
			}
			set.removeAll(toRemove);
			toRemove.clear();
		}
		for (CorpseData data : toRemoveCorpses) {
			removeCorpse(data);
		}
	}

	public boolean isInViewDistance(Player p, CorpseData data) {
		Location p1loc = p.getLocation();
		Location p2loc = data.getTrueLocation();
		double minX = p2loc.getX() - 45;
		double minY = p2loc.getY() - 45;
		double minZ = p2loc.getZ() - 45;
		double maxX = p2loc.getX() + 45;
		double maxY = p2loc.getY() + 45;
		double maxZ = p2loc.getZ() + 45;
		return p1loc.getX() >= minX && p1loc.getX() <= maxX
				&& p1loc.getY() >= minY && p1loc.getY() <= maxY
				&& p1loc.getZ() >= minZ && p1loc.getZ() <= maxZ;
	}

	public List<CorpseData> getAllCorpses() {
		return corpses;
	}

	public void registerPacketListener(Player p) {
		PcktIn_v1_9_R1.registerListener(p);
	}

}

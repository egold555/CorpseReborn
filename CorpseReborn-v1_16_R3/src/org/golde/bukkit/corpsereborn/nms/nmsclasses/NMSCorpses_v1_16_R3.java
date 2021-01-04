package org.golde.bukkit.corpsereborn.nms.nmsclasses;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.nms.Corpses;
import org.golde.bukkit.corpsereborn.nms.NmsBase;
import org.golde.bukkit.corpsereborn.nms.nmsclasses.packetlisteners.PcktIn_v1_16_R3;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherObject;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityPose;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;

public class NMSCorpses_v1_16_R3 extends NmsBase implements Corpses {

	private List<CorpseData> corpses;

	public NMSCorpses_v1_16_R3() {
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
		
		Location loc = player.getLocation();
		
		EntityHuman h = new EntityHuman(
				((CraftWorld) player.getWorld()).getHandle(),
				new BlockPosition(loc.getX(),loc.getY(),loc.getZ()),
				loc.getYaw(),
				((CraftPlayer) player).getProfile()) {

			public BlockPosition getChunkCoordinates() {
				return null;
			}

			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean isCreative() {
				return false;
			}

		};
		h.e(currentEntId);
		return h.getDataWatcher();
	}

	public GameProfile cloneProfileWithRandomUUID(GameProfile oldProf,
			String name) {
		
		// For NPCs, UUIDv2 should be used
		UUID v2 = UUID.randomUUID();
		v2 = UUID.fromString(new StringBuilder(v2.toString()).replace(14, 15, "2").toString());
		
		GameProfile newProf = new GameProfile(v2, name);
		newProf.getProperties().putAll(oldProf.getProperties());
		return newProf;
	}

	public Location getNonClippableBlockUnderPlayer(Location loc, int addToYPos) {
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

	public CorpseData spawnCorpse(Player p, String overrideUsername, Location loc, Inventory inv, int facing) {
		int entityId = getNextEntityId();
		GameProfile prof = cloneProfileWithRandomUUID(
				((CraftPlayer) p).getProfile(),
				ConfigData.showTags() ? ConfigData.getUsername(p.getName(), overrideUsername) : "");
		
		Location locUnder = getNonClippableBlockUnderPlayer(loc, 1);
		Location used = locUnder != null ? locUnder : loc;
		used.setYaw(loc.getYaw());
		used.setPitch(loc.getPitch());

		NMSCorpseData data = new NMSCorpseData(prof, used, entityId,
				ConfigData.getCorpseTime() * 20, inv, facing);

		if(p.getKiller() != null) {
			data.killerName = p.getKiller().getName();
			data.killerUUID = p.getKiller().getUniqueId();
		}

		data.corpseName = p.getName();

		corpses.add(data);
		spawnSlimeForCorpse(data);
		return data;
	}

	@Override
	public CorpseData loadCorpse(String gpName, String gpJSON, Location loc, Inventory items, int facing) {
		int entityId = getNextEntityId();
		GameProfile gp = new GameProfile(UUID.randomUUID(), ConfigData.showTags() ? ConfigData.getUsername(gpName, null) : "");

		if (gpJSON != null) {
			JsonElement element = new JsonParser().parse(gpJSON);
			PropertyMap propertyMap = new PropertyMap.Serializer().deserialize(element,  null,  null);
			gp.getProperties().putAll(propertyMap);
		}

		Location locUnder = getNonClippableBlockUnderPlayer(loc, 1);
		Location used = locUnder != null ? locUnder : loc;
		used.setYaw(loc.getYaw());
		used.setPitch(loc.getPitch());

		NMSCorpseData data = new NMSCorpseData(gp, used, entityId,
				ConfigData.getCorpseTime() * 20, items, facing);

		data.corpseName = gpName;
		corpses.add(data);
		spawnSlimeForCorpse(data);

		return data;
	}

	public static DataWatcher clonePlayerDatawatcher(GameProfile gp, World world,
			int currentEntId) {
		Location loc = world.getSpawnLocation();
		EntityHuman h = new EntityHuman(((CraftWorld) world).getHandle(),
						new BlockPosition(loc.getX(),loc.getY(),loc.getZ()),
						loc.getYaw(), gp) {
			public BlockPosition getChunkCoordinates() {
				return null;
			}

			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean isCreative() {
				return false;
			}
		};
		h.e(currentEntId);
		return h.getDataWatcher();
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
		deleteSlimeForCorpse(data);
	}


	//Fix for lower versions
	public int getNextEntityId() {
		return getNextEntityIdAtomic().get();
	}

	//1.14 Change -- EntityCount is a AtomicInteger now
	public AtomicInteger getNextEntityIdAtomic() {
		try {
			Field entityCount = Entity.class.getDeclaredField("entityCount");
			entityCount.setAccessible(true);

			//Fix for final field
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(entityCount, entityCount.getModifiers() & ~Modifier.FINAL);

			AtomicInteger id = (AtomicInteger) entityCount.get(null);
			id.incrementAndGet();
			entityCount.set(null, id);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
			return new AtomicInteger((int) Math.round(Math.random() * Integer.MAX_VALUE * 0.25));
		}
	}

	public class NMSCorpseData implements CorpseData {

		private Map<Player, Boolean> canSee;
		private Map<Player, Integer> tickLater;
		private GameProfile prof;
		private Location loc;
//		private DataWatcher metadata;
		private int entityId;
		private int ticksLeft;
		private Inventory items;
		private InventoryView iv;
		private int slot;
		private int rotation;
		private String corpseName;

		private String killerName;
		private UUID killerUUID;
		
		// 1.16 support
		private EntityPlayer entity;
		private boolean sleeping = false;
		
		// If you spawn the bed in the same location consecutively, the corpses rotate in weird ways. This will help with that.
		private Location lastLoc;

		public NMSCorpseData(GameProfile prof, Location loc,
				 int entityId, int ticksLeft,
				Inventory items, int rotation) {
			this.prof = prof;
			this.loc = loc;
//			this.metadata = metadata;
			this.entityId = entityId;
			this.ticksLeft = ticksLeft;
			this.canSee = new HashMap<Player, Boolean>();
			this.tickLater = new HashMap<Player, Integer>();
			this.items = items;
			this.rotation = rotation;
			if(rotation >3 || rotation < 0) {
				this.rotation = 0;
			}
			
			// Initialize corpse entity
			this.entity = new CustomEntityPlayer(loc, prof);
			entity.e(entityId);
			
			DataWatcherObject<Byte> skinFlags = new DataWatcherObject<Byte>(16, DataWatcherRegistry.a);
			entity.getDataWatcher().set(skinFlags, (byte)0x7F);
		}


		@Override
		public int getRotation() {
			return rotation;
		}

		public ItemStack convertBukkitToMc(org.bukkit.inventory.ItemStack stack){
			return CraftItemStack.asNMSCopy(stack);
			/*
			if(stack == null){
				return new ItemStack(Item.getById(0));	
			}
			ItemStack temp = new ItemStack(Item.getById(stack.getTypeId()), stack.getAmount());
			temp.setData((int)stack.getData().getData());
			if(stack.getEnchantments().size() >= 1) {
				temp.addEnchantment(Enchantment.c(0), 1);//Dummy enchantment
			}
			return temp;
			 */
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
				
				
//				Field i = packet.getClass().getDeclaredField("h");
//				i.setAccessible(true);
//				i.set(packet, metadata);
			} catch (Exception e) {

				e.printStackTrace();
			}
			return packet;
		}

		//		public PacketPlayOutBed getBedPacket() {
		//			PacketPlayOutBed packet = new PacketPlayOutBed();
		//			try {
		//				Field a = packet.getClass().getDeclaredField("a");
		//				a.setAccessible(true);
		//				a.setInt(packet, entityId);
		//				Field b = packet.getClass().getDeclaredField("b");
		//				b.setAccessible(true);
		//				b.set(packet,
		//						new BlockPosition(loc.getBlockX(), Util.bedLocation(),
		//								loc.getBlockZ()));
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//			return packet;
		//		}

		public PacketPlayOutRelEntityMove getMovePacket() {
			PacketPlayOutRelEntityMove packet = new PacketPlayOutRelEntityMove(
					entityId, (short) (0), (short) (-61.8), (short) (0), false);
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
				data.add(packet.new PlayerInfoData(prof, -1,
						EnumGamemode.SURVIVAL, new ChatMessage("[CR]")));
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
				data.add(packet.new PlayerInfoData(prof, -1,
						EnumGamemode.SURVIVAL, new ChatMessage("[CR]")));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return packet;
		}

		public Location getTrueLocation() {
			return loc.clone().add(0, 0.1, 0);
		}

//		public PacketPlayOutEntityEquipment getEquipmentPacket(EnumItemSlot slot, ItemStack stack){
//			return new PacketPlayOutEntityEquipment(entityId, slot, stack);
//		}

		public PacketPlayOutEntityMetadata getEntityMetadataPacket() {
			return new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), false);
		}
		
		public PacketPlayOutEntityDestroy getDestroyPacket() {
			return new PacketPlayOutEntityDestroy(
					entityId);
		}

		@SuppressWarnings("deprecation")
		public void resendCorpseToEveryone() {
			PacketPlayOutPlayerInfo infoPacket = getInfoPacket();
			PacketPlayOutNamedEntitySpawn spawnPacket = getSpawnPacket();
			PacketPlayOutPlayerInfo removeInfo = getRemoveInfoPacket();

			//Defining the list of Pairs with EnumItemSlot and (NMS) ItemStack
			final List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipmentList = new ArrayList<>();

			equipmentList.add(new Pair<>(EnumItemSlot.HEAD, convertBukkitToMc(items.getItem(1))));
			equipmentList.add(new Pair<>(EnumItemSlot.CHEST, convertBukkitToMc(items.getItem(2))));
			equipmentList.add(new Pair<>(EnumItemSlot.LEGS, convertBukkitToMc(items.getItem(3))));
			equipmentList.add(new Pair<>(EnumItemSlot.FEET, convertBukkitToMc(items.getItem(4))));
			equipmentList.add(new Pair<>(EnumItemSlot.MAINHAND, convertBukkitToMc(items.getItem(slot+45))));
			equipmentList.add(new Pair<>(EnumItemSlot.OFFHAND, convertBukkitToMc(items.getItem(7))));

			//Creating the packet
			final PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(entityId, equipmentList);
			final List<Player> toSend = loc.getWorld().getPlayers();
			for (Player p : toSend) {
				PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
				Location bedLocation = Util.bedLocation(loc);
				
				if(sleeping) {
					wakeUp(p, lastLoc);
					conn.sendPacket(getDestroyPacket());
				}
				
				if(lastLoc != null && bedLocation.equals(lastLoc))
					lastLoc = bedLocation.add(0, 5, 0);
				
				conn.sendPacket(infoPacket);
				conn.sendPacket(spawnPacket);

				// The EntityMetadataPacket is sent from here.
				makePlayerSleep(p, conn, lastLoc = bedLocation);
				sleeping = true;
				
				if(ConfigData.shouldRenderArmor()) {
					conn.sendPacket(entityEquipment);
				}
			}
			
			Bukkit.getServer().getScheduler()
			.scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
				public void run() {
					for (Player p : toSend) {
						((CraftPlayer) p).getHandle().playerConnection
						.sendPacket(removeInfo);
					}
				}
			}, 40L);
		}




		@SuppressWarnings("deprecation")
		public void resendCorpseToPlayer(final Player p) {
			PacketPlayOutPlayerInfo infoPacket = getInfoPacket();
			PacketPlayOutNamedEntitySpawn spawnPacket = getSpawnPacket();
			PacketPlayOutPlayerInfo removeInfo = getRemoveInfoPacket();

			//Defining the list of Pairs with EnumItemSlot and (NMS) ItemStack
			final List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> equipmentList = new ArrayList<>();

			equipmentList.add(new Pair<>(EnumItemSlot.HEAD, convertBukkitToMc(items.getItem(1))));
			equipmentList.add(new Pair<>(EnumItemSlot.CHEST, convertBukkitToMc(items.getItem(2))));
			equipmentList.add(new Pair<>(EnumItemSlot.LEGS, convertBukkitToMc(items.getItem(3))));
			equipmentList.add(new Pair<>(EnumItemSlot.FEET, convertBukkitToMc(items.getItem(4))));
			equipmentList.add(new Pair<>(EnumItemSlot.MAINHAND, convertBukkitToMc(items.getItem(slot+45))));
			equipmentList.add(new Pair<>(EnumItemSlot.OFFHAND, convertBukkitToMc(items.getItem(7))));

			//Creating the packet
			final PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(entityId, equipmentList);
			
			PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
			Location bedLocation = Util.bedLocation(loc);
			
			if(sleeping) {
				wakeUp(p, lastLoc);
				conn.sendPacket(getDestroyPacket());
			}
			
			
			if(lastLoc != null && bedLocation.equals(lastLoc))
				lastLoc = bedLocation.add(1, 0, 1);
			
			conn.sendPacket(infoPacket);
			conn.sendPacket(spawnPacket);

			// The EntityMetadataPacket is sent from here.
			makePlayerSleep(p, conn, lastLoc = bedLocation);
			sleeping = true;
			
			if(ConfigData.shouldRenderArmor()) {
				conn.sendPacket(entityEquipment);
			}
			
			Bukkit.getServer().getScheduler()
			.scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
				public void run() {
					((CraftPlayer) p).getHandle().playerConnection
					.sendPacket(removeInfo);
				}
			}, 40L);

		}

		private BlockPosition getBlockPositionFromBukkitLocation(Location loc) {
			return new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}

		private void makePlayerSleep(Player p, PlayerConnection conn, Location bedPos) {
			try {

				// These lines force an entity player into the sleeping position
				Field poseF = Entity.class.getDeclaredField("POSE");
				poseF.setAccessible(true);
				DataWatcherObject<EntityPose> POSE = (DataWatcherObject<EntityPose>) poseF.get(null);
				entity.getDataWatcher().set(POSE, EntityPose.SLEEPING);
				DataWatcherObject<Byte> skinFlags = new DataWatcherObject<Byte>(16, DataWatcherRegistry.a);
				entity.getDataWatcher().set(skinFlags, (byte)0x7F);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			
			p.sendBlockChange(bedPos, Material.RED_BED.createBlockData());
			entity.entitySleep(getBlockPositionFromBukkitLocation(bedPos)); //go to sleep
			
			conn.sendPacket(getMovePacket());
			conn.sendPacket(getEntityMetadataPacket());
		}
		
		// When resending corpses, you need to wake them up otherwise they won't sleep again
		private void wakeUp(Player p, Location bedPos) {
			try {
				// These lines force an entity player into the standing position
				Field poseF = Entity.class.getDeclaredField("POSE");
				poseF.setAccessible(true);
				DataWatcherObject<EntityPose> POSE = (DataWatcherObject<EntityPose>) poseF.get(null);
				entity.getDataWatcher().set(POSE, EntityPose.STANDING);
				
				// As a matter of fact, waking up a corpse includes removing the skin layers temporarily. This took TOO long to figure out.
				DataWatcherObject<Byte> skinFlags = new DataWatcherObject<Byte>(16, DataWatcherRegistry.a);
				entity.getDataWatcher().set(skinFlags, (byte)0xFF);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			
			p.sendBlockChange(bedPos, Material.AIR.createBlockData());
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), false));
		}
		
		@SuppressWarnings("deprecation")
		public void destroyCorpseFromPlayer(Player p) {
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(
					entityId);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			Block b = Util.bedLocation(loc).getBlock();
			boolean removeBed = true;
			for (CorpseData cd : getAllCorpses()) {
				if (cd != this
						&& Util.bedLocation(cd.getOrigLocation())
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
						&& Util.bedLocation(cd.getOrigLocation())
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

		@Override
		public void setInventoryView(InventoryView iv) {
			this.iv = iv;
		}

		@Override
		public InventoryView getInventoryView() {
			return iv;
		}

		@Override
		public int getSelectedSlot() {
			return slot;
		}

		@Override
		public CorpseData setSelectedSlot(int slot) {
			this.slot = slot;
			return this;
		}


		@Override
		public String getCorpseName() {
			return corpseName;
		}


		@Override
		public String getKillerUsername() {
			return killerName;
		}


		@Override
		public UUID getKillerUUID() {
			return killerUUID;
		}


		@Override
		public String getProfilePropertiesJson() {
			PropertyMap pmap = prof.getProperties();
			JsonElement element = new PropertyMap.Serializer().serialize(pmap, null, null);
			return element.toString();
		}

	}

	int tickNumber = 0;

	public void tick() {
		++tickNumber;

		List<CorpseData> toRemoveCorpses = new ArrayList<CorpseData>();
		for (final CorpseData data : corpses) {
			List<Player> worldPlayers = data.getOrigLocation().getWorld()
					.getPlayers();
			for (final Player p : worldPlayers) {
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
						new BukkitRunnable() {
							public void run() {
								data.resendCorpseToPlayer(p);
							}
						}.runTaskLater(Main.getPlugin(), 2);

						data.setCanSee(p, true);
					} else if (!isInViewDistance(p, data) && data.canSee(p)) {
						data.destroyCorpseFromPlayer(p);
						data.setCanSee(p, false);
					}
				} else if (isInViewDistance(p, data)) {
					new BukkitRunnable() {
						public void run() {
							data.resendCorpseToPlayer(p);
						}
					}.runTaskLater(Main.getPlugin(), 2);
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

	public List<CorpseData> getAllCorpses() {
		return corpses;
	}

	public void registerPacketListener(Player p) {
		PcktIn_v1_16_R3.registerListener(p);
	}

	@Override
	protected void addNbtTagsToSlime(LivingEntity slime) {
		slime.setAI(false);
		Entity nmsEntity = ((CraftEntity) slime).getHandle();
		nmsEntity.setSilent(true);
		nmsEntity.setInvulnerable(true);
		nmsEntity.setNoGravity(true);
	}

	// Used to represent corpses
	static class CustomEntityPlayer extends EntityPlayer {

		public CustomEntityPlayer(Location p, GameProfile prof) {
			super(((CraftWorld) p.getWorld()).getHandle().getMinecraftServer(), ((CraftWorld) p.getWorld()).getHandle(), prof, new PlayerInteractManager(((CraftWorld) p.getWorld()).getHandle()));
		}

	}

}
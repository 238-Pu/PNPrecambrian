package net.pnprecambrian;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;
import java.util.function.Supplier;

public class ElementsPNPrecambrianMod implements IFuelHandler, IWorldGenerator {
    public final List<ModElement> elements = new ArrayList<>();
    public final List<Supplier<Block>> blocks = new ArrayList<>();
    public final List<Supplier<Item>> items = new ArrayList<>();
    public final List<Supplier<Biome>> biomes = new ArrayList<>();
    public final List<Supplier<EntityEntry>> entities = new ArrayList<>();
    public final List<Supplier<Potion>> potions = new ArrayList<>();
    public static Map<ResourceLocation, net.minecraft.util.SoundEvent> sounds = new HashMap<>();
    public ElementsPNPrecambrianMod() {
    }

    public void preInit(FMLPreInitializationEvent event) {
        try {
            for (ASMDataTable.ASMData asmData : event.getAsmData().getAll(ModElement.Tag.class.getName())) {
                Class<?> clazz = Class.forName(asmData.getClassName());
                if (clazz.getSuperclass() == net.pnprecambrian.ElementsPNPrecambrianMod.ModElement.class)
                    elements.add((net.pnprecambrian.ElementsPNPrecambrianMod.ModElement) clazz.getConstructor(this.getClass()).newInstance(this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(elements);
        elements.forEach(net.pnprecambrian.ElementsPNPrecambrianMod.ModElement::initElements);
        this.addNetworkMessage(PNPrecambrianModVariables.WorldSavedDataSyncMessageHandler.class, PNPrecambrianModVariables.WorldSavedDataSyncMessage.class,
                Side.SERVER, Side.CLIENT);
    }

    public void registerSounds(RegistryEvent.Register<net.minecraft.util.SoundEvent> event) {
        for (Map.Entry<ResourceLocation, net.minecraft.util.SoundEvent> sound : sounds.entrySet())
            event.getRegistry().register(sound.getValue().setRegistryName(sound.getKey()));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator cg, IChunkProvider cp) {
        elements.forEach(element -> element.generateWorld(random, chunkX * 16, chunkZ * 16, world, world.provider.getDimension(), cg, cp));
    }

    @Override
    public int getBurnTime(ItemStack fuel) {
        for (ModElement element : elements) {
            int ret = element.addFuel(fuel);
            if (ret != 0)
                return ret;
        }
        return 0;
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.world.isRemote) {
            WorldSavedData mapdata = PNPrecambrianModVariables.MapVariables.get(event.player.world);
            WorldSavedData worlddata = PNPrecambrianModVariables.WorldVariables.get(event.player.world);
            if (mapdata != null)
                PNPrecambrianMod.PACKET_HANDLER.sendTo(new PNPrecambrianModVariables.WorldSavedDataSyncMessage(0, mapdata), (EntityPlayerMP) event.player);
            if (worlddata != null)
                PNPrecambrianMod.PACKET_HANDLER.sendTo(new PNPrecambrianModVariables.WorldSavedDataSyncMessage(1, worlddata), (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.player.world.isRemote) {
            WorldSavedData worlddata = PNPrecambrianModVariables.WorldVariables.get(event.player.world);
            if (worlddata != null)
                PNPrecambrianMod.PACKET_HANDLER.sendTo(new PNPrecambrianModVariables.WorldSavedDataSyncMessage(1, worlddata), (EntityPlayerMP) event.player);
        }
    }
    private int messageID = 0;
    public <T extends IMessage, V extends IMessage> void addNetworkMessage(Class<? extends IMessageHandler<T, V>> handler, Class<T> messageClass,
                                                                           Side... sides) {
        for (Side side : sides)
            PNPrecambrianMod.PACKET_HANDLER.registerMessage(handler, messageClass, messageID, side);
        messageID++;
    }
    public static class GuiHandler implements IGuiHandler {
        @Override
        public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            return null;
        }

        @Override
        public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            return null;
        }
    }
    public List<ModElement> getElements() {
        return elements;
    }

    public List<Supplier<Block>> getBlocks() {
        return blocks;
    }

    public List<Supplier<Item>> getItems() {
        return items;
    }

    public List<Supplier<Biome>> getBiomes() {
        return biomes;
    }

    public List<Supplier<EntityEntry>> getEntities() {
        return entities;
    }

    public List<Supplier<Potion>> getPotions() {
        return potions;
    }
    public static class ModElement implements Comparable<ModElement> {
        @Retention(RetentionPolicy.RUNTIME)
        public @interface Tag {
        }
        protected final net.pnprecambrian.ElementsPNPrecambrianMod elements;
        protected final int sortid;
        public ModElement(net.pnprecambrian.ElementsPNPrecambrianMod elements, int sortid) {
            this.elements = elements;
            this.sortid = sortid;
        }

        public void initElements() {
        }

        public void init(FMLInitializationEvent event) {
        }

        public void preInit(FMLPreInitializationEvent event) {
        }

        public void generateWorld(Random random, int posX, int posZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp) {
        }

        public void serverLoad(FMLServerStartingEvent event) {
        }

        public void registerModels(ModelRegistryEvent event) {
        }

        public int addFuel(ItemStack fuel) {
            return 0;
        }

        @Override
        public int compareTo(ModElement other) {
            return this.sortid - other.sortid;
        }
    }
}

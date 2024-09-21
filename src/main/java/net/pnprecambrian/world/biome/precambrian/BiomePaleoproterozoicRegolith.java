
package net.pnprecambrian.world.biome.precambrian;

import net.lepidodendron.ElementsLepidodendronMod;
import net.lepidodendron.block.BlockDiskagma;
import net.lepidodendron.block.BlockSandPaleoproterozoic;
import net.lepidodendron.util.EnumBiomeTypePrecambrian;
import net.lepidodendron.world.biome.precambrian.BiomePrecambrian;
import net.lepidodendron.world.gen.WorldGenCobble;
import net.lepidodendron.world.gen.WorldGenNullTree;
import net.lepidodendron.world.gen.WorldGenScoria;
import net.lepidodendron.world.gen.WorldGenSinglePlantOptionalWater;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

@ElementsLepidodendronMod.ModElement.Tag
public class BiomePaleoproterozoicRegolith extends ElementsLepidodendronMod.ModElement {
	@GameRegistry.ObjectHolder("lepidodendron:paleoproterozoic_regolith")
	public static final BiomeGenCustom biome = null;
	public BiomePaleoproterozoicRegolith(ElementsLepidodendronMod instance) {
		super(instance, 1589);
	}

	@Override
	public void initElements() {
		elements.biomes.add(() -> new BiomeGenCustom());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		BiomeDictionary.addTypes(biome, BiomeDictionary.Type.DRY);
		BiomeDictionary.addTypes(biome, BiomeDictionary.Type.SANDY);
		BiomeDictionary.addTypes(biome, BiomeDictionary.Type.WASTELAND);
		BiomeDictionary.addTypes(biome, BiomeDictionary.Type.DEAD);
		BiomeDictionary.addTypes(biome, BiomeDictionary.Type.COLD);
	}

	static class BiomeGenCustom extends BiomePrecambrian {
		public BiomeGenCustom() {
			//super(new BiomeProperties("Permian Desert").setRainfall(0.0F).setBaseHeight(0.18F).setHeightVariation(0.17F).setTemperature(2.2F).setRainDisabled().setWaterColor(10990706));
			super(new BiomeProperties("Paleoproterozoic Regolith").setRainfall(0.0F).setBaseHeight(3.037F).setHeightVariation(0.062F).setTemperature(2.1F).setRainDisabled().setWaterColor(0xB00000));
			setRegistryName("lepidodendron:paleoproterozoic_regolith");
			topBlock = BlockSandPaleoproterozoic.block.getDefaultState();
			fillerBlock = BlockSandPaleoproterozoic.block.getDefaultState();
			decorator.treesPerChunk = -999;
			decorator.flowersPerChunk = 0;
			decorator.grassPerChunk = 0;
			decorator.mushroomsPerChunk = 0;
			decorator.bigMushroomsPerChunk = 0;
			decorator.reedsPerChunk = 0;
			decorator.cactiPerChunk = 0;
			decorator.sandPatchesPerChunk = 0;
			decorator.gravelPatchesPerChunk = 2;
			this.spawnableMonsterList.clear();
			this.spawnableCreatureList.clear();
			this.spawnableWaterCreatureList.clear();
			this.spawnableCaveCreatureList.clear();
		}

		protected static final WorldGenNullTree NULL_TREE = new WorldGenNullTree(false);

		protected static final WorldGenScoria SCORIA_GENERATOR = new WorldGenScoria();
		protected static final WorldGenCobble COBBLE_GENERATOR = new WorldGenCobble();
		//protected static final WorldGenDiskagma DISKAGMA_GENERATOR = new WorldGenDiskagma();
		protected static final WorldGenSinglePlantOptionalWater DISKAGMA_GENERATOR = new WorldGenSinglePlantOptionalWater();

		public WorldGenAbstractTree getRandomTreeFeature(Random rand)
		{
			return NULL_TREE;
		}

		/*@Override
		public int getSkyColorByTemp(float par1)
		{
			if (LepidodendronConfig.renderFog) {
				return 0xE2C1FD;
			}
			return super.getSkyColorByTemp(par1);
		}*/

		@Override
		public void decorate(World worldIn, Random rand, BlockPos pos) {

			if (net.minecraftforge.event.terraingen.TerrainGen.decorate(worldIn, rand, new net.minecraft.util.math.ChunkPos(pos), net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.ROCK)) {
				for (int i = 0; i < 4; ++i) {
					int j = rand.nextInt(16) + 8;
					int k = rand.nextInt(16) + 8;
					int l = rand.nextInt(worldIn.getHeight(pos.add(j, 0, k)).getY() + 32);
					SCORIA_GENERATOR.generate(worldIn, rand, pos.add(j, l, k));
				}
			}

			if (net.minecraftforge.event.terraingen.TerrainGen.decorate(worldIn, rand, new net.minecraft.util.math.ChunkPos(pos), net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.ROCK)) {
				for (int i = 0; i < 4; ++i) {
					int j = rand.nextInt(16) + 8;
					int k = rand.nextInt(16) + 8;
					int l = rand.nextInt(worldIn.getHeight(pos.add(j, 0, k)).getY() + 32);
					COBBLE_GENERATOR.generate(worldIn, rand, pos.add(j, l, k));
				}
			}

			if (net.minecraftforge.event.terraingen.TerrainGen.decorate(worldIn, rand, new net.minecraft.util.math.ChunkPos(pos), net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.ROCK)) {
				for (int i = 0; i < 3; ++i) {
					int j = rand.nextInt(16) + 8;
					int k = rand.nextInt(16) + 8;
					int l = rand.nextInt(worldIn.getHeight(pos.add(j, 0, k)).getY() + 32);
					DISKAGMA_GENERATOR.generate(BlockDiskagma.block.getDefaultState(), worldIn, rand, pos.add(j, l, k), 0, 255, true);
				}
			}

			super.decorate(worldIn, rand, pos);
		}

		@Override
		public EnumBiomeTypePrecambrian getBiomeType() {
			return EnumBiomeTypePrecambrian.Paleoproterozoic;
		}

	}
}

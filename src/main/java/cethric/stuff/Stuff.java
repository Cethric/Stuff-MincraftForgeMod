package cethric.stuff;

import cethric.stuff.block.BlockMovingController;
import cethric.stuff.block.BlockTicker;
import cethric.stuff.common.CommonProxy;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@Mod(modid=Stuff.MODID, version=Stuff.MODVERSION)
public class Stuff {
    private static final Logger LOGGER = LogManager.getLogger(Stuff.class);
    public static final String MODID = "stuff";
    public static final String MODVERSION = "0.0.0";
    public static final BlockMovingController blockMoving = new BlockMovingController(Material.clay);
    public static final BlockTicker blockTicker = new BlockTicker(Material.circuits);

    public static final CreativeTabs blocksTab = new CreativeTabs(CreativeTabs.getNextID(), "stuff") {
        @Override
        public Item getTabIconItem() {
            return Items.brick;
        }
    };
    private static Configuration config;

    @SidedProxy(clientSide = "cethric.stuff.client.ClientProxy", serverSide = "cethric.stuff.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("stuff was called by FML pre-init");
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        String[] allowedBlocks = config.get("allowed_blocks", "Blocks", new String[] {""}).getStringList();
        config.save();
        LOGGER.info(allowedBlocks[0]);

        blockMoving.setHardness(0.5f).setStepSound(BlockMovingController.soundTypeGravel).setUnlocalizedName("blockMoving").setCreativeTab(blocksTab);
        GameRegistry.registerBlock(blockMoving, "blockMoving");

        blockTicker.setHardness(0.5f).setStepSound(BlockTicker.soundTypeGravel).setUnlocalizedName("blockTicker").setCreativeTab(blocksTab);
        GameRegistry.registerBlock(blockTicker, "blockTicker");

        BlockMovingController.allowedBlocks = Arrays.asList(Stuff.getConfig().get("allowed_blocks", "Blocks", new String[]{}).getStringList());

        System.out.println(Minecraft.getMinecraft().getResourceManager().getResourceDomains());
        System.out.println(Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("stuff was called by FML init");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOGGER.info("stuff was called by FML post-init");
    }

    public static Configuration getConfig() {
        return config;
    }
}

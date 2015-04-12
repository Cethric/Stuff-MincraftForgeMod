package cethric.stuff.block;

import cethric.stuff.Stuff;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created by blakerogan on 10/04/15.
 */
public class BlockMovingController extends Block {
    private static final Logger LOGGER = LogManager.getLogger(BlockMovingController.class);

    private static int MAX_BLOCKS = 1024;
    private static int MAX_SEARCH_X = 32;
    private static int MAX_SEARCH_Y = 32;
    private static int MAX_SEARCH_Z = 32;
    private List<BlockPos> blockCache = new ArrayList<BlockPos>();
    private boolean cacheSet = false;
    private static boolean checked = false;

    private BlockPos moveDir = new BlockPos(0, 0, 0);
    private EntityPlayer owner = null;


    public BlockMovingController(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (side == EnumFacing.NORTH) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!worldIn.isRemote) {
            if (worldIn.isBlockPowered(pos)) {
                if (cacheSet) {
                    System.out.println("Moving?");
                    checked = false;
                    moveBlocks(worldIn);
                    cacheSet = false;
//                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Move is Complete"));

                    updateCache(pos, worldIn);
                } else {
                    try {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Set The Controller Blocks first. (Right Click BlockMovingController)"));
                    } catch (NullPointerException e) {
                        LOGGER.error("Is this a problem? - " + e.getLocalizedMessage(), e);
                    }
                }
            }
        }
    }

    public void updateCache(BlockPos pos, World worldIn) {
        blockCache.clear();
//        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Updating Block Cache"));
        String dir = EnumFacing.getFacingFromVector(moveDir.getX(), moveDir.getY(), moveDir.getZ()).name().toLowerCase();
        if (dir.equals("up")) {
            getNeighborsUp(pos, worldIn);
        } else if (dir.equals("down")) {
            getNeighborsDown(pos, worldIn);
        } else if (dir.equals("north")) {
            getNeighborsNorth(pos, worldIn);
        } else if (dir.equals("south")) {
            System.out.println(dir);
            getNeighborsSouth(pos, worldIn);
        } else if (dir.equals("east")) {
            System.out.println(dir);
            getNeighborsEast(pos, worldIn);
        } else if (dir.equals("west")) {
            getNeighborsWest(pos, worldIn);
        } else {
            System.out.println(dir);
            getNeighbors(pos, worldIn);
        }

        cacheSet = true;
        checked = false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
//            System.out.println(side);
            moveDir = new BlockPos(0, 0, 0);
            moveDir = moveDir.add(side.getDirectionVec());
            updateCache(pos, worldIn);
            owner = playerIn;
            return true;
        }
        return false;
    }

    public void getNeighborsUp(BlockPos pos, World world) {
        for (int y = pos.getY()+MAX_SEARCH_Y/2; y > pos.getY()-MAX_SEARCH_Y/2; y--) {
            for (int x = pos.getX()-MAX_SEARCH_X/2; x < pos.getX()+MAX_SEARCH_X/2; x++) {
                for (int z = pos.getZ()-MAX_SEARCH_Z/2; z < pos.getZ()+MAX_SEARCH_Z/2; z++) {
                    BlockPos pos1 = new BlockPos(x, y, z);
                    IBlockState iblockstate1 = world.getBlockState(pos1);
                    if (Arrays.asList(Stuff.getConfig().get("allowed_blocks", "Blocks", new String[]{}).getStringList()).contains(iblockstate1.getBlock().getClass().getCanonicalName())) {
                        if (!surroundedByAir(pos1, world) && isAttachedToController(pos1, world)) {
                            blockCache.add(pos1);
                        }
                    }
                }
            }
        }
    }

    public void getNeighborsDown(BlockPos pos, World world) {
        for (int y = pos.getY()+MAX_SEARCH_Y/2; y > pos.getY()-MAX_SEARCH_Y/2; y--) {
            for (int x = pos.getX()-MAX_SEARCH_X/2; x < pos.getX()+MAX_SEARCH_X/2; x++) {
                for (int z = pos.getZ()-MAX_SEARCH_Z/2; z < pos.getZ()+MAX_SEARCH_Z/2; z++) {
                    BlockPos pos1 = new BlockPos(x, y, z);
                    IBlockState iblockstate1 = world.getBlockState(pos1);
                    if (Arrays.asList(Stuff.getConfig().get("allowed_blocks", "Blocks", new String[]{}).getStringList()).contains(iblockstate1.getBlock().getClass().getCanonicalName())) {
                        if (!surroundedByAir(pos1, world) && isAttachedToController(pos1, world)) {
                            blockCache.add(pos1);
                        }
                    }
                }
            }
        }
    }

    public void getNeighborsNorth(BlockPos pos, World world) {
        for (int z = pos.getZ()-MAX_SEARCH_Z/2; z < pos.getZ()+MAX_SEARCH_Z/2; z++) {
            for (int x = pos.getX()-MAX_SEARCH_X/2; x < pos.getX()+MAX_SEARCH_X/2; x++) {
                for (int y = pos.getY()+MAX_SEARCH_Y/2; y > pos.getY()-MAX_SEARCH_Y/2; y--) {
                    BlockPos pos1 = new BlockPos(x, y, z);
                    IBlockState iblockstate1 = world.getBlockState(pos1);
                    if (Arrays.asList(Stuff.getConfig().get("allowed_blocks", "Blocks", new String[]{}).getStringList()).contains(iblockstate1.getBlock().getClass().getCanonicalName())) {
                        if (!surroundedByAir(pos1, world) && isAttachedToController(pos1, world)) {
                            blockCache.add(pos1);
                        }
                    }
                }
            }
        }
    }

    // TODO Fix this so that non blocks (redstone, torches, etc) are checked
    public void getNeighborsSouth(BlockPos pos, World world) {
        for (int z = pos.getZ()+MAX_SEARCH_Z/2; z > pos.getZ()-MAX_SEARCH_Z/2; z--) {
            for (int x = pos.getX()+MAX_SEARCH_X/2; x > pos.getX()-MAX_SEARCH_X/2; x--) {
                for (int y = pos.getY()+MAX_SEARCH_Y/2; y > pos.getY()-MAX_SEARCH_Y/2; y--) {
                    BlockPos pos1 = new BlockPos(x, y, z);
                    IBlockState iblockstate1 = world.getBlockState(pos1);
                    if (Arrays.asList(Stuff.getConfig().get("allowed_blocks", "Blocks", new String[]{}).getStringList()).contains(iblockstate1.getBlock().getClass().getCanonicalName())) {
                        if (!surroundedByAir(pos1, world) && isAttachedToController(pos1, world)) {
                            blockCache.add(pos1);
                        }
                    }
                }
            }
        }
    }

    public void getNeighborsWest(BlockPos pos, World world) {
        for (int x = pos.getX()-MAX_SEARCH_X/2; x < pos.getX()+MAX_SEARCH_X/2; x++) {
            for (int y = pos.getY()+MAX_SEARCH_Y/2; y > pos.getY()-MAX_SEARCH_Y/2; y--) {
                for (int z = pos.getZ()-MAX_SEARCH_Z/2; z < pos.getZ()+MAX_SEARCH_Z/2; z++) {
                    BlockPos pos1 = new BlockPos(x, y, z);
                    IBlockState iblockstate1 = world.getBlockState(pos1);
                    if (Arrays.asList(Stuff.getConfig().get("allowed_blocks", "Blocks", new String[]{}).getStringList()).contains(iblockstate1.getBlock().getClass().getCanonicalName())) {
                        if (!surroundedByAir(pos1, world) && isAttachedToController(pos1, world)) {
                            blockCache.add(pos1);
                        }
                    }
                }
            }
        }
    }

    // TODO Fix this so that non blocks (redstone, torches, etc) are checked
    public void getNeighborsEast(BlockPos pos, World world) {
        for (int x = pos.getX()+MAX_SEARCH_X/2; x > pos.getX()-MAX_SEARCH_X/2; x--) {
            for (int y = pos.getY()+MAX_SEARCH_Y/2; y > pos.getY()-MAX_SEARCH_Y/2; y--) {
                for (int z = pos.getZ()+MAX_SEARCH_Z/2; z > pos.getZ()-MAX_SEARCH_Z/2; z--) {
                    BlockPos pos1 = new BlockPos(x, y, z);
                    IBlockState iblockstate1 = world.getBlockState(pos1);
                    if (Arrays.asList(Stuff.getConfig().get("allowed_blocks", "Blocks", new String[]{}).getStringList()).contains(iblockstate1.getBlock().getClass().getCanonicalName())) {
                        if (!surroundedByAir(pos1, world) && isAttachedToController(pos1, world)) {
                            blockCache.add(pos1);
                        }
                    }
                }
            }
        }
    }

    public void getNeighbors(BlockPos pos, World world) {
        for (int y = pos.getY()+MAX_SEARCH_Y/2; y > pos.getY()-MAX_SEARCH_Y/2; y--) {
            for (int x = pos.getX()-MAX_SEARCH_X/2; x < pos.getX()+MAX_SEARCH_X/2; x++) {
                for (int z = pos.getZ()-MAX_SEARCH_Z/2; z < pos.getZ()+MAX_SEARCH_Z/2; z++) {
                    BlockPos pos1 = new BlockPos(x, y, z);
                    IBlockState iblockstate1 = world.getBlockState(pos1);
                    if (Arrays.asList(Stuff.getConfig().get("allowed_blocks", "Blocks", new String[]{}).getStringList()).contains(iblockstate1.getBlock().getClass().getCanonicalName())) {
                        blockCache.add(pos1);
                    }
                }
            }
        }
    }

    public boolean surroundedByAir(BlockPos pos, World world) {
        IBlockState state;
        boolean up = false;
        state = world.getBlockState(pos.up());
        if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
            up = true;
        }
        boolean down = false;
        state = world.getBlockState(pos.down());
        if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
            down = true;
        }
        boolean north = false;
        state = world.getBlockState(pos.north());
        if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
            north = true;
        }
        boolean south = false;
        state = world.getBlockState(pos.south());
        if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
            south = true;
        }
        boolean east = false;
        state = world.getBlockState(pos.east());
        if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
            east = true;
        }
        boolean west = false;
        state = world.getBlockState(pos.west());
        if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
            west = true;
        }
        return up && down && north && south && east && west;
    }

    public boolean isAttachedToController(BlockPos pos, World world) {
        IBlockState state;
        boolean up;
        state = world.getBlockState(pos.up());
        if (state.getBlock().getClass().getCanonicalName().equals("cethric.stuff.block.BlockMovingController")) {
            up = true;
        } else {
            if (Arrays.asList(Stuff.getConfig().get("allowed_blocks", "Blocks", new String[]{}).getStringList()).contains(state.getBlock().getClass().getCanonicalName())) {
                up = isAttachedToController(pos.up(), world);
            } else {
                up = false;
            }
        }
        boolean down;
        state = world.getBlockState(pos.down());
        if (state.getBlock().getClass().getCanonicalName().equals("cethric.stuff.block.BlockMovingController")) {
            down = true;
        } else {
            if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
                down = isAttachedToController(pos.down(), world);
            } else {
                down = false;
            }
        }
        boolean north;
        state = world.getBlockState(pos.north());
        if (state.getBlock().getClass().getCanonicalName().equals("cethric.stuff.block.BlockMovingController")) {
            north = true;
        } else {
            if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
                north = isAttachedToController(pos.north(), world);
            } else {
                north = false;
            }
        }
        boolean south;
        state = world.getBlockState(pos.south());
        if (state.getBlock().getClass().getCanonicalName().equals("cethric.stuff.block.BlockMovingController")) {
            south = true;
        } else {
            if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
                south = isAttachedToController(pos.south(), world);
            } else {
                south = false;
            }
        }
        boolean east;
        state = world.getBlockState(pos.east());
        if (state.getBlock().getClass().getCanonicalName().equals("cethric.stuff.block.BlockMovingController")) {
            east = true;
        } else {
            if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
                east = isAttachedToController(pos.up(), world);
            } else {
                east = false;
            }
        }
        boolean west;
        state = world.getBlockState(pos.west());
        if (state.getBlock().getClass().getCanonicalName().equals("cethric.stuff.block.BlockMovingController")) {
            west = true;
        } else {
            if (! state.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
                west = isAttachedToController(pos.west(), world);
            } else {
                west = false;
            }
        }
        return up || down || north || south || east || west;
    }

    public BlockPos getBlockUnderEntity(World world, Entity entity) {
        int blockX = MathHelper.floor_double(entity.posX);
        int blockY = MathHelper.floor_double(entity.getEntityBoundingBox().minY) - 1;
        int blockZ = MathHelper.floor_double(entity.posZ);
        return new BlockPos(blockX, blockY, blockZ);
    }

    public void moveBlocks(World world) {
        HashMap<BlockPos, IBlockState> unplaced = new HashMap<BlockPos, IBlockState>();
        IBlockState controllerState = null;
        BlockPos controllerPos = null;
        BlockPos ownerPos = getBlockUnderEntity(world, owner);
        if (!world.isRemote) {
            if (!checked) {
                System.out.println("Checking 'blockCache' blocks");
                if (blockCache.contains(ownerPos)) {
                    owner.posX += moveDir.getX();
                    owner.posY += moveDir.getY();
                    owner.posZ += moveDir.getZ();
                    System.out.println(moveDir);
                }
//                for (BlockPos pos : blockCache) {
                for (int i = 0; i < blockCache.size(); i++) {
                    try {
                        BlockPos pos = blockCache.get(i);
                        owner.motionY = 0;
                        IBlockState iBlockState = world.getBlockState(pos);
                        BlockPos newPos = pos.add(moveDir); //pos.up(1);
                        world.setBlockToAir(pos);
                        if (iBlockState.getBlock().getClass().getCanonicalName().equals("cethric.stuff.block.BlockMovingController")) {
                            System.out.println("Cant move controller yet...");
                            controllerState = iBlockState;
                            controllerPos = newPos;
                        } else {
                            if (iBlockState.getBlock().isFullBlock()) {
                                System.out.println("Moving Block: %s".format(iBlockState.getBlock().toString()));
                                world.setBlockState(newPos, iBlockState, 3);
                            } else {
                                unplaced.put(newPos, iBlockState);
                            }
                        }
                    } catch (ConcurrentModificationException e) {
                        LOGGER.error("Umm Error? " + e.getLocalizedMessage(), e);
                    }
                }
                System.out.println("Checking 'unplaced' blocks");
                for (BlockPos pos : unplaced.keySet()) {
                    owner.motionY = 0;
                    IBlockState iBlockState = unplaced.get(pos);
                    if (! iBlockState.getBlock().getClass().getCanonicalName().equals("net.minecraft.block.BlockAir")) {
                        if (iBlockState.getBlock() instanceof BlockLever) {
                            if (iBlockState.getValue(BlockLever.POWERED).compareTo(Boolean.valueOf(false)) > 0) {
                                System.out.println("Lever is active");
                                iBlockState = iBlockState.withProperty(BlockLever.POWERED, false);
                            }
                        }

                        System.out.println("Moving Block: %s".format(iBlockState.getBlock().toString()));
                        world.setBlockState(pos, iBlockState, 3);
                    }
                }

                if (controllerPos != null && controllerState != null) {
                    world.setBlockState(controllerPos, controllerState, 3);
                }

                System.out.println("Done");
                checked = true;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println("Could not wait to try again");
                }
            }
        }
    }
}

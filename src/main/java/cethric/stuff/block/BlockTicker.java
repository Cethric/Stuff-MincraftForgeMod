package cethric.stuff.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by blakerogan on 12/04/15.
 */
public class BlockTicker extends Block {
    private boolean active = false;
    public BlockTicker(Material materialIn) {
        super(materialIn);
    }

    @Override
    public int tickRate(World worldIn) {
        return 1;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        active = !active;
        super.updateTick(worldIn, pos, state, rand);
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }


    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        if (active) {
            return 15;
        } else {
            return 0;
        }
    }
}

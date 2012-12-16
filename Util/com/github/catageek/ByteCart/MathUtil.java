package com.github.catageek.ByteCart;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;


public final class MathUtil {

	public static int binlog( int bits ) // returns 0 for bits=0
	{
		int log = 0;
		//        if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
		//        if( bits >= 256 ) { bits >>>= 8; log += 8; }
		//        if( bits >= 16  ) { bits >>>= 4; log += 4; }
		if( bits >= 4   ) { bits >>>= 2; log += 2; }
		return log + ( bits >>> 1 );
	}

	public static final BlockFace clockwise(BlockFace b) {
		switch(b) {
		case NORTH:
			return BlockFace.EAST;
		case EAST:
			return BlockFace.SOUTH;
		case SOUTH:
			return BlockFace.WEST;
		case WEST:
			return BlockFace.NORTH;
		}
		return b;

	}

	public static final BlockFace anticlockwise(BlockFace b) {
		switch(b) {
		case NORTH:
			return BlockFace.WEST;
		case EAST:
			return BlockFace.NORTH;
		case SOUTH:
			return BlockFace.EAST;
		case WEST:
			return BlockFace.SOUTH;
		}
		return b;

	}

	public static final void forceUpdate(Block b) {
		byte oldData = b.getData();
		byte notData;
		if (oldData>1) notData = (byte)(oldData-1);
		else if (oldData<15) notData = (byte)(oldData+1);
		else notData = 0;
		b.setData(notData, true);
		b.setData(oldData, true);
	}

	public static final void loadChunkAround(World world, int x, int z) {
		int j, i = x-2, k = x+2, l = z+2;


//		long start = System.nanoTime();

		for (; i<=k; ++i) {
			for (j=z-2;  j<=l ; ++j) {
				world.loadChunk(i, j, false);
/*				if(ByteCart.debug)
					ByteCart.log.info("ByteCart: loading chunk (" + i + "," + j + ")");
*/			}
		}

/*		long end = System.nanoTime();
		if(ByteCart.debug)
			ByteCart.log.info("ByteCart: time to load chunks (millis) : "  + (end - start));
*/

	}

	public static final void unloadChunkXAxis(World world, int x, Integer z) {
		int j = z+2;
		for(int i = z-2; i<= j; ++i) {
			world.unloadChunkRequest(x, i);

		}

	}

	public static final void unloadChunkZAxis(World world, int x, Integer z) {
		int j = x+2;
		for(int i = x-2; i<= j; ++i) {
			world.unloadChunkRequest(i, z);
		}
	}

}
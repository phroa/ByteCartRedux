package com.github.catageek.ByteCart.Updaters;

import org.bukkit.block.BlockFace;

import com.github.catageek.ByteCart.Signs.BCSign;
import com.github.catageek.ByteCart.Wanderer.Wanderer;

class UpdaterResetBackbone extends UpdaterBackBone implements Wanderer {

	UpdaterResetBackbone(BCSign bc, UpdaterContent rte) {
		super(bc, rte);
	}


	@Override
	public void doAction(BlockFace to) {
		if (! this.isAtBorder())
			reset();
	}
	
	@Override
	protected BlockFace selectDirection() {
		BlockFace face;
		if ((face = manageBorder()) != null)
			return face;
		return DefaultRouterWanderer.getRandomBlockFace(getRoutingTable(), getFrom().getBlockFace());
	}


}

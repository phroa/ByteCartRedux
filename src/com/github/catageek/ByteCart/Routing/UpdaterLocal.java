package com.github.catageek.ByteCart.Routing;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.entity.Vehicle;

import com.github.catageek.ByteCart.ByteCart;
import com.github.catageek.ByteCart.CollisionManagement.SimpleCollisionAvoider.Side;
import com.github.catageek.ByteCart.HAL.CounterInventory;
import com.github.catageek.ByteCart.HAL.StackInventory;
import com.github.catageek.ByteCart.Util.DirectionRegistry;

public final class UpdaterLocal implements Updater {

	private final Vehicle Vehicle;
	private Address SignAddress;
	private final RoutingTableExchange Routes;
	private final CounterInventory Counter;
	private final StackInventory Start;
	private final StackInventory End;
	private final int SignNetmask;
	private final Level Level;
	private final BlockFace From;
	private RoutingTable RoutingTable;

	private enum counterSlot {
		REGION(16),
		RING(17);

		private final int slot;

		private counterSlot(int i) {
			this.slot = i;
		}
	}

	public UpdaterLocal(org.bukkit.entity.Vehicle vehicle,
			Address signAddress, BlockFace from, int netmask, Level level) {
		super();
		Vehicle = vehicle;
		SignAddress = signAddress;
		SignNetmask = netmask;
		Level = level;
		From = from;
		Counter = new CounterInventory(((StorageMinecart) this.getVehicle()).getInventory(), 18);
		Start = new StackInventory(((StorageMinecart) this.getVehicle()).getInventory(), 18, 5);
		End = new StackInventory(((StorageMinecart) this.getVehicle()).getInventory(), 23, 5);
		Routes = ByteCart.myPlugin.getUm().getMapRoutes().get(getVehicle().getEntityId());
		RoutingTable = null;

		// set cookie A to wait a router
		if(this.getRoutes().getCurrent() == -2) {
			this.getStart().push(1);
			this.getRoutes().setCurrent(0);
		}

	}


	public UpdaterLocal(RoutingTable routingtable,
			org.bukkit.entity.Vehicle vehicle2, Address ringAddress,
			BlockFace from2, Level level2) {
		this(vehicle2, ringAddress, from2, 0, level2);
		RoutingTable = routingtable;
	}


	@Override
	public void Update(BlockFace to) {

		int signring = this.SignAddress.getTrack().getAmount();
		// the route where we went the lesser
		int preferredroute = this.getRoutes().getMinDistance(RoutingTable, new DirectionRegistry(this.getFrom()));


		// if we are not in the good region or on ring 0, skip update
		if (this.SignAddress.getRegion().getAmount() != Routes.getRegion()
				|| signring == 0
				) {
			return;
		}

		// if this is cookie A, do nothing if this is not the route where we want to go
		if (this.getEnd().empty() ^ this.getStart().empty()
				&& this.getStart().peek() != 0) {
			preferredroute = this.getStart().peek();
			if (preferredroute != signring)
				return;
		}
		
		// mark all stations of subnets in stack as taken
		while (! this.getEnd().empty())
			this.leaveSubnet();

		//if cookie B is present, leave cookie A and return
		if (this.getEnd().empty() ^ this.getStart().empty())
			if (this.getStart().pop() == 0) {
				// Pushing the route where we want to go
				this.getStart().push(preferredroute);
				return;
			}

		// leave subnet, resetting start and end stacks (and removing cookie A)
		this.getStart().clear();
		this.getEnd().clear();


		// incrementing ring counter in the RoutingTableExchange map
		int ring = this.getCounter().getCount(counterSlot.RING.slot);
		if (this.getRoutes().hasRouteTo(ring))
			this.getRoutes().setRoute(ring, this.getRoutes().getDistance(ring) + 1);
		else
			this.getRoutes().setRoute(ring, 1);

		// updating region and counter data from sign
		this.getCounter().setCount(counterSlot.REGION.slot, this.getSignAddress().getRegion().getAmount());
		this.getCounter().setCount(counterSlot.RING.slot, this.getSignAddress().getTrack().getAmount());

		// mark station 0 as taken
		this.getCounter().incrementCount(0, 32);

		if(ByteCart.debug)
			ByteCart.log.info("ByteCart : UpdaterLocal : updating cart from sign\n" + this.getCounter());

	}

	@Override
	public void Update(Side to) {


		// wrong level or cookie still there or we did not enter the subnet
		if (this.getLevel().number != (this.getRoutes().getLevel().number & 7)
				|| this.getStart().empty() ^ this.getEnd().empty()
				|| (to.Value() != Side.RIGHT.Value() && this.getNetmask() < 4))
			return;
		// reset cart
		if (this.getRoutes().getLevel() == com.github.catageek.ByteCart.Routing.Updater.Level.RESET_LOCAL) {
			this.getSignAddress().remove();
			return;
		}


		// if sign is not consistent, rewrite it
		if (! getSignAddress().isValid() || this.needUpdate()) {
			int start;
			if ((start = this.getFreeSubnet(getNetmask())) != -1) {
				String address = "" + this.getCounter().getCount(counterSlot.REGION.slot)
						+ "." + this.getCounter().getCount(counterSlot.RING.slot)
						+ "." + start;
				this.getSignAddress().setAddress(address);

				// reload sign
				this.setSignAddress(AddressFactory.getAddress(address));

				if(ByteCart.debug)
					ByteCart.log.info("ByteCart : UpdaterLocal : Update() : rewrite sign to " + address + "(" + this.getSignAddress().toString() + ")");
			}
		}


		if (getSignAddress().isValid()) {
			if (this.getNetmask() != 4) {
				// general case
				// if we go out from the current subnet and possibly entering a new one
				if (! this.isInSubnet(getSignAddress().getStation().getAmount(), this.getNetmask()))
					leaveSubnet();

				// register new subnet start and mask
				this.getStart().push(this.getSignAddress().getStation().getAmount());
				this.getEnd().push(this.getCurrent() + (16 >> this.getNetmask()));
			}
			else
				// case of stations
				this.getCounter().incrementCount(this.getSignAddress().getStation().getAmount(), 64);
		}
	}

	@Override
	public Side giveSimpleDirection() {
		// if 
		if (this.getLevel().number == (this.getRoutes().getLevel().number & 7) && this.getNetmask() < 4
				&& ! (this.getStart().empty() ^ this.getEnd().empty()))
			return Side.RIGHT;
		return Side.LEFT;
	}

	@Override
	public BlockFace giveRouterDirection() {
		// check if we are in the good region
		if (this.SignAddress.getRegion().getAmount() != Routes.getRegion()) {
			// case this is not the right region
			try {
				return RoutingTable.getDirection(0).getBlockFace();
			} catch (NullPointerException e) {
				// no route to 0
				return this.getFrom();
			}
		}

		// there is a cookie (so it is cookie A)
		if (this.getStart().empty() ^ this.getEnd().empty()) {
			int signring = this.SignAddress.getTrack().getAmount();
			int preferredroute = this.getStart().peek();

			// if we are not arrived yet or in ring 0, we continue
			if (signring == 0 || signring != preferredroute)
				try {
					if(ByteCart.debug)
						ByteCart.log.info("not arrived, signaddress = " + signring);
					return RoutingTable.getDirection(preferredroute).getBlockFace();
				} catch (NullPointerException e) {
					// no route to ring
					return AbstractUpdater.getRandomBlockFace(RoutingTable, getFrom());
				}
		}
		else {
			// no cookie
			// check counter
			if (this.getCounter().isAllFull(0, 15)) {
				// we configured all stations
				// incrementing ring counter in the RoutingTableExchange map
				int ring = this.getCounter().getCount(counterSlot.RING.slot);
				if (this.getRoutes().hasRouteTo(ring))
					this.getRoutes().setRoute(ring, this.getRoutes().getDistance(ring) + 1);
				else
					this.getRoutes().setRoute(ring, 1);

				// reset counters
				this.getCounter().resetAll();
				//clear stacks and set cookie B
				this.getStart().clear();
				this.getEnd().clear();
				this.getStart().push(0);

				// the route where we went the lesser
				int preferredroute = this.getRoutes().getMinDistance(RoutingTable, new DirectionRegistry(getFrom()));

				try {
					return RoutingTable.getDirection(preferredroute).getBlockFace();
				} catch (NullPointerException e) {
					// no route to ring
					return AbstractUpdater.getRandomBlockFace(RoutingTable, getFrom());
				}

			}
		}
		return this.getFrom();



	}

	public void leaveSubnet() {
		if(!this.getStart().empty() && ! this.getEnd().empty()) {
			this.fillSubnet();
			this.getStart().pop();
			this.getEnd().pop();
		}
	}

	/*
	@SuppressWarnings("unused")
	private int findFreeStationNumber(int current) {
		// find a free number for the track if needed
		current = this.getCounter().firstEmpty();
		if (this.isInMask(current, this.getCurrentNetmask())) {
			return current;
		}
		return -1;
	}
	 */

	private int getFreeSubnet(int netmask) {
		boolean free;
		int start = (this.getStart().empty() ? 0 : this.getStart().peek());
		int end = (this.getEnd().empty()) ? 16 : this.getEnd().peek();
		int step = 16 >> netmask;
		if(ByteCart.debug)
			ByteCart.log.info("ByteCart : getFreeSubnet() : start = "
					+ start + " end " + end + " step = " + step + "\n" + this.getCounter().toString());
		for (int i = start; i < end; i += step) {
			free = true;
			for (int j = i; j < i + step; j++) {
				free &= (this.getCounter().getCount(j) == 0);
			}
			if (free) {
				if(ByteCart.debug)
					ByteCart.log.info("ByteCart : getFreeSubnet() : testing : " + i + " : " + free);
				return i;
			}
			if(ByteCart.debug)
				ByteCart.log.info("ByteCart : getFreeSubnet() : testing : " + i + " : " + free);
		}
		return -1;
	}

	private void fillSubnet() {
		int start = this.getCurrent();
		int end = this.getNext();
		if(ByteCart.debug)
			ByteCart.log.info("ByteCart : UpdaterLocal : fill start " + start + " end " + end);
		for (int i = start; i < end; i++)
			this.getCounter().incrementCount(i, 64);
	}


	private Level getLevel() {
		return this.Level;
	}

	private Vehicle getVehicle() {
		return Vehicle;
	}

	private Address getSignAddress() {
		return SignAddress;
	}


	private RoutingTableExchange getRoutes() {
		return Routes;
	}


	private CounterInventory getCounter() {
		return Counter;
	}


	private int getNetmask() {
		return SignNetmask;
	}

	private int getNext() {
		if (this.getEnd().empty())
			return 16;
		return this.getEnd().peek();
	}

	private int getCurrent() {
		if (this.getStart().empty())
			return 0;
		return this.getStart().peek();
	}

	private boolean isInSubnet(int address, int netmask) {
		return (address >= this.getCurrent() && (address | (15 >> netmask))  < this.getNext());
	}

	private boolean needUpdate() {
		return getSignAddress().getRegion().getAmount() != this.getCounter().getCount(counterSlot.REGION.slot)
				|| getSignAddress().getTrack().getAmount() != this.getCounter().getCount(counterSlot.RING.slot)
				|| ! isInSubnet(getSignAddress().getStation().getAmount(), this.getNetmask());
	}


	protected final BlockFace getFrom() {
		return From;
	}


	private StackInventory getStart() {
		return Start;
	}


	private StackInventory getEnd() {
		return End;
	}

	private final void setSignAddress(Address signAddress) {
		SignAddress = signAddress;
	}

}

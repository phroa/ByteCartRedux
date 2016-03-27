/**
 * ByteCart, ByteCart Redux
 * Copyright (C) Catageek
 * Copyright (C) phroa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.catageek.ByteCart.Signs;

import com.github.catageek.ByteCart.AddressLayer.TicketFactory;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * A ticket remover
 */
class BC7018 extends AbstractTriggeredSign implements Triggable, Clickable {

    BC7018(org.bukkit.block.Block block, org.bukkit.entity.Vehicle vehicle) {
        super(block, vehicle);
        // TODO Auto-generated constructor stub
    }

    BC7018(org.bukkit.block.Block block, Player player) {
        super(block, null);
        this.setInventory(player.getInventory());
    }

    @Override
    public void click() {
        try {
            this.trigger();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void trigger() throws ClassNotFoundException, IOException {
        TicketFactory.removeTickets(this.getInventory());
    }

    @Override
    public String getName() {
        return "BC7018";
    }

    @Override
    public String getFriendlyName() {
        return "Remove Ticket";
    }
}

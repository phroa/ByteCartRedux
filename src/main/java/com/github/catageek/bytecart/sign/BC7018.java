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
package com.github.catageek.bytecart.sign;

import com.github.catageek.bytecart.address.TicketFactory;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

import java.io.IOException;

/**
 * A ticket remover
 */
class BC7018 extends AbstractTriggeredSign implements Triggerable, Clickable {

    BC7018(BlockSnapshot block, Entity vehicle) {
        super(block, vehicle);
    }

    BC7018(BlockSnapshot block, Player player) {
        super(block, null);
        this.setInventory(player.getInventory());
    }

    @Override
    public void click() {
        try {
            this.trigger();
        } catch (ClassNotFoundException | IOException e) {
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

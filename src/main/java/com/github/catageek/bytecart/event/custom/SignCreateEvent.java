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
package com.github.catageek.bytecart.event.custom;

import com.github.catageek.bytecart.hardware.IC;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;


/**
 * Event triggered when a player creates a valid sign.
 */
public class SignCreateEvent extends BCEvent {

    private final Player player;
    private final String[] strings;

    public SignCreateEvent(IC ic, Player player, String[] strings) {
        super(ic);
        this.player = player;
        this.strings = strings;
    }

    /**
     * @return the player that created the block
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the lines of the sign
     */
    public String[] getStrings() {
        return strings;
    }

    @Override
    public Cause getCause() {
        return Cause.source(player).build();
    }
}

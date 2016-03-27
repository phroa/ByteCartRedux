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
package com.github.catageek.ByteCart.CollisionManagement;


public interface IntersectionSide {

    /**
     * Position of the T cross-roads.
     */
    enum Side {
        LEVER_ON(3),
        LEVER_OFF(0);

        private int Value;

        Side(int b) {
            Value = b;
        }

        public int Value() {
            return Value;
        }

        public Side opposite() {
            if (this.equals(LEVER_OFF)) {
                return LEVER_ON;
            }
            return LEVER_OFF;
        }
    }

}

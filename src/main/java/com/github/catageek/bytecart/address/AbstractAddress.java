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
package com.github.catageek.bytecart.address;


/**
 * Abstract class implementing basic operations on address
 *
 *  All address subclass must extend this class
 */
abstract class AbstractAddress implements Address {

    /**
     * A flag to tell to the world if the address should be considered as valid or not
     */
    boolean isValid = true;

    @Override
    public final boolean isValid() {
        return isValid;
    }

    /**
     * Copy the fields of the address object
     *
     * @param a the source address to copy
     * @return true if the address was copied
     */
    private boolean setAddress(Address a) {
        this.setStation(a.getStation().getValue());
        this.setIsTrain(a.isTrain());
        this.setTrack(a.getTrack().getValue());
        this.setRegion(a.getRegion().getValue());
        return this.UpdateAddress();

    }

    @Override
    public boolean setAddress(String a, String name) {
        return this.setAddress(a);
    }

    @Override
    public boolean setAddress(String s) {
        return setAddress(AddressFactory.getUnresolvedAddress(s));
    }

    @Override
    public final boolean setTrain(boolean istrain) {
        this.setIsTrain(istrain);
        return this.UpdateAddress();
    }

    @Override
    public String toString() {
        return "" + this.getRegion().getValue() + "." + this.getTrack().getValue() + "." + (this.getStation().getValue());
    }

    /**
     * flush the address to its support
     *
     *
     * @return always true
     */
    boolean UpdateAddress() {
        finalizeAddress();
        return true;
    }

    @Override
    public void finalizeAddress() {
    }

    /**
     * Set the region field
     *
     *
     * @param region the region number to set
     */
    abstract protected void setRegion(int region);

    /**
     * Set the ring field
     *
     *
     * @param track the ring number to set
     */
    abstract protected void setTrack(int track);

    /**
     * Set the station field
     *
     *
     * @param station the station number to set
     */
    abstract protected void setStation(int station);

    /**
     * Set the train flag
     *
     *
     * @param isTrain true if the flag must be set
     */
    abstract protected void setIsTrain(boolean isTrain);

    /**
     * Length (in bits) for various fields of address
     *
     * position is deprecated
     */
    protected enum Offsets {
        // length (default : 6), pos (default : 0)
        REGION(11, 0),
        TRACK(11, 0),
        STATION(8, 0),
        ISTRAIN(1, 0),
        ISRETURNABLE(1, 1),
        TTL(7, 0);

        private final int Length, Offset;

        Offsets() {
            Length = 6;
            Offset = 0;
        }

        Offsets(int length, int offset) {
            Length = length;
            Offset = offset;
        }


        /**
         * @return the length
         */
        public int getLength() {
            return Length;
        }


        /**
         * @return the offset
         */
        public int getOffset() {
            return Offset;
        }


    }
}

package com.github.catageek.ByteCart.Signs;

/**
 * An IC that defines a station should implement this
 */
public interface Station extends HasNetmask, BCSign {

    /*
     * Return the name of the station
     */
    public String getStationName();
}

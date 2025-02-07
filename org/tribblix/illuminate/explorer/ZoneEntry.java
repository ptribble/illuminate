/*
 * SPDX-License-Identifier: CDDL-1.0
 *
 * CDDL HEADER START
 *
 * This file and its contents are supplied under the terms of the
 * Common Development and Distribution License ("CDDL"), version 1.0.
 * You may only use this file in accordance with the terms of version
 * 1.0 of the CDDL.
 *
 * A full copy of the text of the CDDL should have accompanied this
 * source. A copy of the CDDL is also available via the Internet at
 * http://www.illumos.org/license/CDDL.
 *
 * CDDL HEADER END
 *
 * Copyright 2025 Peter Tribble
 *
 */

package org.tribblix.illuminate.explorer;

import java.util.Set;
import java.util.HashSet;
import org.tribblix.illuminate.InfoCommand;

/**
 * ZoneEntry - represent a zone configuration.
 * @author Peter Tribble
 * @version 1.0
 */
public class ZoneEntry {

    // common attributes
    private int zid;
    private String zname;
    private String zstate;
    private String zroot;
    private String zuuid;
    private String zbrand;
    private String ziptype;

    // attributes derived if necessary
    private Set<ZoneNet> netconfig;

    /**
     * Create a ZoneEntry to describe a zone's configuration. Basic
     * properties are as given by zoneadm list.
     *
     * @param zid the zone id
     * @param zname the zone name
     * @param zstate the zone state
     * @param zroot the zone root
     * @param zuuid the zone's uuid
     * @param zbrand the zone's brand
     * @param ziptype the zone's ip type, shared or exclusive
     */
    public ZoneEntry(int zid, String zname, String zstate, String zroot,
		String zuuid, String zbrand, String ziptype) {
	this.zid = zid;
	this.zname = zname;
	this.zstate = zstate;
	this.zroot = zroot;
	this.zuuid = zuuid;
	this.zbrand = zbrand;
	this.ziptype = ziptype;
    }

    /**
     * Return the zone's configuration.
     *
     * @return The output of zonecfg info for this zone
     */
    public String getConfig() {
	InfoCommand ic = new
	    InfoCommand("zc", "/usr/sbin/zonecfg", "-z " + zname + " info");
	return ic.getOutput();
    }

    /**
     * Return the zone's zoneid.
     *
     * @return the zone id
     */
    public int getZoneid() {
	return zid;
    }

    /**
     * Return the zone's name.
     *
     * @return the zone name
     */
    public String getName() {
	return zname;
    }

    /**
     * Return the zone's state.
     *
     * @return the zone state
     */
    public String getState() {
	return zstate;
    }

    /**
     * Return if the zone is running.
     *
     * @return true if this zone is in the running state
     */
    public boolean isRunning() {
	return "running".equals(zstate);
    }

    /**
     * Return the zone's filesystem root.
     *
     * @return the zone root
     */
    public String getRoot() {
	return zroot;
    }

    /**
     * Return the zone's UUID.
     *
     * @return the zone UUID
     */
    public String getUUID() {
	return zuuid;
    }

    /**
     * Return the zone's brand.
     *
     * @return the zone brand
     */
    public String getBrand() {
	return zbrand;
    }

    /**
     * Return the zone's ip type.
     *
     * @return the zone ip type
     */
    public String getIPtype() {
	return ziptype;
    }

    /**
     * Return if the zone network is shared-ip.
     *
     * @return true if the zone is shared-ip
     */
    public boolean isSharedIP() {
	return "shared".equals(ziptype);
    }

    /**
     * Return if the zone network is exclusive-ip.
     *
     * @return true if the zone is exclusive-ip
     */
    public boolean isExclusiveIP() {
	return "excl".equals(ziptype);
    }

    /**
     * Return a Set of zone network configurations.
     *
     * @return A Set of ZoneNet entries, one for each interface
     */
    public Set<ZoneNet> getNetworks() {
	if (netconfig == null) {
	    populateNet();
	}
	return netconfig;
    }

    /*
     * Parse the network interface specification from zonecfg
     */
    private void populateNet() {
	netconfig = new HashSet<>();
	String address = null;
	String allowedaddress = null;
	String physical = null;
	String defrouter = null;
	boolean needtosave = false;
	InfoCommand ic = new
	    InfoCommand("zc", "/usr/sbin/zonecfg", "-z " + zname + " info net");
	for (String line : ic.getOutputLines()) {
	    String[] ds = line.trim().split("\\s+", 2);
	    if (ds[0].startsWith("net:")) {
		if (needtosave) {
		    netconfig.add(new ZoneNet(address, allowedaddress,
					physical, defrouter));
		}
		needtosave = true;
		address = null;
		allowedaddress = null;
		physical = null;
		defrouter = null;
	    }
	    /*
	     * attributes have a : if defined, but not if they aren't
	     * so we search for the key without the :
	     */
	    if (ds[0].startsWith("address")) {
		address = ds[1];
	    }
	    if (ds[0].startsWith("allowed-address")) {
		allowedaddress = ds[1];
	    }
	    if (ds[0].startsWith("physical")) {
		physical = ds[1];
	    }
	    if (ds[0].startsWith("defrouter")) {
		defrouter = ds[1];
	    }
	}
	if (needtosave) {
	    netconfig.add(new ZoneNet(address, allowedaddress,
				physical, defrouter));
	}
    }
}

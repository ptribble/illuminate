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

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import uk.co.petertribble.jumble.JumbleFile;

/**
 * A class to map disk devices in c#t#d#s# form to the device instances
 * such as sd used by kstats.
 * @author Peter Tribble
 * @version 1.0
 */
public final class DevicePath {

    private static final DevicePath INSTANCE = new DevicePath();
    private static Map<String, String> dMap = new HashMap<>();
    private static Map<String, String> kMap = new HashMap<>();
    private static Map<String, String> pMap = new HashMap<>();
    private static Map<String, Set<String>> altMap = new HashMap<>();

    /*
     * This builds the two primary maps. We look through /dev/dsk for
     * files of the form c#t#d#s# or c#d#s#, although in practice we
     * just look for c*s2. Each is a symlink
     * /dev/dsk/c0t0d0s2 -> ../../devices/pci@0,0/pci-ide@4/ide@0/sd@0,0:c
     * we also parse /etc/path_to_inst which has lines like
     * "/pci@0,0/pci-ide@4/ide@0/sd@0,0" 0 "sd"
     * matching these up, sd0=c0t0d0
     */
    static {
	readPathToInst();
	scanDev();
    }

    private DevicePath() {
    }

    /**
     * Returns the singleton DevicePath.
     *
     * @return  The singleton DevicePath.
     */
    public static DevicePath getInstance() {
	return INSTANCE;
    }

    /*
     * We break up a line that looks like
     * "/pci@0,0/pci-ide@4/ide@0/sd@0,0" 0 "sd"
     * by splitting on the quotes. So the device path is between
     * quotes 1 and 2, the instance is between 2 and 3, trimming whitespace,
     * and the driver is between 3 and 4.
     */
    private static void readPathToInst() {
	for (String s1: JumbleFile.getLines(new File("/etc/path_to_inst"))) {
	    String[] ds = s1.split("\"");
	    if (ds.length > 3) {
		pMap.put(ds[1], ds[3] + ds[2].trim());
	    }
	}
    }

    private static void scanDev() {
	File fdir = new File("/dev/dsk");
	for (File f : fdir.listFiles()) {
	    try {
		String devpath = f.getCanonicalPath();
		if (devpath.startsWith("/devices/")) {
		    String s = devpath.substring(8);
		    int i = s.indexOf(':');
		    // p is the device
		    String p = s.substring(0, i);
		    if (pMap.containsKey(p)) {
			String dev = f.getName();
			// the substring here is the slice or partition
			String kname = pMap.get(p) + "," + s.substring(i + 1);
			if (kMap.containsKey(kname)) {
			    addAlternate(dev, kMap.get(kname));
			}
			kMap.put(kname, dev);
			dMap.put(dev, kname);
			if (dev.endsWith("s2")) {
			    // whole disk, extract disk identifier
			    String ds = dev.substring(0, dev.length() - 2);
			    kname = pMap.get(p);
			    if (kMap.containsKey(kname)) {
				addAlternate(ds, kMap.get(kname));
			    }
			    kMap.put(kname, ds);
			    dMap.put(ds, kname);
			}
		    }
		}
	    } catch (IOException ioe) { }
	}
    }

    /*
     * We keep track of alternate entries in /dev/dsk that point back to the
     * same real device. We keep a Set of all equivalent alternate device
     * names, and keep it in a Map, using all possible names as keys.
     */
    private static void addAlternate(String dev1, String dev2) {
	if (altMap.containsKey(dev1)) {
	    altMap.get(dev1).add(dev2);
	} else {
	    Set<String> salt = new HashSet<>();
	    salt.add(dev1);
	    salt.add(dev2);
	    altMap.put(dev1, salt);
	    altMap.put(dev2, salt);
	}
	if (altMap.containsKey(dev2)) {
	    altMap.get(dev2).add(dev1);
	} else {
	    Set<String> salt = new HashSet<>();
	    salt.add(dev1);
	    salt.add(dev2);
	    altMap.put(dev1, salt);
	    altMap.put(dev2, salt);
	}
    }

    /**
     * Return a driver name corresponding to a disk or partition identifier.
     *
     * @param device the name of the disk or partition to return the name of
     * the driver for
     *
     * @return the driver name matching the given device, or null if no such
     * driver can be found
     */
    public String getDriverName(String device) {
	return dMap.get(device);
    }

    /**
     * Return a disk or partition name corresponding to a driver identifier.
     *
     * @param driver the name of the driver to return the name of the disk or
     * partition for
     *
     * @return the disk or partition matching the given driver
     */
    public String getDiskName(String driver) {
	return kMap.get(driver);
    }

    /**
     * Return all known disk or partition names corresponding to a driver
     * identifier.
     *
     * @param driver the name of the driver to return the name of the disk or
     * partition for
     *
     * @return the Set of disk or partition names matching the given driver
     */
    public Set<String> getDiskNames(String driver) {
	return altMap.get(getDiskName(driver));
    }
}

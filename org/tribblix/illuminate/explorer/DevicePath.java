/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
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
public class DevicePath {

    private static final DevicePath INSTANCE = new DevicePath();
    private static Map <String, String> dMap = new HashMap <String, String> ();
    private static Map <String, String> kMap = new HashMap <String, String> ();
    private static Map <String, String> pMap = new HashMap <String, String> ();
    private static Map <String, Set <String>> altMap =
	new HashMap <String, Set <String>> ();

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
    static private void readPathToInst() {
	for (String s1: JumbleFile.getLines(new File("/etc/path_to_inst"))) {
	    String[] ds = s1.split("\"");
	    if (ds.length > 3) {
		pMap.put(ds[1], ds[3]+ds[2].trim());
	    }
	}
    }

    static private void scanDev() {
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
			String kname = pMap.get(p) + "," + s.substring(i+1);
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
	    } catch (IOException ioe) {}
	}
    }

    /*
     * We keep track of alternate entries in /dev/dsk that point back to the
     * same real device. We keep a Set of all equivalent alternate device
     * names, and keep it in a Map, using all possible names as keys.
     */
    static private void addAlternate(String dev1, String dev2) {
	if (altMap.containsKey(dev1)) {
	    altMap.get(dev1).add(dev2);
	} else {
	    Set <String> salt = new HashSet <String> ();
	    salt.add(dev1);
	    salt.add(dev2);
	    altMap.put(dev1, salt);
	    altMap.put(dev2, salt);
	}
	if (altMap.containsKey(dev2)) {
	    altMap.get(dev2).add(dev1);
	} else {
	    Set <String> salt = new HashSet <String> ();
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
    public Set <String> getDiskNames(String driver) {
	return altMap.get(getDiskName(driver));
    }
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import uk.co.petertribble.jumble.JumbleFile;

/**
 * Parse and extract data from the /etc/mnttab file.
 * @author Peter Tribble
 * @version 1.0
 */
public final class Mnttab {

    private Map<String, String> devmap = new HashMap<>();
    private Map<String, String> fsmap = new HashMap<>();
    private Map<String, String> fstypemap = new HashMap<>();
    private Map<String, List<String>> optmap = new HashMap<>();
    private Set<String> fsList = new HashSet<>();
    private long modified;

    /**
     * Parse the list of mounted filesystems.
     */
    public Mnttab() {
	update();
    }

    /**
     * Update the list of mount entries. If the mnttab hasn't been modified,
     * then does nothing, else parses the mnttab completely and adds any new
     * entries. Old entries are not removed: this class is not designed to be
     * used to enumerate filesystems but to store their properties, and we
     * keep old entries in case consumers haven't updated their lists.
     */
    public void update() {
	File f = new File("/etc/mnttab");
	if (f.lastModified() == modified) {
	    return;
	}
	modified = f.lastModified();
	for (String line : JumbleFile.getLines(f)) {
	    // line is: [0]device [1]name [2]type [3]options [4]timestamp
	    String[] st = line.split("\\s+");
	    // parsed, add to data structures
	    fsmap.put(st[0], st[1]);
	    devmap.put(st[1], st[0]);
	    fstypemap.put(st[1], st[2]);
	    optmap.put(st[1], Arrays.asList(st[3].split(",")));
	    fsList.add(st[1]);
	}
    }

    /**
     * Returns the String representation of the device for the given filesystem.
     *
     * @param fs The filesystem of interest
     *
     * @return The device the given filesystem is mounted on
     */
    public String getDevice(String fs) {
	return devmap.get(fs);
    }

    /**
     * Returns the String representation of the filesystem for the given device.
     * Multiple filesystems may be mounted on the same device (for example
     * swap), and in that case only one is returned.
     *
     * @param device The device of interest
     *
     * @return The filesystem mounted on the given device
     */
    public String getFs(String device) {
	return fsmap.get(device);
    }

    /**
     * Returns the String representation of the filesystem type of the given
     * filesystem.
     *
     * @param fs The filesystem of interest
     *
     * @return The type of the given filesystem
     */
    public String getFsType(String fs) {
	return fstypemap.get(fs);
    }

    /**
     * Returns a List of Strings which are the mount options for the given
     * filesystem.
     *
     * @param fs The filesystem of interest
     *
     * @return The mount options of the given filesystem
     */
    public List<String> getOptions(String fs) {
	return optmap.get(fs);
    }

    /**
     * Find the mount option that matches the key and return its
     * value. Mount options are either single words, or pairs of
     * words separated by an = sign. If the mount option is not
     * present, return null.
     *
     * @param fs The filesystem of interest
     * @param sopt The mount option of interest
     *
     * @return The value of the requested mount option of the given filesystem
     */
    private String getOptionValue(String fs, String sopt) {
	String sval = null;
	for (String si : getOptions(fs)) {
	    if (si.startsWith(sopt)) {
		int i = si.indexOf('=');
		if (i > 0) {
		    sval = si.substring(i + 1);
		}
	    }
	}
	return sval;
    }

    /**
     * Return the zone the filesystem is mounted in. If not specified, return
     * the literal String "global".
     *
     * @param fs The filesystem of interest
     *
     * @return The name of the zone that the given filesystem is mounted in.
     */
    public String getZoneName(String fs) {
	String sz = getOptionValue(fs, "zone");
	return (sz == null) ? "global" : sz;
    }

    /**
     * Return the device id of the given filesystem.
     *
     * @param fs The filesystem of interest
     *
     * @return The device id of the given filesystem
     */
    public String getDeviceID(String fs) {
	return getOptionValue(fs, "dev");
    }

    /**
     * Return true or false depending on whether the filesystem has
     * the ignore flag set. If no filesystem matches, return false.
     *
     * @param fs The filesystem of interest
     *
     * @return Whether the given filesystem has the ignore flag set
     */
    public boolean getIgnore(String fs) {
	List<String> v = optmap.get(fs);
	return v != null && v.contains("ignore");
    }

    /**
     * Return the first filesystem with the given deviceid. Prefers a non-lofs
     * filesystem if one is available.
     *
     * @param s A device id
     *
     * @return The name of the first filesystem with the given device id
     */
    public String getFSforDevice(String s) {
	for (String fs : fsList) {
	    if (!"lofs".equals(getFsType(fs)) && s.equals(getDeviceID(fs))) {
		return fs;
	    }
	}
	for (String fs : fsList) {
	    if (s.equals(getDeviceID(fs))) {
		return fs;
	    }
	}
	// we didn't find it
	return null;
    }

    /**
     * Return a list of zones that have filesystems mounted.
     *
     * @return A List of zones that have mounted filesystems
     */
    public List<String> getZoneList() {
	List<String> v = new ArrayList<>();
	for (String fs : fsList) {
	    String zn = getZoneName(fs);
	    if (!v.contains(zn)) {
		v.add(zn);
	    }
	}
	return v;
    }

    /**
     * Return a list of filesystems in a given zone.
     *
     * @param myzone The zone name to be queried
     *
     * @return A List of filesystems that the given zone has mounted
     */
    public List<String> getFSforZone(String myzone) {
	List<String> v = new ArrayList<>();
	for (String fs : fsList) {
	    if (getZoneName(fs).equals(myzone)) {
		v.add(fs);
	    }
	}
	return v;
    }

    /**
     * Return a list of mounted fstypes.
     *
     * @return A List of filesystem types that are currently mounted
     */
    public List<String> getFstypeList() {
	List<String> v = new ArrayList<>();
	for (String s : fstypemap.values()) {
	    if (!v.contains(s)) {
		v.add(s);
	    }
	}
	return v;
    }

    /**
     * Return a list of filesystems of the given fstype.
     *
     * @param ftype The requested filesystem type
     *
     * @return A List of filesystems of the given filesystem type
     */
    public List<String> getFSforFstype(String ftype) {
	List<String> v = new ArrayList<>();
	for (Map.Entry<String, String> entry : fstypemap.entrySet()) {
	    if (entry.getValue().equals(ftype)) {
		v.add(entry.getKey());
	    }
	}
	return v;
    }

    /**
     * Return a list of device ids of the given fstype.
     *
     * @param ftype The requested filesystem type
     *
     * @return A List of device IDs for the given filesystem type
     */
    public List<String> getIDforFstype(String ftype) {
	List<String> v = new ArrayList<>();
	for (Map.Entry<String, String> entry : fstypemap.entrySet()) {
	    if (entry.getValue().equals(ftype)) {
		String dev = devmap.get(entry.getKey());
		if (!v.contains(dev)) {
		    v.add(dev);
		}
	    }
	}
	return v;
    }
}

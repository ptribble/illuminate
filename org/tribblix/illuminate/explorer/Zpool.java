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
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import org.tribblix.illuminate.InfoCommand;

/**
 * Zpool - represent the configuration of ZFS.
 * @author Peter Tribble
 * @version 1.0
 */
public class Zpool {

    private String name;
    private Set<Zfilesys> zfilesys = new HashSet<>();
    private Set<Zvolume> zvolumes = new HashSet<>();
    private Zfilesys zparent;
    private Map<String, Zfilesys> zmap = new HashMap<>();
    private Set<String> zdevices = new HashSet<>();

    /**
     * Create a Zpool object representing a ZFS pool.
     *
     * @param name The name of the pool.
     */
    public Zpool(String name) {
	this.name = name;
	addComponents();
	parseZfs();
	relateFs();
    }

    /*
     * Read zpool status to find a list of disks or partitions that the pool
     * contains. At the moment, just identifies disks or partitions, and
     * doesn't attempt to do anything other than work out they're in use.
     *
     * FIXME defer until information is requested, or run in a SwingWorker
     */
    private void addComponents() {
	InfoCommand ic = new InfoCommand("ZP", "/usr/sbin/zpool",
						"status " + name);
	if (ic.exists()) {
	    for (String line : ic.getOutputLines()) {
		String[] ds = line.trim().split("\\s+");
		File f = new File("/dev/dsk", ds[0]);
		if (f.exists() && !f.isDirectory()) {
		    zdevices.add(ds[0]);
		}
	    }
	}
    }

    /*
     * Parsing zfs output: format is
     * NAME    USED  AVAIL  REFER  MOUNTPOINT
     * peter  91.5K  1.95G    40K  /peter
     *
     * Walk through filesystem and volume lists separately
     */
    private void parseZfs() {
	InfoCommand ic = new InfoCommand("ZF", "/usr/sbin/zfs",
					"list -H -t filesystem -r " + name);
	if (ic.exists()) {
	    for (String line : ic.getOutputLines()) {
		String[] ds = line.split("\\s+");
		// ignore any lines that aren't right, including empty lines
		if (ds.length == 5) {
		    Zfilesys zfs = new Zfilesys(ds[0]);
		    zfilesys.add(zfs);
		    zmap.put(ds[0], zfs);
		}
	    }
	}
	ic = new InfoCommand("ZF", "/usr/sbin/zfs",
					"list -H -t volume -r " + name);
	if (ic.exists()) {
	    for (String line : ic.getOutputLines()) {
		String[] ds = line.split("\\s+");
		// ignore any lines that aren't right, including empty lines
		if (ds.length == 5) {
		    Zvolume zfs = new Zvolume(ds[0]);
		    zvolumes.add(zfs);
		    // FIXME zmap.put(ds[0], zfs);
		}
	    }
	}
    }

    /*
     * Walk through the datasets to define relationships.
     *
     * If it contains a / then strip off one level and add it to its parent.
     * The top-level filesystem is recorded separately as the parent.
     */
    private void relateFs() {
	for (Zfilesys zfs : zfilesys) {
	    String zname = zfs.getName();
	    // regular dataset
	    int j = zname.lastIndexOf('/');
	    if (j >= 0) {
		Zfilesys zp = zmap.get(zname.substring(0, j));
		zp.addChild(zfs);
	    } else {
		// no separators, must be top-level
		zparent = zfs;
	    }
	}
    }

    /**
     * Return the name of this ZFS pool.
     *
     * @return the name of this zpool
     */
    public String getName() {
	return name;
    }

    /**
     * Return the set of known filesystems.
     *
     * @return a Set of all ZFS filesystems
     */
    public Set<Zfilesys> filesystems() {
	return zfilesys;
    }

    /**
     * Return the set of known volumes.
     *
     * @return a Set of all ZFS volumes
     */
    public Set<Zvolume> volumes() {
	return zvolumes;
    }

    /**
     * Return the top-level filesystem. This will be the filesystem with
     * the same name as the pool.
     *
     * @return the top-level ZFS filesystem
     */
    public Zfilesys getParent() {
	return zparent;
    }

    /**
     * Returns whether a device of the given name is a component in this pool.
     *
     * @param s the name of the device to check
     *
     * @return true if the pool contains a device of the given name
     */
    public boolean contains(String s) {
	return zdevices.contains(s);
    }
}

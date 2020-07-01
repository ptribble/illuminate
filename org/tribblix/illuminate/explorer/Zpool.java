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
    private Set <Zfilesys> zfilesys;
    private Set <Zfilesys> zparents;
    private Map <String, Zfilesys> zmap;
    private Set <String> zdevices;

    /**
     * Create a Zpool object representing a ZFS pool.
     *
     * @param name The name of the pool.
     */
    public Zpool(String name) {
	this.name = name;
	zfilesys = new HashSet <Zfilesys> ();
	zparents = new HashSet <Zfilesys> ();
	zmap = new HashMap <String, Zfilesys> ();
	zdevices = new HashSet <String> ();
	add_components();
	parse_zfs();
	relate_fs();
    }

    /*
     * Read zpool status to find a list of disks or partitions that the pool
     * contains. At the moment, just identifies disks or partitions, and
     * doesn't attempt to do anything other than work out they're in use.
     *
     * FIXME defer until information is requested, or run in a SwingWorker
     */
    private void add_components() {
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
     */
    private void parse_zfs() {
	InfoCommand ic = new InfoCommand("ZF", "/usr/sbin/zfs",
						"list -H -r " + name);
	if (ic.exists()) {
	    for (String line : ic.getOutputLines()) {
		String[] ds = line.split("\\s+");
		if (ds.length == 5) {
		    Zfilesys zfs = new Zfilesys(ds[0]);
		    zfilesys.add(zfs);
		    zmap.put(ds[0], zfs);
		} else {
		    System.out.println("Unable to parse zfs output");
		    System.out.println(line);
		}
	    }
	}
    }

    /*
     * Walk through the datasets to define relationships.
     *
     * If it contains an @ then it's a snapshot and is added to its parent
     * else we just strip off one level and add it to the parent. Top-level
     * filesystems are put in the zparents Set.
     */
    private void relate_fs() {
	for (Zfilesys zfs : zfilesys) {
	    String name = zfs.getName();
	    int i = name.indexOf('@');
	    if (i >= 0) {
		// it's a snapshot
		Zfilesys zp = zmap.get(name.substring(0, i));
		zp.addSnapshot(zfs);
	    } else {
		// regular dataset
		int j = name.lastIndexOf('/');
		if (j >= 0) {
		    Zfilesys zp = zmap.get(name.substring(0, j));
		    zp.addChild(zfs);
		} else {
		    // no separators, must be top-level
		    zparents.add(zfs);
		}
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
    public Set <Zfilesys> filesystems() {
	return zfilesys;
    }

    /**
     * Return the set of parent filesystems. These are the top-level
     * filesystems, so does not include snapshots or child datasets.
     *
     * @return a Set of top-level ZFS filesystems
     */
    public Set <Zfilesys> parents() {
	return zparents;
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

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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import org.tribblix.illuminate.InfoCommand;

/**
 * Zfilesys - represent a ZFS filesystem.
 * @author Peter Tribble
 * @version 1.0
 */
public class Zfilesys {

    private String name;
    private Map <String, String> propmap;
    private Set <Zfilesys> children;
    private Set <Zfilesys> snapshots;

    /**
     * Create a new Zfilesys object, to store details of a ZFS filesystem (or,
     * more generally, dataset).
     *
     * @param name the name of the ZFS dataset
     */
    public Zfilesys(String name) {
	this.name = name;
	children = new HashSet <Zfilesys> ();
	snapshots = new HashSet <Zfilesys> ();
    }

    /**
     * Return the name of this filesystem.
     *
     * @return the name of the dataset described by this Zfilesys
     */
    public String getName() {
	return name;
    }

    /**
     * Return the entire property map.
     *
     * @return a Map containing the properties of this Zfilesys
     */
    public Map <String, String> getProperties() {
	if (propmap == null) {
	    propmap = new HashMap <String, String> ();
	    InfoCommand ic = new InfoCommand("ZP", "/usr/sbin/zfs",
						"get -Hp all " + name);
	    if (ic.exists()) {
		for (String line : ic.getOutputLines()) {
		    String[] ds = line.split("\\s+");
		    propmap.put(ds[1], ds[2]);
		}
	    }
	}
	return propmap;
    }

    /**
     * Retrieve the value of the specified property.
     *
     * @param key the name of the property to retrieve
     *
     * @return the value of the specified property
     */
    public String getProperty(String key) {
	return getProperties().get(key);
    }

    /**
     * Add a child dataset.
     *
     * @param zfs the child dataset to add
     */
    public void addChild(Zfilesys zfs) {
	children.add(zfs);
    }

    /**
     * Add a snapshot.
     *
     * @param zfs the snapshot to add
     */
    public void addSnapshot(Zfilesys zfs) {
	snapshots.add(zfs);
    }

    /**
     * Get the Set of all child datasets. Note that this class does not create
     * or manage the dataset relationships, assuming that Zpool does all
     * the work.
     *
     * @return the Set of all child datasets
     */
    public Set <Zfilesys> children() {
	return children;
    }

    /**
     * Get the Set of all snapshots. Note that this class does not create
     * or manage the dataset relationships, assuming that Zpool does all
     * the work.
     *
     * @return the Set of all child datasets
     */
    public Set <Zfilesys> snapshots() {
	return snapshots;
    }

    /**
     * Return the String representation of this ZFS filesystem, its name.
     *
     * @return the name of this dataset
     */
    @Override
    public String toString() {
	return name;
    }
}

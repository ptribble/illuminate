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

import java.util.Set;
import java.util.HashSet;
import org.tribblix.illuminate.InfoCommand;

/**
 * Zfilesys - represent a ZFS filesystem.
 * @author Peter Tribble
 * @version 1.1
 */
public class Zfilesys extends Zdataset {

    private String shortname;
    private Set<Zfilesys> children = new HashSet<>();
    private Set<Zsnapshot> snapshots;

    /**
     * Create a new Zfilesys object, to store details of a ZFS filesystem.
     *
     * @param name the name of the ZFS filesystem
     */
    public Zfilesys(String name) {
	this.name = name;
	int j = name.lastIndexOf('/');
	if (j >= 0) {
	    shortname = name.substring(j + 1);
	} else {
	    shortname = name;
	}
    }

    /**
     * Return the last component of the name of this filesystem.
     *
     * @return the last component of the name of the filesystem described by
     * this Zfilesys
     */
    public String getShortName() {
	return shortname;
    }

    /**
     * Add a child filesystem.
     *
     * @param zfs the child filesystem to add
     */
    public void addChild(Zfilesys zfs) {
	children.add(zfs);
    }

    /**
     * Get the Set of all child filesystems. Note that this class does not
     * create or manage the dataset relationships, assuming that Zpool does
     * all the work.
     *
     * @return the Set of all child filesystems
     */
    public Set<Zfilesys> getChildren() {
	return children;
    }

    /**
     * Get the Set of all snapshots.
     *
     * @return the Set of all snapshots of this filesystem
     */
    public Set<Zsnapshot> getSnapshots() {
	if (snapshots == null) {
	    snapshots = new HashSet<>();
	    InfoCommand ic = new InfoCommand("ZF", "/usr/sbin/zfs",
					"list -H -t snapshot -d 1 -r " + name);
	    if (ic.exists()) {
		for (String line : ic.getOutputLines()) {
		    String[] ds = line.split("\\s+");
		    snapshots.add(new Zsnapshot(ds[0]));
		}
	    }
	}
	return snapshots;
    }
}

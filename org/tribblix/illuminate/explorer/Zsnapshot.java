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
import org.tribblix.illuminate.InfoCommand;

/**
 * Zsnapshot - represent a ZFS snapshot.
 * @author Peter Tribble
 * @version 1.0
 */
public class Zsnapshot {

    private String name;
    private Map <String, String> propmap;

    /**
     * Create a new Zsnapshot object, to store details of a ZFS snapshot
     *
     * @param name the name of the ZFS snapshot
     */
    public Zsnapshot(String name) {
	this.name = name;
    }

    /**
     * Return the name of this filesystem.
     *
     * @return the name of the snapshot described by this Zsnapshot
     */
    public String getName() {
	return name;
    }

    /**
     * Return the entire property map.
     *
     * @return a Map containing the properties of this Zsnapshot
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
     * Return the String representation of this ZFS filesystem, its name.
     *
     * @return the name of this dataset
     */
    @Override
    public String toString() {
	return name;
    }
}

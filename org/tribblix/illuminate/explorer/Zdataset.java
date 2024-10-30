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
import java.util.Map;
import java.util.HashMap;
import org.tribblix.illuminate.InfoCommand;

/**
 * Zdataset - represent a ZFS dataset.
 * @author Peter Tribble
 * @version 1.0
 */
public class Zdataset {

    protected String name;
    protected Set<Zproperty> propset;
    protected Map<String, Zproperty> propmap;

    /**
     * Return the name of this dataset.
     *
     * @return the name of the dataset described by this Zdataset
     */
    public String getName() {
	return name;
    }

    /*
     * Initialize all the properties if we haven't already done so.
     */
    private void initProperties() {
	if (propmap == null) {
	    propset = new HashSet<>();
	    propmap = new HashMap<>();
	    InfoCommand ic = new InfoCommand("ZP", "/usr/sbin/zfs",
				"get -o property,value,source -Hp all " + name);
	    if (ic.exists()) {
		for (String line : ic.getOutputLines()) {
		    String[] ds = line.split("\t", 3);
		    Zproperty zp = new Zproperty(ds[0], ds[1], ds[2]);
		    propset.add(zp);
		    propmap.put(ds[0], zp);
		}
	    }
	}
    }

    /**
     * Return all the properties.
     *
     * @return a Set of the properties of this dataset
     */
    public Set<Zproperty> getProperties() {
	initProperties();
	return propset;
    }

    /**
     * Retrieve the value of the specified property.
     *
     * @param key the name of the property to retrieve
     *
     * @return the requested property
     */
    public Zproperty getProperty(String key) {
	initProperties();
	return propmap.get(key);
    }

    /**
     * Return the String representation of this ZFS dataset, its name.
     *
     * @return the name of this dataset
     */
    @Override
    public String toString() {
	return name;
    }
}

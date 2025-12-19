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
import java.util.Map;
import java.util.HashMap;
import org.tribblix.illuminate.InfoCommand;

/**
 * Zdataset - represent a ZFS dataset.
 * @author Peter Tribble
 * @version 1.0
 */
public class Zdataset {

    /**
     * The name of this dataset.
     */
    protected String name;
    /**
     * A set of all this dataset's properties.
     */
    protected Set<Zproperty> propset;
    /**
     * This dataset's properties, keyed by name.
     */
    protected Map<String, Zproperty> propmap;

    /**
     * Set the name of this dataset.
     *
     * @param newname the name of the dataset described by this Zdataset
     */
    public void setName(final String newname) {
	name = newname;
    }

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
    public Zproperty getProperty(final String key) {
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

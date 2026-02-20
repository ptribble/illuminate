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
 * Copyright 2026 Peter Tribble
 *
 */

package org.tribblix.illuminate.explorer;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import org.tribblix.illuminate.InfoCommand;

/**
 * ZoneConfig - represent the list of zones and their configuration.
 * @author Peter Tribble
 * @version 1.0
 */
public final class ZoneConfig {
    private static final ZoneConfig INSTANCE = new ZoneConfig();

    private final Set<String> zones = new HashSet<>();
    private final Map<String, ZoneEntry> zmap = new HashMap<>();
    private boolean isglobal;

    private ZoneConfig() {
	parseZones();
    }

    /**
     * Get the singleton ZoneConfig instance.
     *
     * @return the singleton ZoneConfig instance
     */
    public static ZoneConfig getInstance() {
	return INSTANCE;
    }

    /*
     * Get the list of zones, ignore global.
     * Populate the ZoneEntry map;
     */
    private void parseZones() {
	InfoCommand ic =
	    new InfoCommand("ZL", "/usr/sbin/zoneadm", "list -icp");
	if (ic.exists()) {
	    for (String line : ic.getOutputLines()) {
		String[] lentries = line.split(":");
		int izone;
		/*
		 * If the zone is running it has a numeric ID
		 * but if not, it gets "-", catch that and set
		 * the zoneid (which will not be used) to -1
		 */
		try {
		    izone = Integer.parseInt(lentries[0]);
		} catch (NumberFormatException nfe) {
		    izone = -1;
		}
		if ("global".equals(lentries[1])) {
		    isglobal = true;
		} else {
		    zones.add(lentries[1]);
		    zmap.put(lentries[1], new ZoneEntry(
				izone,
				lentries[1],
				lentries[2],
				lentries[3],
				lentries[4],
				lentries[5],
				lentries[6]));
		}
	    }
	}
    }

    /**
     * Return the list of available zones.
     *
     * @return a Set of all zone names
     */
    public Set<String> names() {
	return zones;
    }

    /**
     * Return the number of available zones.
     *
     * @return the number of available (non-global) zones
     */
    public int size() {
	return zones.size();
    }

    /**
     * Return whether we're running in the global zone. If we're in the global
     * zone, then there's a zone called global that is not added to the list
     * of zones.
     *
     * @return true if run in the global zone
     */
    public boolean isGlobalZone() {
	return isglobal;
    }

    /**
     * Return the ZoneEntry for the given zone.
     *
     * @param zname The name of the zone of interest
     *
     * @return the named zone's ZoneEntry
     */
    public ZoneEntry getZoneEntry(final String zname) {
	return zmap.get(zname);
    }
}

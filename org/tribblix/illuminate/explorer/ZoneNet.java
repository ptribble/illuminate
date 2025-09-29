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

/**
 * ZoneNet - represent the net configuration of a zone.
 * @author Peter Tribble
 * @version 1.0
 */
public class ZoneNet {

    private String address;
    private String allowedaddress;
    private String physical;
    private String defrouter;

    /**
     * Create a ZoneNet object to describe the configuration of a zone's
     * network interface.
     *
     * @param ipaddress the IP address of the interface
     * @param allowedip the address the interface is restricted to
     * @param ifname the name of the interface
     * @param router the default router associated with this interface
     */
    public ZoneNet(final String ipaddress, final String allowedip,
		   final String ifname, final String router) {
	address = ipaddress;
	allowedaddress = allowedip;
	physical = ifname;
	defrouter = router;
    }

    /**
     * Return the IP address.
     *
     * @return the address of the net interface
     */
    public String getAddress() {
	return address;
    }

    /**
     * Return the allowed address.
     *
     * @return the allowed address of the net interface
     */
    public String getAllowedAddress() {
	return allowedaddress;
    }

    /**
     * Return the physical interface.
     *
     * @return the name of the net interface
     */
    public String getPhysical() {
	return physical;
    }

    /**
     * Return the default router.
     *
     * @return the defrouter of the net interface
     */
    public String getDefrouter() {
	return defrouter;
    }
}

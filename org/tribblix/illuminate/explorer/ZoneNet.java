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

/**
 * ZoneNet - represent the net configuration of a zone
 * @author Peter Tribble
 * @version 1.0
 */
public class ZoneNet {

    private String address;
    private String allowedaddress;
    private String physical;
    private String defrouter;

    public ZoneNet(String address, String allowedaddress, String physical,
			String defrouter) {
	this.address = address;
	this.allowedaddress = allowedaddress;
	this.physical = physical;
	this.defrouter = defrouter;
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

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

import java.util.Map;
import java.util.HashMap;
import uk.co.petertribble.jkstat.api.Kstat;

/**
 * SysItem - represent an item of hardware.
 * @author Peter Tribble
 * @version 1.3
 */
public final class SysItem {

    /*
     * The following are types of items
     */

    /**
     * Represents the host.
     */
    public static final int HOST = 1;

    /**
     * Represents a cpu container.
     */
    public static final int CPU_CONTAINER = 101;
    /**
     * Represents a cpu.
     */
    public static final int CPU = 102;
    /**
     * Represents a cpu core.
     */
    public static final int CPU_CORE = 103;
    /**
     * Represents a cpu thread.
     */
    public static final int CPU_THREAD = 104;

    /**
     * Represents a disk container.
     */
    public static final int DISK_CONTAINER = 201;
    /**
     * Represents a disk.
     */
    public static final int DISK = 202;
    /**
     * Represents a disk partition (slice).
     */
    public static final int DISK_PARTITION = 203;
    /**
     * Represents disk I/O.
     */
    public static final int DISK_IO = 206;

    /**
     * Represents a network container.
     */
    public static final int NET_CONTAINER = 301;
    /**
     * Represents a network interface.
     */
    public static final int NET_INTERFACE = 302;
    /**
     * Represents a network protocol.
     */
    public static final int NET_PROTOCOL = 303;
    /**
     * Represents the IP protocol.
     */
    public static final int NET_PROTO_IP = 304;
    /**
     * Represents the TCP protocol.
     */
    public static final int NET_PROTO_TCP = 305;
    /**
     * Represents the UDP protocol.
     */
    public static final int NET_PROTO_UDP = 306;
    /**
     * Represents the SCTP protocol.
     */
    public static final int NET_PROTO_SCTP = 307;
    /**
     * Represents a network statistic.
     */
    public static final int NET_STAT = 308;
    /**
     * Represents a dladm object.
     */
    public static final int NET_DLADM = 310;
    /**
     * Represents a dladm physical interface.
     */
    public static final int NET_DL_PHYS = 311;
    /**
     * Represents a dladm link.
     */
    public static final int NET_DL_LINK = 312;
    /**
     * Represents a dladm link property.
     */
    public static final int NET_DL_LINKPROP = 313;
    /**
     * Represents a dladm vnic.
     */
    public static final int NET_DL_VNIC = 314;
    /**
     * Represents a dladm etherstub.
     */
    public static final int NET_DL_ETHERSTUB = 315;
    /**
     * Represents a dladm aggregation.
     */
    public static final int NET_DL_AGGR = 316;
    /**
     * Represents an ipadm object.
     */
    public static final int NET_IPADM = 320;
    /**
     * Represents an ipadm interface.
     */
    public static final int NET_IP_IF = 321;
    /**
     * Represents an ipadm interface property.
     */
    public static final int NET_IP_IFPROP = 322;
    /**
     * Represents an ipadm address.
     */
    public static final int NET_IP_ADDR = 323;
    /**
     * Represents an ipadm address property.
     */
    public static final int NET_IP_ADDRPROP = 324;
    /**
     * Represents an ipadm property.
     */
    public static final int NET_IP_PROP = 325;
    /**
     * Represents routing.
     */
    public static final int NET_ROUTE = 330;
    /**
     * Represents the route table.
     */
    public static final int NET_ROUTE_TABLE = 331;
    /**
     * Represents the routeadm state.
     */
    public static final int NET_ROUTE_ADM = 332;

    /**
     * Represents a memory container.
     */
    public static final int MEM_CONTAINER = 401;
    /**
     * Represents a kmem_cache widget.
     */
    public static final int MEM_KMEM = 402;
    /**
     * Represents an arcstat widget.
     */
    public static final int MEM_ARCSTAT = 403;

    /**
     * Represents a filesystem.
     */
    public static final int FS_CONTAINER = 501;
    /**
     * Represents an fsstat widget.
     */
    public static final int FS_FSSTAT = 502;
    /**
     * Represents a ZFS container.
     */
    public static final int ZFS_CONTAINER = 503;
    /**
     * Represents a ZFS pool.
     */
    public static final int ZFS_POOL = 504;
    /**
     * Represents a ZFS file system.
     */
    public static final int ZFS_FS = 505;
    /**
     * Represents a ZFS volume.
     */
    public static final int ZFS_VOLUME = 506;

    /**
     * Represents a zone container.
     */
    public static final int ZONE_CONTAINER = 601;
    /**
     * Represents a zone.
     */
    public static final int ZONE_ZONE = 602;
    /**
     * Represents zone processes.
     */
    public static final int ZONE_PROC = 603;
    /**
     * Represents zone networks.
     */
    public static final int ZONE_NET = 604;
    /**
     * Represents zone statistics.
     */
    public static final int ZONE_KSTAT = 605;
    /**
     * Represents zone usage.
     */
    public static final int ZONE_USAGE = 606;

    /**
     * Represents a process container.
     */
    public static final int PROCESS_CONTAINER = 701;

    /*
     * The following are status codes, used by the cell renderer to display
     * the health or quality of this item.
     */

    /**
     * A status code indicating this item is OK.
     */
    public static final int OK = 0;
    /**
     * A status code indicating this item is FAILed.
     */
    public static final int FAIL = 1;
    /**
     * A status code indicating this item has some sort of warning.
     */
    public static final int WARN = 2;
    /**
     * A status code indicating this item's status is unknown.
     */
    public static final int UNKNOWN = 3;
    /**
     * A status code indicating a BLANK status that can be ignored..
     */
    public static final int BLANK = 99;

    private Map<String, Object> attributes;
    private int type;
    private int status = BLANK;

    private Kstat kstat;

    /**
     * A container for a hardware item.
     *
     * @param type the type of item
     */
    public SysItem(int type) {
	this.type = type;
	attributes = new HashMap<>();
    }

    /**
     * A container for a hardware item. Adds a flag, a String that will be
     * added as the attribute for key "flag"
     *
     * @param type the type of item
     * @param flag a String flag
     */
    public SysItem(int type, String flag) {
	this.type = type;
	attributes = new HashMap<>();
	addAttribute("flag", flag);
    }

    /**
     * Return the type of this SysItem.
     *
     * @return the type of this item
     */
    public int getType() {
	return type;
    }

    /**
     * Add an attribute to this item. Attributes are named by their
     * key, and can be any Object. It is the responsibility of the
     * consumer of the attribute to know what the attribute represents.
     *
     * @param key the name of the attribute
     * @param value an Object representing this attribute
     */
    public void addAttribute(String key, Object value) {
	attributes.put(key, value);
    }

    /**
     * Get the named attribute.
     *
     * @param key the name of the desired attribute
     *
     * @return the attribute of the given name
     */
    public Object getAttribute(String key) {
	return attributes.get(key);
    }

    /**
     * Set the kstat associated with this item. This is sufficiently common
     * usage that it is handled explicitly rather than as an attribute.
     *
     * @see #getKstat
     *
     * @param kstat the Kstat to be associated with this item
     */
    public void setKstat(Kstat kstat) {
	this.kstat = kstat;
    }

    /**
     * Get the kstat associated with this item.
     *
     * @see #setKstat
     *
     * @return the Kstat associated with this item
     */
    public Kstat getKstat() {
	return kstat;
    }

    /**
     * Set the status of this item.
     *
     * @see #getStatus
     *
     * @param status the new status of this item
     */
    public void setStatus(int status) {
	this.status = status;
    }

    /**
     * Get the status of this item.
     *
     * @see #setStatus
     *
     * @return the status of this item
     */
    public int getStatus() {
	return status;
    }
}

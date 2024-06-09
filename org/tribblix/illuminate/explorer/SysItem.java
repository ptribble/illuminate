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
import uk.co.petertribble.jkstat.api.Kstat;

/**
 * SysItem - represent an item of hardware.
 * @author Peter Tribble
 * @version 1.3
 */
public class SysItem {

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
    public static final int NET_DLADM_PHYS = 311;
    /**
     * Represents a dladm link.
     */
    public static final int NET_DLADM_LINK = 312;
    /**
     * Represents a dladm link property
     */
    public static final int NET_DLADM_LINKPROP = 313;
    /**
     * Represents a dladm vnic.
     */
    public static final int NET_DLADM_VNIC = 314;
    /**
     * Represents a dladm etherstub.
     */
    public static final int NET_DLADM_ETHERSTUB = 315;
    /**
     * Represents a dladm aggregation.
     */
    public static final int NET_DLADM_AGGR = 316;
    /**
     * Represents an ipadm object.
     */
    public static final int NET_IPADM = 320;
    /**
     * Represents an ipadm interface.
     */
    public static final int NET_IPADM_IF = 321;
    /**
     * Represents an ipadm interface property.
     */
    public static final int NET_IPADM_IFPROP = 322;
    /**
     * Represents an ipadm address.
     */
    public static final int NET_IPADM_ADDR = 323;
    /**
     * Represents an ipadm address property.
     */
    public static final int NET_IPADM_ADDRPROP = 324;
    /**
     * Represents an ipadm property.
     */
    public static final int NET_IPADM_PROP = 325;

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

    public static final int OK = 0;
    public static final int FAIL = 1;
    public static final int WARN = 2;
    public static final int UNKNOWN = 3;
    public static final int BLANK = 99;

    private Map <String, Object> attributes;
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
	attributes = new HashMap <String, Object> ();
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
	attributes = new HashMap <String, Object> ();
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

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

package org.tribblix.illuminate;

import java.io.File;
import java.util.Vector;
import org.tribblix.illuminate.explorer.ZoneConfig;

/**
 * InfoCommandList - a List of information commands.
 * @author Peter Tribble
 * @version 1.0
 */
public final class InfoCommandList extends Vector<InfoCommand> {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a List of available commands.
     * Note that it's actually a Vector.
     */
    public InfoCommandList() {
	boolean inglobal = ZoneConfig.getInstance().isGlobalZone();
	// add all the possible commands here
	InfoCommand ic =
	    new InfoCommand(IlluminateResources.getString("INFO.BEADM"),
		"/usr/sbin/beadm", "list");
	addCommand(ic, "beadm.8");
	if (inglobal) {
	    ic = new InfoCommand(IlluminateResources.getString("INFO.CFGADM"),
		"/usr/sbin/cfgadm");
	    addCommand(ic, "cfgadm.8");
	}
	ic = new InfoCommand(IlluminateResources.getString("INFO.CONN"),
		"/usr/bin/connstat");
	addCommand(ic, "connstat.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.CORE"),
		"/usr/bin/coreadm");
	addCommand(ic, "coreadm.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.CPUID"),
		"/usr/bin/cpuid");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.DF"),
		"/usr/sbin/df", "-kl");
	addCommand(ic, "df.8");
	if (inglobal) {
	    ic = new InfoCommand(IlluminateResources.getString("INFO.DLLED"),
		"/usr/lib/dl/dlled");
	    addCommand(ic);
	}
	ic = new InfoCommand(IlluminateResources.getString("INFO.IOSTAT"),
		"/usr/bin/iostat", "-En");
	addCommand(ic, "iostat.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.HOSTID"),
		"/usr/bin/hostid");
	addCommand(ic, "hostid.1");
	ic = new InfoCommand(IlluminateResources.getString("INFO.IFCONFIG"),
		"/usr/sbin/ifconfig", "-a");
	addCommand(ic, "ifconfig.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.INETADM"),
		"/usr/sbin/inetadm");
	addCommand(ic, "inetadm.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.ISA"),
		"/usr/bin/isainfo", "-v");
	addCommand(ic, "isainfo.1");
	ic = new InfoCommand(IlluminateResources.getString("INFO.MOD"),
		"/usr/sbin/modinfo");
	addCommand(ic, "modinfo.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.NSCD"),
		"/usr/sbin/nscd", "-g");
	addCommand(ic, "nscd.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.NTP"),
		"/usr/sbin/ntpq", "-p");
	addCommand(ic);
	if (!new File("/usr/bin/pkg").exists()) {
	    ic = new InfoCommand(IlluminateResources.getString("INFO.PKGS"),
		"/usr/bin/pkginfo");
	    addCommand(ic, "pkginfo.1");
	}
	if (inglobal) {
	    ic = new InfoCommand(IlluminateResources.getString("INFO.EEPROM"),
		"/usr/sbin/eeprom");
	    addCommand(ic, "eeprom.8");
	    ic = new InfoCommand(IlluminateResources.getString("INFO.PRTCONF"),
		"/usr/sbin/prtconf");
	    addCommand(ic, "prtconf.8");
	    ic = new InfoCommand(IlluminateResources.getString("INFO.PRTDIAG"),
		"/usr/sbin/prtdiag");
	    addCommand(ic, "prtdiag.8");
	    ic = new InfoCommand(IlluminateResources.getString("INFO.PRTFRU"),
		"/usr/sbin/prtfru");
	    addCommand(ic, "prtfru.8");
	    ic = new InfoCommand(IlluminateResources.getString("INFO.PRTPICL"),
		"/usr/sbin/prtpicl");
	    addCommand(ic, "prtpicl.8");
	}
	ic = new InfoCommand(IlluminateResources.getString("INFO.PSRINFO"),
		"/usr/sbin/psrinfo", "-v");
	addCommand(ic, "psrinfo.8");
	if (inglobal) {
	    ic = new InfoCommand(IlluminateResources.getString("INFO.ROUTE"),
		"/sbin/routeadm");
	    addCommand(ic, "routeadm.8");
	}
	ic = new InfoCommand(IlluminateResources.getString("INFO.RPC"),
		"/usr/bin/rpcinfo", "-s");
	addCommand(ic, "rpcinfo.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.SHARE"),
		"/usr/sbin/share");
	addCommand(ic, "share.8");
	if (inglobal) {
	    ic = new InfoCommand(IlluminateResources.getString("INFO.SMBIOS"),
		"/usr/sbin/smbios");
	    addCommand(ic, "smbios.8");
	}
	ic = new InfoCommand(IlluminateResources.getString("INFO.SWAP"),
		"/usr/sbin/swap", "-lh");
	addCommand(ic, "swap.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.UNAME"),
		"/usr/bin/uname", "-a");
	addCommand(ic, "uname.1");
	ic = new InfoCommand(IlluminateResources.getString("INFO.UP"),
		"/usr/bin/uptime");
	ic.setManpage("uptime.1");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.ZPOOL"),
		"/usr/sbin/zpool", "status");
	addCommand(ic, "zpool.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.ZFS"),
		"/usr/sbin/zfs", "list");
	addCommand(ic, "zfs.8");
	ic = new InfoCommand(IlluminateResources.getString("INFO.ZONES"),
		"/usr/sbin/zoneadm", "list -icv");
	addCommand(ic, "zoneadm.8");
	if (inglobal) {
	    ic = new InfoCommand(IlluminateResources.getString("INFO.UCODE"),
		"/usr/sbin/ucodeadm", "-v");
	    addCommand(ic, "ucodeadm.8");
	}
    }

    private void addCommand(InfoCommand ic, String manpage) {
	ic.setManpage(manpage);
	addCommand(ic);
    }

    private void addCommand(InfoCommand ic) {
	if (ic.exists()) {
	    add(ic);
	}
    }
}

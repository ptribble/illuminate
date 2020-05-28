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

package org.tribblix.illuminate;

import java.util.Vector;

/**
 * InfoCommandList - a List of information commands.
 * @author Peter Tribble
 * @version 1.0
 */
public class InfoCommandList extends Vector <InfoCommand> {

    /**
     * Construct a List of available commands.
     * Note that it's actually a Vector.
     */
    public InfoCommandList() {
	// add all the possible commands here
	InfoCommand ic =
	    new InfoCommand(IlluminateResources.getString("INFO.BEADM"),
		"/usr/sbin/beadm", "list");
	ic.setManpage("beadm.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.CFGADM"),
		"/usr/sbin/cfgadm");
	ic.setManpage("cfgadm.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.CORE"),
		"/usr/bin/coreadm");
	ic.setManpage("coreadm.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.DF"),
		"/usr/sbin/df", "-kl");
	ic.setManpage("df.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.IOSTAT"),
		"/usr/bin/iostat", "-En");
	ic.setManpage("iostat.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.HOSTID"),
		"/usr/bin/hostid");
	ic.setManpage("hostid.1");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.IFCONFIG"),
		"/usr/sbin/ifconfig", "-a");
	ic.setManpage("ifconfig.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.INETADM"),
		"/usr/sbin/inetadm");
	ic.setManpage("inetadm.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.ISA"),
		"/usr/bin/isainfo", "-v");
	ic.setManpage("isainfo.1");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.MOD"),
		"/usr/sbin/modinfo");
	ic.setManpage("modinfo.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.NSCD"),
		"/usr/sbin/nscd", "-g");
	ic.setManpage("nscd.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.NTP"),
		"/usr/sbin/ntpq", "-p");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.PKGS"),
		"/usr/bin/pkginfo");
	ic.setManpage("pkginfo.1");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.EEPROM"),
		"/usr/sbin/eeprom");
	ic.setManpage("eeprom.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.PRTCONF"),
		"/usr/sbin/prtconf");
	ic.setManpage("prtconf.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.PRTDIAG"),
		"/usr/sbin/prtdiag");
	ic.setManpage("prtdiag.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.PRTFRU"),
		"/usr/sbin/prtfru");
	ic.setManpage("prtfru.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.PRTPICL"),
		"/usr/sbin/prtpicl");
	ic.setManpage("prtpicl.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.PSRINFO"),
		"/usr/sbin/psrinfo", "-v");
	ic.setManpage("psrinfo.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.ROUTE"),
		"/sbin/routeadm");
	ic.setManpage("routeadm.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.RPC"),
		"/usr/bin/rpcinfo", "-s");
	ic.setManpage("rpcinfo.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.SHARE"),
		"/usr/sbin/share");
	ic.setManpage("share.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.SMBIOS"),
		"/usr/sbin/smbios");
	ic.setManpage("smbios.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.SWAP"),
		"/usr/sbin/swap", "-lh");
	ic.setManpage("swap.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.UNAME"),
		"/usr/bin/uname", "-a");
	ic.setManpage("uname.1");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.UP"),
		"/usr/bin/uptime");
	ic.setManpage("uptime.1");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.ZPOOL"),
		"/usr/sbin/zpool", "status");
	ic.setManpage("zpool.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.ZFS"),
		"/usr/sbin/zfs", "list");
	ic.setManpage("zfs.1m");
	addCommand(ic);
	ic = new InfoCommand(IlluminateResources.getString("INFO.ZONES"),
		"/usr/sbin/zoneadm", "list -icv");
	ic.setManpage("zoneadm.1m");
	addCommand(ic);
    }

    private void addCommand(InfoCommand ic) {
	if (ic.exists()) {
	    add(ic);
	}
    }
}

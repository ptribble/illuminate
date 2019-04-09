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
	addCommand(IlluminateResources.getString("INFO.BEADM"),
		"/usr/sbin/beadm", "list");
	addCommand(IlluminateResources.getString("INFO.CORE"),
		"/usr/bin/coreadm");
	addCommand(IlluminateResources.getString("INFO.DF"),
		"/usr/sbin/df", "-kl");
	addCommand(IlluminateResources.getString("INFO.IOSTAT"),
		"/usr/bin/iostat", "-En");
	addCommand(IlluminateResources.getString("INFO.HOSTID"),
		"/usr/bin/hostid");
	addCommand(IlluminateResources.getString("INFO.IFCONFIG"),
		"/usr/sbin/ifconfig", "-a");
	addCommand(IlluminateResources.getString("INFO.INETADM"),
		"/usr/sbin/inetadm");
	addCommand(IlluminateResources.getString("INFO.ISA"),
		"/usr/bin/isainfo", "-v");
	addCommand(IlluminateResources.getString("INFO.MOD"),
		"/usr/sbin/modinfo");
	addCommand(IlluminateResources.getString("INFO.NSCD"),
		"/usr/sbin/nscd", "-g");
	addCommand(IlluminateResources.getString("INFO.NTP"),
		"/usr/sbin/ntpq", "-p");
	addCommand(IlluminateResources.getString("INFO.PKGS"),
		"/usr/bin/pkginfo");
	addCommand(IlluminateResources.getString("INFO.EEPROM"),
		"/usr/sbin/eeprom");
	addCommand(IlluminateResources.getString("INFO.PRTCONF"),
		"/usr/sbin/prtconf");
	addCommand(IlluminateResources.getString("INFO.PRTDIAG"),
		"/usr/sbin/prtdiag");
	addCommand(IlluminateResources.getString("INFO.PRTFRU"),
		"/usr/sbin/prtfru");
	addCommand(IlluminateResources.getString("INFO.PRTPICL"),
		"/usr/sbin/prtpicl");
	addCommand(IlluminateResources.getString("INFO.PSRINFO"),
		"/usr/sbin/psrinfo", "-v");
	addCommand(IlluminateResources.getString("INFO.ROUTE"),
		"/sbin/routeadm");
	addCommand(IlluminateResources.getString("INFO.RPC"),
		"/usr/bin/rpcinfo", "-s");
	addCommand(IlluminateResources.getString("INFO.SHARE"),
		"/usr/sbin/share");
	addCommand(IlluminateResources.getString("INFO.SMBIOS"),
		"/usr/sbin/smbios");
	addCommand(IlluminateResources.getString("INFO.SWAP"),
		"/usr/sbin/swap", "-lh");
	addCommand(IlluminateResources.getString("INFO.UNAME"),
		"/usr/bin/uname", "-a");
	addCommand(IlluminateResources.getString("INFO.UP"), "/usr/bin/uptime");
	addCommand(IlluminateResources.getString("INFO.ZPOOL"),
		"/usr/sbin/zpool", "status");
	addCommand(IlluminateResources.getString("INFO.ZFS"),
		"/usr/sbin/zfs", "list");
	addCommand(IlluminateResources.getString("INFO.ZONES"),
		"/usr/sbin/zoneadm", "list -icv");
    }

    private void addCommand(String text, String cmd) {
	addCommand(text, cmd, (String) null);
    }

    private void addCommand(String text, String cmd, String args) {
	add(new InfoCommand(text, cmd, args));
    }
}

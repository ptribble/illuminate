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

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import uk.co.petertribble.jkstat.api.*;
import org.tribblix.illuminate.InfoCommand;

/**
 * FsInfoPanel - shows Filesystem status.
 * @author Peter Tribble
 * @version 1.0
 */
public class FsInfoPanel extends InfoPanel {

    private JKstat jkstat;
    private JFSstatPanel fsPanel;

    /**
     * Display a file system information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public FsInfoPanel(SysItem hi, JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;

	switch (hi.getType()) {
	    case SysItem.FS_FSSTAT:
		displayFS();
		break;
	    case SysItem.ZFS_POOL:
		displayZpool();
		break;
	    case SysItem.ZFS_FS:
		displayZFS();
		break;
	    case SysItem.ZFS_VOLUME:
		displayZVOL();
		break;
	    case SysItem.FS_CONTAINER:
		displaySummary();
		break;
	    case SysItem.ZFS_CONTAINER:
		displayZSummary();
		break;
	}

	validate();
    }

    @Override
    public void stopLoop() {
	if (fsPanel != null) {
	    fsPanel.stopLoop();
	}
    }

    /*
     * Top level summary. just df
     *
     * The last column header says "Mounted on" so we need to explicitly
     * specify the number of columns.
     */
    private void displaySummary() {
	addLabel("Filesystem Usage Summary");
	addText(new CommandTableModel(
			new InfoCommand("df", "/usr/sbin/df", "-h"), 6));
    }

    /*
     * Top level summary. just zpool status
     */
    private void displayZSummary() {
	addLabel("ZFS Pool Summary");
	addText(new CommandTableModel(
			new InfoCommand("zp", "/usr/sbin/zpool",
			"list -o name,size,alloc,free,cap,dedup,health")));
    }

    /*
     * Zpool details
     */
    private void displayZpool() {
	Zpool zp = (Zpool) hi.getAttribute("zpool");
	addLabel("Details of ZFS pool " + zp.getName());
	JPanel jzp = new JPanel();
	jzp.setLayout(new BoxLayout(jzp, BoxLayout.PAGE_AXIS));

	InfoCommand ic = new InfoCommand("zp", "/usr/sbin/zpool",
					"status " + zp.getName());
	jzp.add(new SysCmdPanel(ic));

	ic = new InfoCommand("zp", "/usr/sbin/zpool",
			"get -o property,value,source all " + zp.getName());
	JTable jt = new JTable(new CommandTableModel(ic, 3));
	jzp.add(jt.getTableHeader());
	jzp.add(jt);

	addScrollPane(jzp);
    }

    /*
     * fsstat
     */
    private void displayFS() {
	addLabel("Filesystem statistics");
	fsPanel = new JFSstatPanel(jkstat, 5);
	addScrollPane(fsPanel);
    }

    /*
     * ZFS filesystem properties
     */
    private void displayZFS() {
	Zfilesys zfs = (Zfilesys) hi.getAttribute("zfs");
	addLabel("ZFS Filesystem properties for " + zfs.getName());
	addScrollPane(new JTable(new ZfsTableModel(zfs.getProperties())));
    }

    /*
     * ZFS volume properties
     */
    private void displayZVOL() {
	Zvolume zfs = (Zvolume) hi.getAttribute("zvol");
	if (zfs == null) {
	    // comes from the plain label
	    addLabel("ZFS volumes");
	    InfoCommand ic = new InfoCommand("zf", "/usr/sbin/zfs",
		"list -t volume -o name,used,avail,refer");
	    addText(new CommandTableModel(ic, 4));
	} else {
	    addLabel("ZFS volume properties for " + zfs.getName());
	    addScrollPane(new JTable(new ZfsTableModel(zfs.getProperties())));
	}
    }
}

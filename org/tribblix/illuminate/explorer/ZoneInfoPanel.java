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

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import org.tribblix.illuminate.InfoCommand;
import org.tribblix.illuminate.helpers.ManFrame;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.gui.AccessoryNetPanel;
import uk.co.petertribble.jkstat.gui.KstatAccessoryPanel;
import uk.co.petertribble.jkstat.gui.KstatAccessorySet;
import uk.co.petertribble.jkstat.gui.SparkRateAccessory;
import uk.co.petertribble.jkstat.gui.KstatTable;

/**
 * ZoneInfoPanel - shows Zone information.
 * @author Peter Tribble
 * @version 1.0
 */
public class ZoneInfoPanel extends InfoPanel implements ActionListener {

    private JKstat jkstat;
    private KstatAccessorySet kas;
    private KstatTable kt;
    private List <KstatAccessoryPanel> kaplist;
    private JButton jmb;

    /**
     * Display a zone information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public ZoneInfoPanel(SysItem hi, JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;
	kaplist = new ArrayList <KstatAccessoryPanel> ();

	if (hi.getType() == SysItem.ZONE_CONTAINER) {
	    displaySummary();
	} else if (hi.getType() == SysItem.ZONE_ZONE) {
	    displayZone();
	} else if (hi.getType() == SysItem.ZONE_PROC) {
	    displayZoneProc();
	} else if (hi.getType() == SysItem.ZONE_NET) {
	    displayZoneNet();
	} else if (hi.getType() == SysItem.ZONE_KSTAT) {
	    displayZoneKstat();
	} else if (hi.getType() == SysItem.ZONE_USAGE) {
	    displayZoneProc();
	}

	validate();
	kas = new KstatAccessorySet(kaplist, 1);
    }

    @Override
    public void stopLoop() {
	kas.stopLoop();
	if (kt != null) {
	    kt.stopLoop();
	}
    }

    /*
     * Top level summary. List of Zones.
     */
    private void displaySummary() {
	addLabel("Zone Summary");
	addText(new CommandTableModel(new InfoCommand("za", "/usr/sbin/zoneadm",
					"list -icv")));
    }

    /*
     * Describe a Zone.
     */
    private void displayZone() {
	ZoneEntry ze = (ZoneEntry) hi.getAttribute("zoneentry");
	JToolBar jtb = new JToolBar();
	jtb.setFloatable(false);
	jtb.setRollover(true);
	jtb.setLayout(new BorderLayout());
	jtb.add(new JLabel("Details of "+ze.getState()+" zone "+ze.getName()),
		BorderLayout.LINE_START);
	jtb.addSeparator();
	jmb = new JButton("About " + ze.getBrand() + " zones");
	jmb.setEnabled(true);
	jmb.setName(ze.getBrand() + ".5");
	jmb.addActionListener(this);
	jtb.add(jmb, BorderLayout.LINE_END);

	addComponent(jtb);
	addText(ze.getConfig());
    }

    /*
     * Show Zone processes
     */
    private void displayZoneProc() {
	addComponent(new ProcessInfoPanel(hi));
    }

    /*
     * Show Zone kstats
     */
    private void displayZoneKstat() {
	ZoneEntry ze = (ZoneEntry) hi.getAttribute("zoneentry");
	addLabel("Zone " + ze.getName() + " kstats");
	kt = new KstatTable(hi.getKstat(), 1, jkstat);
	addScrollPane(kt);
    }

    /*
     * Show Zone network
     */
    private void displayZoneNet() {
	ZoneEntry ze = (ZoneEntry) hi.getAttribute("zoneentry");
	@SuppressWarnings("unchecked")
	Map <String, Kstat> netMap =
	    (Map <String, Kstat>) hi.getAttribute("netmap");
	for (ZoneNet znet : ze.getNetworks()) {
	    Kstat ks = netMap.get(znet.getPhysical());
	    if (ks == null) {
		addText("Interface " + znet.getPhysical());
	    } else {
		// outer panel
		JPanel opanel = new JPanel();
		opanel.setLayout(new BoxLayout(opanel, BoxLayout.PAGE_AXIS));

		// regular network accessory
		AccessoryNetPanel acp = new AccessoryNetPanel(ks, 5, jkstat);
		kaplist.add(acp);
		opanel.add(acp);

		// panel for row of sparkcharts
		JPanel npanel = new JPanel();
		npanel.add(new JLabel("In: "));
		SparkRateAccessory kap =
		    new SparkRateAccessory(ks, -1, jkstat, "rbytes64");
		kap.enableTips("Current kb/s in:", 1.0/1024.0);
		npanel.add(kap);
		kaplist.add(kap);
		npanel.add(new JLabel("  Out: "));
		kap = new SparkRateAccessory(ks, -1, jkstat, "obytes64");
		kap.enableTips("Current kb/s out:", 1.0/1024.0);
		npanel.add(kap);
		kaplist.add(kap);
		opanel.add(npanel);
		opanel.setBorder(BorderFactory.createTitledBorder
				 ("Interface " + ks.getName()));
		addComponent(opanel);
	    }
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == jmb) {
	    new ManFrame(jmb.getName());
	}
    }
}

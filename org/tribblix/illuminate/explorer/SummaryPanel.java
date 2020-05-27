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

import javax.swing.*;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.api.KstatFilter;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.gui.KstatAccessoryPanel;
import uk.co.petertribble.jkstat.gui.KstatAccessorySet;
import uk.co.petertribble.jkstat.gui.SparkRateAccessory;
import uk.co.petertribble.jkstat.gui.SparkValueAccessory;
import uk.co.petertribble.jkstat.gui.KstatResources;
import uk.co.petertribble.jkstat.demo.ProcessorTree;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * SummaryPanel - shows overall summary of activity.
 * @author Peter Tribble
 * @version 1.0
 */
public class SummaryPanel extends InfoPanel {

    private KstatAccessorySet kas;
    private List <KstatAccessoryPanel> kaplist;
    private JKstat jkstat;

    /**
     * Display a summary information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public SummaryPanel(SysItem hi, JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;
	kaplist = new ArrayList <KstatAccessoryPanel> ();

	displaySummary();

	validate();
	kas = new KstatAccessorySet(kaplist, 1);
    }

    @Override
    public void stopLoop() {
	kas.stopLoop();
    }

    /*
     * Top level summary. We should have a description, cpu summary, network
     * chart, probably process count, load average, and memory summary
     */
    private void displaySummary() {
	ProcessorTree proctree = new ProcessorTree(jkstat);
	addLabel("Processor Summary");

        addLabel((proctree.numChips() == 1) ?
		"System contains 1 chip" :
		"System contains " + proctree.numChips() + " chips");

	StringBuilder sb = new StringBuilder();
	Set <Kstat> kss = new HashSet <Kstat> ();
	for (Long l : proctree.getChips()) {
	    sb.append(proctree.chipDetails(l));
	    kss.addAll(proctree.chipStats(l));
	}
	addText(sb.toString());
	addLoadAccessory();
        jvp.add(new CpuStatePanel(jkstat));
	addNetAccessory();
    }

    /*
     * Add a network accessory
     */
    private void addNetAccessory() {
	// filter the kstats we need
	KstatFilter ksf = new KstatFilter(jkstat);
	ksf.setFilterClass("net");
	ksf.addFilter(":::rbytes64");
	ksf.addNegativeFilter("::mac");

	String iflabel = "Network " +
	    KstatResources.getString("NETLOAD.IF.TEXT") + " ";
	for (Kstat ks : new TreeSet <Kstat> (ksf.getKstats())) {
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
	    npanel.setBorder(BorderFactory.createTitledBorder
				  (iflabel + ks.getName()));
	    jvp.add(npanel);
	}
    }

    /*
     * Add a load average accessory
     */
    private void addLoadAccessory() {
	Kstat ks = jkstat.getKstat("unix", 0, "system_misc");

	// 1 and 15 minute load averages
	JPanel lpanel = new JPanel();
	lpanel.add(new JLabel("1min: "));
	SparkValueAccessory kap =
		new SparkValueAccessory(ks, -1, jkstat, "avenrun_1min");
	kap.enableTips("Current load:", 1.0/256.0);
	lpanel.add(kap);
	kaplist.add(kap);
	lpanel.add(new JLabel("  15min: "));
	kap = new SparkValueAccessory(ks, -1, jkstat, "avenrun_15min");
	kap.enableTips("Current load:", 1.0/256.0);
	lpanel.add(kap);
	kaplist.add(kap);
	lpanel.setBorder(BorderFactory.createTitledBorder("Load Average"));
	jvp.add(lpanel);

	// process count
	JPanel ppanel = new JPanel();
	ppanel.add(new JLabel("Count: "));
	kap = new SparkValueAccessory(ks, -1, jkstat, "nproc");
	kap.enableTips("Current process count:");
	ppanel.add(kap);
	kaplist.add(kap);
	ppanel.setBorder(BorderFactory.createTitledBorder("Processes"));
	jvp.add(ppanel);
    }
}

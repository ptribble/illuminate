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
import java.awt.BorderLayout;
import java.awt.Color;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.api.KstatAggregate;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.gui.KstatAccessoryPanel;
import uk.co.petertribble.jkstat.gui.AccessoryCpuPanel;
import uk.co.petertribble.jkstat.gui.KstatBaseChart;
import uk.co.petertribble.jkstat.gui.KstatAreaChart;
import uk.co.petertribble.jkstat.gui.KstatAggregateAreaChart;
import uk.co.petertribble.jkstat.gui.MPstatTable;
import uk.co.petertribble.jkstat.demo.ProcessorTree;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import org.jfree.chart.ChartPanel;

/**
 * CpuInfoPanel - shows Cpu status.
 * @author Peter Tribble
 * @version 1.0
 */
public class CpuInfoPanel extends InfoPanel {

    private ProcessorTree proctree;
    private MPstatTable mptable;
    private KstatAccessoryPanel kap;
    private KstatBaseChart kbc;
    private JKstat jkstat;
    private static final List <String> mystats =
		Arrays.asList("kernel", "user", "idle");
    private static final List <Color> mycolors =
		Arrays.asList(Color.YELLOW, Color.GREEN, Color.BLUE);

    /**
     * Display a Cpu information panel.
     *
     * @param hi The item to display
     * @param jkstat A JKstat object
     */
    public CpuInfoPanel(SysItem hi, JKstat jkstat) {
	super(hi);
	this.jkstat = jkstat;

	proctree = (ProcessorTree) hi.getAttribute("ptree");

	switch (hi.getType()) {
	    case SysItem.CPU:
		displayChip();
		break;
	    case SysItem.CPU_CORE:
		displayCore();
		break;
	    case SysItem.CPU_THREAD:
		displayThread();
		break;
	    case SysItem.CPU_CONTAINER:
		displaySummary();
		break;
	}

	validate();
    }

    @Override
    public void stopLoop() {
	if (kap != null) {
	    kap.stopLoop();
	}
	if (mptable != null) {
	    mptable.stopLoop();
	}
	if (kbc != null) {
	    kbc.stopLoop();
	}
    }

    /*
     * Top level summary. This ought to be a variant on psrinfo.
     */
    private void displaySummary() {
        addLabel((proctree.numChips() == 1) ?
		"System contains 1 chip" :
		"System contains " + proctree.numChips() + " chips");

	StringBuilder sb = new StringBuilder();
	for (Long l : proctree.getChips()) {
	    sb.append(proctree.chipDetails(l));
	}
	addText(sb.toString());
	mptable = new MPstatTable(jkstat, 5);
	addScrollPane(mptable);
    }

    /*
     * Note on types: See SysTree.java, where the "core" and "chip"
     * attributes are Long, whereas the "thread" is an int.
     */

    /*
     * A processor chip.
     */
    private void displayChip() {
	Long l = (Long) hi.getAttribute("chip");
	addLabel("Details of processor " + l);
	addText(proctree.chipDetails(l));
	if (proctree.isMulticore()) {
	    addChipAccessory();
	} else {
	    addAccessory();
	}
    }

    /*
     * A processor core.
     */
    private void displayCore() {
	addLabel("Details of processor core "
		+ hi.getAttribute("core"));
	if (proctree.isThreaded()) {
	    // FIXME psrinfo fragment here
	    addCoreAccessory();
	} else {
	    addText(ProcessorTree.details(hi.getKstat()));
	    addAccessory();
	}
    }

    /*
     * A processor thread.
     */
    private void displayThread() {
	addLabel("Details of processor thread "
		+ hi.getAttribute("thread"));
	// FIXME psrinfo fragment here
	addAccessory();
    }

    /*
     * Add an accessory if we can.
     */
    private void addAccessory() {
	Kstat ks = hi.getKstat();
	if (ks != null) {
	    Kstat ksc = proctree.makeCpuKstat(ks);
	    kap = new AccessoryCpuPanel(ksc, 1, jkstat);
	    JPanel jp = new JPanel(new BorderLayout());
	    jp.add(kap);
	    jp.setBorder(BorderFactory.createTitledBorder("CPU activity"));
	    addComponent(jp);
	    kbc = new KstatAreaChart(jkstat, ksc, mystats, true);
	    kbc.setColors(mycolors);
	    addComponent(new ChartPanel(kbc.getChart()));
	}
    }

    /*
     * Add an accessory aggregated over threads if we can.
     */
    private void addCoreAccessory() {
	Set <Kstat> kss = proctree.coreStats((Long) hi.getAttribute("chip"),
					(Long) hi.getAttribute("core"));
	if (!kss.isEmpty()) {
	    addAggregateAccessory(new KstatAggregate(jkstat, kss,
					"core " + hi.getAttribute("core")));
	}
    }

    /*
     * Add an accessory aggregated over cores if we can.
     */
    private void addChipAccessory() {
	Set <Kstat> kss = proctree.chipStats((Long) hi.getAttribute("chip"));
	if (!kss.isEmpty()) {
	    addAggregateAccessory(new KstatAggregate(jkstat, kss,
					"chip " + hi.getAttribute("chip")));
	}
    }

    /*
     * Common aggregate accessory.
     */
    private void addAggregateAccessory(KstatAggregate ksa) {
	kap = new AggregateCpuPanel(ksa, 1, jkstat);
	JPanel jp = new JPanel(new BorderLayout());
	jp.add(kap);
	jp.setBorder(BorderFactory.createTitledBorder(
						"Aggregate CPU activity"));
	addComponent(jp);
	kbc = new KstatAggregateAreaChart(jkstat, ksa, mystats, true);
	kbc.setColors(mycolors);
	addComponent(new ChartPanel(kbc.getChart()));
    }
}

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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import uk.co.petertribble.jkstat.api.*;
import uk.co.petertribble.jkstat.gui.*;
import org.tribblix.illuminate.IlluminateResources;
import uk.co.petertribble.jkstat.demo.ProcessorTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import uk.co.petertribble.jingle.SpringUtilities;

/**
 * A graphical kstat demo, like an enhanced xcpustate that also shows
 * aggregate statistics for multithreaded and multicore chips.
 *
 * @author Peter Tribble
 */
public class CpuStatePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static int orientation = SwingConstants.VERTICAL;

    private JKstat jkstat;

    private transient ProcessorTree proctree;
    private Set<Kstat> kstats;
    private int ncpus;
    private int ncpu;

    private JMenuItem[] aboutCpuItem;
    private JMenuItem[] extendedCpuItem;
    private JMenuItem[] chartCpuItem;
    private String[] cpuID;
    private List<KstatAccessoryPanel> kaplist;

    private Dimension dchip;
    private Dimension dcore;
    private Dimension dthread;
    // localised via properties
    private String chipText;
    private String coreText;
    private String cpuLabel;
    private String cpuChartLabel;
    private int fontScale;
    // save this once, for reuse
    private Font lfont;

    /**
     * Create a CpuStatePanel.
     *
     * @param jkstat a JKstat object
     */
    public CpuStatePanel(JKstat jkstat) {
	this.jkstat = jkstat;

	KstatFilter ksf = new KstatFilter(jkstat);
	ksf.setFilterClass("misc");
	ksf.addFilter("cpu_stat:::");

	kstats = ksf.getKstats();

	ncpus = kstats.size();
	aboutCpuItem = new JMenuItem[ncpus];
	extendedCpuItem = new JMenuItem[ncpus];
	chartCpuItem = new JMenuItem[ncpus];
	cpuID = new String[ncpus];
	kaplist = new ArrayList<>();

	// get the logical structure of the cpus
	proctree = new ProcessorTree(jkstat);

	// create localized labels
	chipText = IlluminateResources.getString("CPUSTATE.CHIP") + " ";
	coreText = IlluminateResources.getString("CPUSTATE.CORE") + " ";
	String cpuText = IlluminateResources.getString("CPUSTATE.CPU") + " ";

	// change the labels depending on structure
	if (proctree.isMulticore()) {
	    cpuText = IlluminateResources.getString("CPUSTATE.CORE") + " ";
	}
	if (proctree.isThreaded()) {
	    cpuText = IlluminateResources.getString("CPUSTATE.THREAD") + " ";
	    fontScale = -4;
	}
	cpuLabel = IlluminateResources.getString("CPUSTATE.ABOUT.TEXT")
		+ " " + cpuText;
	cpuChartLabel = IlluminateResources.getString("CPUSTATE.CHART.TEXT")
		+ " " + cpuText;

	setDimensions();

	populate();
    }

    private void setDimensions() {
	int threadheight = 20;
	if (proctree.isMulticore()) {
	    threadheight -= 5;
	}
	if (proctree.isThreaded()) {
	    threadheight -= 5;
	}
	dchip = new Dimension(20, 50);
	dcore = new Dimension(proctree.isMulticore() ? 15 : 20, 50);
	dthread = new Dimension(threadheight, 50);
    }

    private void populate() {
	populateNew();
    }

    /*
     * The idea here is to lay out the chips above each other. Then, for each
     * chip show the aggregate on the left of the box and the chip's name
     * (possibly with details) across the bottom of the box. Then in the
     * remaining space, lay out the cores horizontally. And again use the same
     * aggregate at the left, text across the bottom, with threads side by
     * side in the remaining space.
     */
    private void populateNew() {
	if (proctree.isMulticore()) {
	    // iterate over chips, showing aggregates over cores
	    for (Long l : proctree.getChips()) {
		Set<Kstat> kschip = proctree.chipStats(l);
		JPanel cpanl = new JPanel(new BorderLayout());
		cpanl.setBackground(cpanl.getBackground().darker());
		addChipNew(kschip, dchip, cpanl);
		JLabel clabel = new JLabel(chipText + l, JLabel.CENTER);
		cpanl.add(clabel, BorderLayout.SOUTH);
		if (proctree.isThreaded()) {
		    // multicore and multithreaded
		    // tpanl holds all the core panels
		    JPanel tpnl = new JPanel(new GridLayout());
		    for (Long ll : proctree.getCores(l)) {
			Set<Kstat> kscore = proctree.coreStats(l, ll);
			// mpanl is the outer panel for this core
			JPanel mpanl = new JPanel(new BorderLayout());
			mpanl.setBackground(mpanl.getBackground().brighter());
			// core aggregate goes on the left
			addChipNew(kscore, dcore, mpanl);
			// label across the bottom
			mpanl.add(new JLabel(coreText + ll.toString(),
				JLabel.CENTER), BorderLayout.SOUTH);
			// add the panel with the threads
			mpanl.add(multiPanel(kscore));
			mpanl.setBorder(
				BorderFactory.createMatteBorder(2, 2, 2, 2,
							tpnl.getBackground()));
			tpnl.add(mpanl);
		    }
		    cpanl.add(tpnl);
		} else {
		    // multicore but not threaded
		    // show the individual cores in that chip
		    cpanl.add(multiPanel(kschip));
		}
		add(cpanl);
	    }
	} else if (proctree.isThreaded()) {
	    // single core threaded cpus
	    // iterate over chips, showing aggregates over threads
	    for (Long l : proctree.getChips()) {
		JPanel cpanl = new JPanel(new BorderLayout());
		Set<Kstat> kschip = proctree.chipStats(l);
		addChipNew(kschip, dchip, cpanl);
		JLabel clabel = new JLabel(chipText + l, JLabel.CENTER);
		cpanl.add(clabel, BorderLayout.SOUTH);
		cpanl.add(multiPanel(kschip));
		add(cpanl);
	    }
	} else {
	    // neither multicore nor multithread, just show the individual cpus
	    for (Kstat ks : kstats) {
		addProcessor(ks, this);
	    }
	    for (Kstat ks : kstats) {
		add(new JLabel(ks.getInstance(), JLabel.CENTER));
	    }
	}
    }

    /*
     * Put a set of kstats into a panel: line of vertical accessories
     * above a line of labels.
     */
    private JPanel multiPanel(Set<Kstat> kss) {
	JPanel ppanl = new JPanel(new SpringLayout());
	for (Kstat ks : kss) {
	    addProcessor(ks, ppanl);
	}
	for (Kstat ks : kss) {
	    JLabel jl = new JLabel(ks.getInstance(), JLabel.CENTER);
	    if (lfont == null) {
		Font f = jl.getFont();
		lfont = new Font(f.getName(), f.getStyle(),
				f.getSize()+fontScale);
	    }
	    jl.setFont(lfont);
	    ppanl.add(jl);
	}
	SpringUtilities.makeCompactGrid(ppanl, 2, kss.size(), 6, 3, 3, 3);
	return ppanl;
    }

    /*
     * Add a processor to the given panel.
     */
    private void addProcessor(Kstat ks, JPanel ppanel) {
	String scpu = ks.getInstance();
	KstatAccessoryPanel acp =
	    new AccessoryCpuPanel(ks, 1, jkstat, orientation);
	kaplist.add(acp);
	// make wider and thinner than normal
	acp.setMinimumSize(dthread);
	acp.setPreferredSize(dthread);
	ppanel.add(acp);
	// add a popup menu to each one
	JPopupMenu jpm = new JPopupMenu();
	cpuID[ncpu] = scpu;
	aboutCpuItem[ncpu] = new JMenuItem(cpuLabel + scpu);
	aboutCpuItem[ncpu].addActionListener(this);
	jpm.add(aboutCpuItem[ncpu]);
	extendedCpuItem[ncpu] = new JMenuItem(
			IlluminateResources.getString("CPUSTATE.EXT.TEXT"));
	extendedCpuItem[ncpu].addActionListener(this);
	jpm.add(extendedCpuItem[ncpu]);
	chartCpuItem[ncpu] = new JMenuItem(cpuChartLabel + scpu);
	chartCpuItem[ncpu].addActionListener(this);
	jpm.add(chartCpuItem[ncpu]);
	acp.addMouseListener((MouseListener) new PopupListener(jpm));
	ncpu++;
    }

    /*
     * Only used in the vertical layout
     */
    private void addChipNew(Set<Kstat> ksc, Dimension d, JPanel panl) {
	KstatAggregate ksa = new KstatAggregate(jkstat, ksc);
	KstatAccessoryPanel agp =
	    new AggregateCpuPanel(ksa, 1, jkstat, orientation);
	kaplist.add(agp);
	agp.setMinimumSize(d);
	agp.setPreferredSize(d);
	panl.add(agp, BorderLayout.WEST);
    }

    public void stopLoop() {
	for (KstatAccessoryPanel kap : kaplist) {
	    kap.stopLoop();
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	for (int i = 0; i < ncpus; i++) {
	    if (e.getSource() == aboutCpuItem[i]) {
		new KstatTableFrame("cpu_info", cpuID[i], "cpu_info"+cpuID[i],
				    -1, jkstat);
	    }
	    if (e.getSource() == extendedCpuItem[i]) {
		new KstatTableFrame("cpu_stat", cpuID[i], "cpu_stat"+cpuID[i],
				    1, jkstat);
	    }
	    if (e.getSource() == chartCpuItem[i]) {
		List<String> stats = Arrays.asList("user", "kernel", "idle");
		new KstatAreaChartFrame(jkstat,
			new Kstat("cpu_stat", Integer.parseInt(cpuID[i]),
				"cpu_stat"+cpuID[i]),
			stats, true);
	    }
	}
    }

    /**
     * Inner class to handle mouse popups.
     */
    static class PopupListener extends MouseAdapter {
	private JPopupMenu popup;

	PopupListener(JPopupMenu popup) {
	    this.popup = popup;
	}

	@Override
	public void mousePressed(MouseEvent e) {
	    showPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	    showPopup(e);
	}

	private void showPopup(MouseEvent e) {
	    if (e.isPopupTrigger()) {
		popup.show(e.getComponent(), e.getX(), e.getY());
	    }
	}
    }
}

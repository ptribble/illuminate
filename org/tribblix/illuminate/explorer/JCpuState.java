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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import uk.co.petertribble.jkstat.api.*;
import uk.co.petertribble.jkstat.gui.*;
import org.tribblix.illuminate.IlluminateResources;
import uk.co.petertribble.jkstat.demo.ProcessorTree;
import uk.co.petertribble.jkstat.demo.JKdemo;
import java.util.*;
import uk.co.petertribble.jingle.SpringUtilities;

/**
 * A graphical kstat demo, like an enhanced xcpustate that also shows
 * aggregate statistics for multithreaded and multicore chips.
 *
 * @author Peter Tribble
 */
public class JCpuState extends JKdemo implements ActionListener {

    static private final int STYLE_BASIC = 0;
    static private final int STYLE_CHART = 1;
    private static int style = STYLE_BASIC;
    private static int orientation = SwingConstants.HORIZONTAL;

    private JKstat jkstat;

    private ProcessorTree proctree;
    private Set <Kstat> kstats;
    private int ncpus;
    private int naggr;
    private int ncpu;

    private JPanel mainPanel;

    private JMenuItem[] aboutCpuItem;
    private JMenuItem[] extendedCpuItem;
    private JMenuItem[] chartCpuItem;
    private String[] cpuID;
    private List <KstatAccessoryPanel> kaplist;

    private Dimension dchip;
    private Dimension dcore;
    private Dimension dthread;
    // localised via properties
    private String chipText;
    private String coreText;
    private String cpuText;
    private String cpuLabel;
    private String cpuChartLabel;
    private int fontScale;
    // save this once, for reuse
    private Font lfont;

    // for optional menu item
    private boolean showChips = true;
    private boolean showCores = true;
    private boolean showThreads = true;
    private JCheckBoxMenuItem showChipItem;
    private JCheckBoxMenuItem showCoreItem;
    private JCheckBoxMenuItem showThreadItem;

    /**
     * Create a new JCpuState application.
     */
    public JCpuState() {
	this(new NativeJKstat(), new String[0], true);
    }

    /**
     * Create a new JCpuState application.
     *
     * @param args command line arguments
     */
    public JCpuState(String[] args) {
	this(new NativeJKstat(), args, true);
    }

    /**
     * Create a new JCpuState application.
     *
     * @param jkstat a JKstat object
     * @param standalone if false, indicates that this demo is being called
     * from another application
     */
    public JCpuState(JKstat jkstat, boolean standalone) {
	this(jkstat, new String[0], standalone);
    }

    /**
     * Create a JCpuState demo.
     *
     * @param jkstat a JKstat object
     * @param args command line arguments
     * @param standalone if false, indicates that this demo is being called
     * from another application
     */
    public JCpuState(JKstat jkstat, String[] args, boolean standalone) {
	super("jcpustate", 1, standalone, false, false);

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
	kaplist = new ArrayList <KstatAccessoryPanel> ();

	// create main display panel
	mainPanel = new JPanel(new SpringLayout());
	setContentPane(mainPanel);

	// get the logical structure of the cpus
	proctree = new ProcessorTree(jkstat);

	/*
	 * Parse arguments. This has to be done after producing the
	 * ProcessorTree so that we know which arguments are valid.
	 */
	parseArgs(args);

	// create localized labels
	chipText = IlluminateResources.getString("CPUSTATE.CHIP") + " ";
	coreText = IlluminateResources.getString("CPUSTATE.CORE") + " ";
	cpuText = IlluminateResources.getString("CPUSTATE.CPU") + " ";

	// change the labels depending on structure
	if (proctree.isMulticore()) {
	    cpuText = IlluminateResources.getString("CPUSTATE.CORE") + " ";
	}
	if (proctree.isThreaded()) {
	    cpuText = IlluminateResources.getString("CPUSTATE.THREAD") + " ";
	    fontScale = (orientation == SwingConstants.HORIZONTAL) ? -2 : -4;
	}
	cpuLabel = IlluminateResources.getString("CPUSTATE.ABOUT.TEXT")
		+ " " + cpuText;
	cpuChartLabel = IlluminateResources.getString("CPUSTATE.CHART.TEXT")
		+ " " + cpuText;

	setDimensions();

	populate();

	if (orientation == SwingConstants.HORIZONTAL) {
	    addMenus();
	}

	pack();
	setVisible(true);
    }

    private void setDimensions() {
	int threadheight = 20;
	if (proctree.isMulticore()) {
	    threadheight -= 5;
	}
	if (proctree.isThreaded()) {
	    threadheight -= 5;
	}
	if (orientation == SwingConstants.VERTICAL) {
	    dchip = new Dimension(20, 50);
	    dcore = new Dimension(proctree.isMulticore() ? 15 : 20, 50);
	    dthread = new Dimension(threadheight, 50);
	} else {
	    dchip = new Dimension(200, 20);
	    dcore = new Dimension(200, proctree.isMulticore() ? 15 : 20);
	    dthread = new Dimension(200, threadheight);
	}
    }

    private void populate() {
	if (orientation == SwingConstants.VERTICAL) {
	    populateNew();
	} else {
	    populateOld();
	}
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
	    int nchips = 0;
	    for (Long l : proctree.getChips()) {
		Set <Kstat> kschip = proctree.chipStats(l);
		JPanel cpanl = new JPanel(new BorderLayout());
		cpanl.setBackground(cpanl.getBackground().darker());
		nchips++;
		addChipNew(kschip, dchip, cpanl);
		JLabel clabel = new JLabel(chipText + l, JLabel.CENTER);
		cpanl.add(clabel, BorderLayout.SOUTH);
		if (proctree.isThreaded()) {
		    // multicore and multithreaded
		    // tpanl holds all the core panels
		    // JPanel tpnl = new JPanel(new SpringLayout());
		    JPanel tpnl = new JPanel(new GridLayout());
		    for (Long ll : proctree.getCores(l)) {
			Set <Kstat> kscore = proctree.coreStats(l, ll);
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
		mainPanel.add(cpanl);
	    }
	    SpringUtilities.makeCompactGrid(mainPanel, nchips, 1, 6, 3, 3, 3);
	} else if (proctree.isThreaded()) {
	    // single core threaded cpus
	    // iterate over chips, showing aggregates over threads
	    int nchips = 0;
	    for (Long l : proctree.getChips()) {
		JPanel cpanl = new JPanel(new BorderLayout());
		nchips++;
		Set <Kstat> kschip = proctree.chipStats(l);
		addChipNew(kschip, dchip, cpanl);
		JLabel clabel = new JLabel(chipText + l, JLabel.CENTER);
		cpanl.add(clabel, BorderLayout.SOUTH);
		cpanl.add(multiPanel(kschip));
		mainPanel.add(cpanl);
	    }
	    SpringUtilities.makeCompactGrid(mainPanel, nchips, 1, 6, 3, 3, 3);
	} else {
	    // neither multicore nor multithread, just show the individual cpus
	    for (Kstat ks : kstats) {
		addProcessor(ks);
	    }
	    for (Kstat ks : kstats) {
		mainPanel.add(new JLabel(ks.getInstance(), JLabel.CENTER));
	    }
	    SpringUtilities.makeCompactGrid(mainPanel, 2, ncpu, 6, 3, 3, 3);
	}
    }

    /*
     * Put a set of kstats into a panel: line of vertical accessories
     * above a line of lables.
     */
    private JPanel multiPanel(Set <Kstat> kss) {
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

    private void populateOld() {
	if (proctree.isMulticore()) {
	    // iterate over chips, showing aggregates over cores
	    for (Long l : proctree.getChips()) {
		Set <Kstat> kschip = proctree.chipStats(l);
		if (showChips) {
		    addChip(kschip, chipText, l, dchip);
		}
		if (proctree.isThreaded()) {
		    for (Long ll : proctree.getCores(l)) {
			Set <Kstat> kscore = proctree.coreStats(l, ll);
			if (showCores) {
			    addChip(kscore, coreText, ll, dcore);
			}
			if (showThreads) {
			    // show the individual threads in that core
			    for (Kstat ks : kscore) {
				addProcessor(ks);
			    }
			}
		    }
		} else {
		    // not threaded
		    if (showCores) {
			// show the individual cores in that chip
			for (Kstat ks : kschip) {
			    addProcessor(ks);
			}
		    }
		}
	    }
	} else if (proctree.isThreaded()) {
	    // single core cpus
	    // iterate over chips, showing aggregates over threads
	    for (Long l : proctree.getChips()) {
		Set <Kstat> kschip = proctree.chipStats(l);
		if (showChips) {
		    addChip(kschip, chipText, l, dchip);
		}
		if (showThreads) {
		    // show the individual threads in that chip
		    for (Kstat ks : kschip) {
			addProcessor(ks);
		    }
		}
	    }
	} else {
	    // neither multicore nor multithread, show the individual cpus
	    for (Kstat ks : kstats) {
		addProcessor(ks);
	    }
	}

	SpringUtilities.makeCompactGrid(mainPanel, ncpu+naggr, 2, 6, 3, 3, 3);
    }

    // FIXME for charts, preserve history
    private void repopulate() {
	// stop everything
	stopLoop();
	// clear everything
	ncpu = 0;
	naggr = 0;
	kaplist.clear();
	mainPanel.removeAll();
	// and create the main interface again
	populate();
	mainPanel.revalidate();
	validate();
	// sanitise the menus
	checkMenus();

	pack();
    }

    private void addMenus() {
	if (proctree.isMulticore() || proctree.isThreaded()) {
	    JMenu jms = new JMenu(IlluminateResources.getString("SHOW.TEXT"));
	    jms.setMnemonic(KeyEvent.VK_S);
	    showChipItem = new JCheckBoxMenuItem(
			IlluminateResources.getString("CPUSTATE.SHOW.CHIP"));
	    showChipItem.setSelected(showChips);
	    showChipItem.addActionListener(this);
	    jms.add(showChipItem);
	    if (proctree.isMulticore()) {
		showCoreItem = new JCheckBoxMenuItem(
			IlluminateResources.getString("CPUSTATE.SHOW.CORE"));
		showCoreItem.setSelected(showCores);
		showCoreItem.addActionListener(this);
		jms.add(showCoreItem);
	    }
	    if (proctree.isThreaded()) {
		showThreadItem = new JCheckBoxMenuItem(
			IlluminateResources.getString("CPUSTATE.SHOW.THREAD"));
		showThreadItem.setSelected(showThreads);
		showThreadItem.addActionListener(this);
		jms.add(showThreadItem);
	    }
	    addMenu(jms);
	}

	checkMenus();
    }

    /*
     * If the Show menu has only one enabled entry, disable it so that it
     * can't be deselected.
     */
    private void checkMenus() {
	if (orientation == SwingConstants.HORIZONTAL) {
	    if (proctree.isMulticore() && proctree.isThreaded()) {
		showChipItem.setEnabled(showCores || showThreads);
		showCoreItem.setEnabled(showChips || showThreads);
		showThreadItem.setEnabled(showChips || showCores);
	    } else if (proctree.isMulticore()) {
		showChipItem.setEnabled(showCores);
		showCoreItem.setEnabled(showChips);
	    } else if (proctree.isThreaded()) {
		showChipItem.setEnabled(showThreads);
		showThreadItem.setEnabled(showChips);
	    }
	}
    }

    /*
     * Add a processor to the main panel
     */
    private void addProcessor(Kstat ks) {
	addProcessor(ks, mainPanel);
    }

    /*
     * Add a processor to the given panel.
     */
    private void addProcessor(Kstat ks, JPanel ppanel) {
	String scpu = ks.getInstance();
	// only add the label here if we're horizontal
	if (orientation != SwingConstants.VERTICAL) {
	    JLabel jl = new JLabel(cpuText + scpu);
	    if (lfont == null) {
		Font f = jl.getFont();
		lfont = new Font(f.getName(), f.getStyle(),
				f.getSize()+fontScale);
	    }
	    jl.setFont(lfont);
	    ppanel.add(jl);
	}
	KstatAccessoryPanel acp =
	    (style == STYLE_CHART) ?
	    new AccessoryCpuChart(ks, 1, jkstat) :
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
     * Only used in the horizontal layout
     */
    private void addChip(Set <Kstat> ksc, String s, Long l, Dimension d) {
	mainPanel.add(new JLabel(s + l));
	KstatAggregate ksa = new KstatAggregate(jkstat, ksc);
	KstatAccessoryPanel agp =
	    (style == STYLE_CHART) ?
	    new AggregateCpuChart(ksa, 1, jkstat) :
	    new AggregateCpuPanel(ksa, 1, jkstat, orientation);
	kaplist.add(agp);
	// make wider and thinner than normal
	agp.setMinimumSize(d);
	agp.setPreferredSize(d);
	mainPanel.add(agp);
	naggr++;
    }

    /*
     * Only used in the vertical layout
     */
    private void addChipNew(Set <Kstat> ksc, Dimension d, JPanel panl) {
	KstatAggregate ksa = new KstatAggregate(jkstat, ksc);
	KstatAccessoryPanel agp =
	    new AggregateCpuPanel(ksa, 1, jkstat, orientation);
	kaplist.add(agp);
	agp.setMinimumSize(d);
	agp.setPreferredSize(d);
	panl.add(agp, BorderLayout.WEST);
    }

    @Override
    public void stopLoop() {
	for (KstatAccessoryPanel kap : kaplist) {
	    kap.stopLoop();
	}
    }

    public void actionPerformed(ActionEvent e) {
	super.actionPerformed(e);
	if (e.getSource() == showChipItem) {
	    showChips = showChipItem.isSelected();
	    repopulate();
	}
	if (e.getSource() == showCoreItem) {
	    showCores = showCoreItem.isSelected();
	    repopulate();
	}
	if (e.getSource() == showThreadItem) {
	    showThreads = showThreadItem.isSelected();
	    repopulate();
	}
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
		List <String> stats = Arrays.asList("user", "kernel", "idle");
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

	public PopupListener(JPopupMenu popup) {
	    this.popup = popup;
	}

	public void mousePressed(MouseEvent e) {
	    showPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
	    showPopup(e);
	}

	private void showPopup(MouseEvent e) {
	    if (e.isPopupTrigger()) {
		popup.show(e.getComponent(), e.getX(), e.getY());
	    }
	}
    }


    // getopts would be nice
    private void parseArgs(String[] args) {
	if (args.length > 0) {
	    showChips = false;
	    showCores = false;
	    showThreads = false;
	}
	for (String s : args) {
	    if ("chart".equals(s)) {
		style = STYLE_CHART;
		orientation = SwingConstants.HORIZONTAL;
	    }
	    if ("vertical".equals(s)) {
		style = STYLE_BASIC;
		orientation = SwingConstants.VERTICAL;
	    }
	    if ("-p".equals(s)) {
		showChips = true;
	    }
	    if (proctree.isMulticore() && "-c".equals(s)) {
		showCores = true;
	    }
	    if (proctree.isThreaded() && "-t".equals(s)) {
		showThreads = true;
	    }
	}
	// if nothing enabled, then bad args, enable everything
	if (!(showChips || showCores || showThreads)) {
	    showChips = true;
	    showCores = true;
	    showThreads = true;
	}
    }

    /**
     * Create a standalone JCpuState demo application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
	new JCpuState(args);
    }
}

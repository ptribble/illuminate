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

package org.tribblix.illuminate.explorer;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.api.KstatAggregate;
import uk.co.petertribble.jkstat.api.KstatFilter;
import uk.co.petertribble.jkstat.api.NativeJKstat;
import uk.co.petertribble.jkstat.gui.AccessoryCpuChart;
import uk.co.petertribble.jkstat.gui.AccessoryCpuPanel;
import uk.co.petertribble.jkstat.gui.KstatAccessoryPanel;
import uk.co.petertribble.jkstat.gui.KstatAreaChartFrame;
import uk.co.petertribble.jkstat.gui.KstatTableFrame;
import org.tribblix.illuminate.IlluminateResources;
import uk.co.petertribble.jkstat.demo.ProcessorChip;
import uk.co.petertribble.jkstat.demo.ProcessorCore;
import uk.co.petertribble.jkstat.demo.ProcessorTree;
import uk.co.petertribble.jkstat.demo.JKdemo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A graphical kstat demo, like an enhanced xcpustate that also shows
 * aggregate statistics for multithreaded and multicore chips.
 *
 * @author Peter Tribble
 */
public final class JCpuState extends JKdemo implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final int STYLE_BASIC = 0;
    private static final int STYLE_CHART = 1;
    private static int style = STYLE_BASIC;
    private static int orientation = SwingConstants.HORIZONTAL;

    private transient JKstat jkstat;

    private transient ProcessorTree proctree;
    private transient Set<Kstat> kstats;
    /**
     * How many cpus we have.
     */
    private int ncpus;
    /**
     * How many aggregates we have.
     */
    private int naggr;
    /**
     * Temp counter.
     */
    private int ncpu;
    /**
     * The main panel.
     */
    private JPanel mainPanel;

    /**
     * Menu items for about popup.
     */
    private JMenuItem[] aboutCpuItem;
    /**
     * Menu items for extended information popup.
     */
    private JMenuItem[] extendedCpuItem;
    /**
     * Menu items for chart popup.
     */
    private JMenuItem[] chartCpuItem;
    /**
     * Save the id of each cpu.
     */
    private String[] cpuID;
    private transient List<KstatAccessoryPanel> kaplist;

    /**
     * The size of a chip widget.
     */
    private Dimension dchip;
    /**
     * The size of a core widget.
     */
    private Dimension dcore;
    /**
     * The size of a thread widget.
     */
    private Dimension dthread;
    /**
     * Text for chip label, localised via properties.
     */
    private String chipText;
    /**
     * Text for core label, localised via properties.
     */
    private String coreText;
    /**
     * Text for cpu label, localised via properties.
     */
    private String cpuText;
    /**
     * Text for cpu label, localised via properties.
     */
    private String cpuLabel;
    /**
     * Text for chart label, localised via properties.
     */
    private String cpuChartLabel;
    /**
     * Save the font scaling, depends on layout.
     */
    private int fontScale;
    /**
     * Font, save this once, for reuse.
     */
    private Font lfont;

    /**
     * Whether aggregates for chips are shown.
     */
    private boolean showChips = true;
    /**
     * Whether aggregates for cores are shown.
     */
    private boolean showCores = true;
    /**
     * Whether individual threads are shown.
     */
    private boolean showThreads = true;
    /**
     * Menu item to choose whether aggregates for chips are shown.
     */
    private JCheckBoxMenuItem showChipItem;
    /**
     * Menu item to choose whether aggregates for cores are shown.
     */
    private JCheckBoxMenuItem showCoreItem;
    /**
     * Menu item to choose whether aggregates for chips are shown.
     */
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
    public JCpuState(final String[] args) {
	this(new NativeJKstat(), args, true);
    }

    /**
     * Create a new JCpuState application.
     *
     * @param jkstat a JKstat object
     * @param standalone if false, indicates that this demo is being called
     * from another application
     */
    public JCpuState(final JKstat jkstat, final boolean standalone) {
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
    public JCpuState(final JKstat jkstat, final String[] args,
		     final boolean standalone) {
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
	kaplist = new ArrayList<>();

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
	    for (ProcessorChip chip : proctree.getProcessorChips()) {
		Set<Kstat> kschip =
		    ProcessorTree.makeCpuKstats(chip.infoStats());
		JPanel cpanl = new JPanel(new BorderLayout());
		cpanl.setBackground(cpanl.getBackground().darker());
		nchips++;
		addChipNew(kschip, dchip, cpanl);
		JLabel clabel = new JLabel(chipText + chip.getChipid(),
					   JLabel.CENTER);
		cpanl.add(clabel, BorderLayout.SOUTH);
		if (proctree.isThreaded()) {
		    // multicore and multithreaded
		    // tpanl holds all the core panels
		    JPanel tpnl = new JPanel(new GridLayout());
		    for (ProcessorCore core : chip.getCores()) {
			Set<Kstat> kscore =
			    ProcessorTree.makeCpuKstats(core.infoStats());
			// mpanl is the outer panel for this core
			JPanel mpanl = new JPanel(new BorderLayout());
			mpanl.setBackground(mpanl.getBackground().brighter());
			// core aggregate goes on the left
			addChipNew(kscore, dcore, mpanl);
			// label across the bottom
			mpanl.add(new JLabel(coreText + core.getCoreid(),
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
	    for (ProcessorChip chip : proctree.getProcessorChips()) {
		JPanel cpanl = new JPanel(new BorderLayout());
		nchips++;
		Set<Kstat> kschip =
		    ProcessorTree.makeCpuKstats(chip.infoStats());
		addChipNew(kschip, dchip, cpanl);
		JLabel clabel = new JLabel(chipText + chip.getChipid(),
					   JLabel.CENTER);
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
     * above a line of labels.
     */
    private JPanel multiPanel(final Set<Kstat> kss) {
	JPanel ppanl = new JPanel();
	ppanl.setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	c.gridx = 0;
	c.gridy = 0;
	c.insets = new Insets(3, 1, 1, 3);
	c.weightx = 0.0;
	c.weighty = 0.5;
	c.fill = GridBagConstraints.BOTH;
	for (Kstat ks : kss) {
	    addProcessor(ks, ppanl, c);
	    c.gridx = c.gridx + 1;
	}
	c.gridx = 0;
	c.gridy = 1;
	c.weightx = 0.0;
	c.weighty = 0.0;
	for (Kstat ks : kss) {
	    JLabel jl = new JLabel(ks.getInstance(), JLabel.CENTER);
	    if (lfont == null) {
		Font f = jl.getFont();
		lfont = new Font(f.getName(), f.getStyle(),
				f.getSize() + fontScale);
	    }
	    jl.setFont(lfont);
	    ppanl.add(jl, c);
	    c.gridx = c.gridx + 1;
	}
	return ppanl;
    }

    private void populateOld() {
	if (proctree.isMulticore()) {
	    // iterate over chips, showing aggregates over cores
	    for (ProcessorChip chip : proctree.getProcessorChips()) {
		Set<Kstat> kschip =
		    ProcessorTree.makeCpuKstats(chip.infoStats());
		if (showChips) {
		    addChip(kschip, chipText, chip.getChipid(), dchip);
		}
		if (proctree.isThreaded()) {
		    for (ProcessorCore core : chip.getCores()) {
			Set<Kstat> kscore =
			    ProcessorTree.makeCpuKstats(core.infoStats());
			if (showCores) {
			    addChip(kscore, coreText, core.getCoreid(), dcore);
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
	    for (ProcessorChip chip : proctree.getProcessorChips()) {
		Set<Kstat> kschip =
		    ProcessorTree.makeCpuKstats(chip.infoStats());
		if (showChips) {
		    addChip(kschip, chipText, chip.getChipid(), dchip);
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

	SpringUtilities.makeCompactGrid(mainPanel, ncpu + naggr, 2, 6, 3, 3, 3);
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
    private void addProcessor(final Kstat ks) {
	addProcessor(ks, mainPanel);
    }

    /*
     * Add a processor to the given panel.
     */
    private void addProcessor(final Kstat ks, final JPanel ppanel) {
	addProcessor(ks, ppanel, (GridBagConstraints) null);
    }

    /*
     * Add a processor to the given panel.
     */
    private void addProcessor(final Kstat ks, final JPanel ppanel,
			      final GridBagConstraints c) {
	String scpu = ks.getInstance();
	// only add the label here if we're horizontal
	if (orientation != SwingConstants.VERTICAL) {
	    JLabel jl = new JLabel(cpuText + scpu);
	    if (lfont == null) {
		Font f = jl.getFont();
		lfont = new Font(f.getName(), f.getStyle(),
				f.getSize() + fontScale);
	    }
	    jl.setFont(lfont);
	    ppanel.add(jl);
	}
	KstatAccessoryPanel acp = (style == STYLE_CHART)
	    ? new AccessoryCpuChart(ks, 1, jkstat)
	    : new AccessoryCpuPanel(ks, 1, jkstat, orientation);
	kaplist.add(acp);
	// make wider and thinner than normal
	acp.setMinimumSize(dthread);
	acp.setPreferredSize(dthread);
	if (c == null) {
	    ppanel.add(acp);
	} else {
	    ppanel.add(acp, c);
	}
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
    private void addChip(final Set<Kstat> ksc, final String s, final Long l,
			 final Dimension d) {
	mainPanel.add(new JLabel(s + l));
	KstatAggregate ksa = new KstatAggregate(jkstat, ksc);
	KstatAccessoryPanel agp = (style == STYLE_CHART)
	    ? new AggregateCpuChart(ksa, 1, jkstat)
	    : new AggregateCpuPanel(ksa, 1, jkstat, orientation);
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
    private void addChipNew(final Set<Kstat> ksc, final Dimension d,
			    final JPanel panl) {
	KstatAggregate ksa = new KstatAggregate(jkstat, ksc);
	KstatAccessoryPanel agp =
	    new AggregateCpuPanel(ksa, 1, jkstat, orientation);
	kaplist.add(agp);
	agp.setMinimumSize(d);
	agp.setPreferredSize(d);
	panl.add(agp, BorderLayout.WEST);
    }

    @Override
    public void setDelay(final int i) {
	for (KstatAccessoryPanel kap : kaplist) {
	    kap.setDelay(i);
	}
    }

    @Override
    public void stopLoop() {
	for (KstatAccessoryPanel kap : kaplist) {
	    kap.stopLoop();
	}
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
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
		new KstatTableFrame("cpu_info", cpuID[i], "cpu_info" + cpuID[i],
				    -1, jkstat);
	    }
	    if (e.getSource() == extendedCpuItem[i]) {
		new KstatTableFrame("cpu_stat", cpuID[i], "cpu_stat" + cpuID[i],
				    1, jkstat);
	    }
	    if (e.getSource() == chartCpuItem[i]) {
		List<String> stats = Arrays.asList("user", "kernel", "idle");
		new KstatAreaChartFrame(jkstat,
			new Kstat("cpu_stat", Integer.parseInt(cpuID[i]),
				"cpu_stat" + cpuID[i]),
			stats, true);
	    }
	}
    }

    /**
     * Inner class to handle mouse popups.
     */
    static class PopupListener extends MouseAdapter {
	private JPopupMenu popup;

	PopupListener(final JPopupMenu popup) {
	    this.popup = popup;
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	    showPopup(e);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	    showPopup(e);
	}

	private void showPopup(final MouseEvent e) {
	    if (e.isPopupTrigger()) {
		popup.show(e.getComponent(), e.getX(), e.getY());
	    }
	}
    }


    // getopts would be nice
    private void parseArgs(final String[] args) {
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
    public static void main(final String[] args) {
	new JCpuState(args);
    }
}

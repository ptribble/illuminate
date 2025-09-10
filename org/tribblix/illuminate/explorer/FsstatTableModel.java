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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import uk.co.petertribble.jkstat.api.ChartableKstat;
import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.api.KstatFilter;
import uk.co.petertribble.jkstat.api.KstatSet;

/**
 * A Table model describing fsstat statistics.
 * @author Peter Tribble
 * @version 1.0
 */
public final class FsstatTableModel extends AbstractTableModel
		implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String VOP_PREFIX = "vopstats_";

    // standard choices for columns
    // read/write
    private static String deftitle;
    private static String[] defnames;
    /**
     * The currently displaying columns.
     */
    private String[] columnNames;
    /**
     * The short name for the currently displaying columns.
     */
    private String columnTitle;

    private static final Map<String, String[]> COLUMN_MAP;
    private transient List<ChartableKstat> allfsdata = new ArrayList<>();
    private transient List<ChartableKstat> fsdata = new ArrayList<>();
    private transient Map<ChartableKstat, String> fsnames = new HashMap<>();

    /**
     * A Timer to update the display in a loop.
     */
    private Timer timer;
    /**
     * The update delay, in ms.
     */
    private int delay = 1000;
    private transient JKstat jkstat;
    private transient KstatSet kss;
    private transient Mnttab mnttab;

    /**
     * Display mask, for filtering.
     */
    private int filtermask;
    // don't include anything with the ignore option
    private static final int MASK_IGNORE = 1;
    // only show those in the shown zone list
    // private static final int MASK_BYZONE = 2;
    // only show those in the shown fstype list
    private static final int MASK_BYTYPE = 4;
    // don't show aggregates which have no filesystems
    // private static final int MASK_MINAGGR = 8;
    // don't show any filesystem aggregates at all
    private static final int MASK_ALLAGGR = 16;
    // Lists to hold lists of zones, fstypes to show
    private transient List<String> showzones = new ArrayList<>();
    private transient List<String> showfstypes = new ArrayList<>();

    static {
	// standard choices for columns
	// read/write
	String title1 = "I/O operations";
	String[] names1 = {"nread", "nwrite", "nreaddir",
		"read_bytes", "write_bytes", "readdir_bytes"};
	// create/remove
	String title2 = "Create/Remove";
	String[] names2 = {"ncreate", "nremove", "nmkdir",
		"nrmdir", "nlink", "nsymlink"};
	// file status
	String title3 = "File access";
	String[] names3 = {"nlookup", "naccess", "npathconf",
		"nreadlink"};
	// attributes - this is exactly fsstat -a
	String title4 = "Attributes";
	String[] names4 = {"ngetattr", "nsetattr",
		 "ngetsecattr", "nsetsecattr"};
	// map - this is exactly fsstat -v
	String title5 = "fsstat -v";
	String[] names5 = {"nmap", "naddmap", "ndelmap",
		"ngetpage", "nputpage", "npageio"};
	COLUMN_MAP = new HashMap<>();
	COLUMN_MAP.put(title1, names1);
	COLUMN_MAP.put(title2, names2);
	COLUMN_MAP.put(title3, names3);
	COLUMN_MAP.put(title4, names4);
	COLUMN_MAP.put(title5, names5);
	deftitle = title1;
	defnames = names1;
    }

    /**
     * Create a Table Model to display fsstat kstats.
     *
     * @param jkstat a JKstat object
     * @param interval the desired update interval, in seconds
     */
    public FsstatTableModel(JKstat jkstat, int interval) {
	this(jkstat, interval, MASK_IGNORE | MASK_ALLAGGR);
    }

    /**
     * Create a Table Model to display fsstat kstats.
     *
     * @param jkstat a JKstat object
     * @param interval the desired update interval, in seconds
     * @param filtermask a mask to filter out undesired statistics
     */
    public FsstatTableModel(JKstat jkstat, int interval, int filtermask) {
	this.jkstat = jkstat;
	delay = interval * 1000;
	this.filtermask = filtermask;

	/*
	 * All fsstat kstats are under unix:0
	 */
	KstatFilter ksf = new KstatFilter(jkstat);
	ksf.addFilter("unix:0::");
	kss = new KstatSet(jkstat, ksf);
	// but we have to match the actual name
	for (Kstat ks : kss.getKstats()) {
	    if (ks.getName().startsWith(VOP_PREFIX)) {
		allfsdata.add(new ChartableKstat(jkstat, ks));
	    }
	}

	mnttab = new Mnttab();

	updateFilter();
	setNames(deftitle);
	startLoop();
    }

    /**
     * Determine whether ignored filesystems are shown.
     *
     * @param b true if ignored filesystems should be shown.
     */
    public void showIgnored(boolean b) {
	if (b) {
	    setMask(filtermask & ~MASK_IGNORE);
	} else {
	    setMask(filtermask | MASK_IGNORE);
	}
    }

    /**
     * Determine whether the aggregates of filesystem types are shown.
     *
     * @param b true if the aggregates by filesystem type should be shown.
     */
    public void showAggregates(boolean b) {
	if (b) {
	    setMask(filtermask & ~MASK_ALLAGGR);
	} else {
	    setMask(filtermask | MASK_ALLAGGR);
	}
    }

    /**
     * Determine whether some or all filesystem types are shown.
     *
     * @param b if true, only show the fs types in the showfstypes list
     */
    public void showTypes(boolean b) {
	if (b) {
	    setMask(filtermask & ~MASK_BYTYPE);
	} else {
	    setMask(filtermask | MASK_BYTYPE);
	}
    }

    /*
     * Set the filter mask.
     *
     * @param filtermask the new filter mask
     */
    private void setMask(int filtermask) {
	this.filtermask = filtermask;
	updateFilter();
	fireTableDataChanged();
    }

    /**
     * Start the timer loop, so that the table updates itself.
     */
    public void startLoop() {
	if (delay > 0) {
	    if (timer == null) {
		timer = new Timer(delay, this);
	    }
	    timer.start();
	}
    }

    /**
     * Stop the timer loop, so that the table will no longer be updated.
     */
    public void stopLoop() {
	if (timer != null) {
	    timer.stop();
	}
    }

    /**
     * Set the loop delay to be the specified number of seconds.
     * If a zero or negative delay is requested, stop the updates
     * and remember the previous delay.
     *
     * @param interval the desired delay interval in seconds.
     */
    public void setDelay(int interval) {
	if (interval <= 0) {
	    stopLoop();
	} else {
	    delay = interval * 1000;
	    if (timer != null) {
		timer.setDelay(delay);
	    }
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	updateKstat();
    }

    /**
     * Filter the displayed filesystems by zone.
     *
     * @param zs the name of the zone to filter on
     * @param b if true, add the given zone to the display list, else remove it
     */
    public void showZone(String zs, boolean b) {
	if (b) {
	    if (!showzones.contains(zs)) {
		showzones.add(zs);
	    }
	} else {
	    showzones.remove(zs);
	}
    }

    /**
     * Filter the displayed filesystems by filesystem type.
     *
     * @param fs the name of the filesystem type to filter on
     * @param b if true, add the given filesystem type to the display list,
     * else remove it
     */
    public void showFstype(String fs, boolean b) {
	if (b) {
	    if (!showfstypes.contains(fs)) {
		showfstypes.add(fs);
	    }
	} else {
	    showfstypes.remove(fs);
	}
    }

    /*
     * Initializes the active lists of kstats by filtering the full list.
     * Also initializes the list of filesystem names.
     */
    private void updateFilter() {
	fsdata.clear();
 	for (ChartableKstat cks : allfsdata) {
	    addIfFiltered(cks);
	}
    }

    /*
     * A separate method to check if we pass the filter, as this code is
     * called from both updateFilter() and updateKstat()
     */
    private void addIfFiltered(ChartableKstat cks) {
	Kstat ks = cks.getKstat();
	boolean doadd = true;
	String dev2 = ks.getName().substring(VOP_PREFIX.length());
	String dev3 = mnttab.getFSforDevice(dev2);

	// first check if we should ignore it
	if ((filtermask & MASK_IGNORE) != 0 && dev3 != null
	        && mnttab.getIgnore(dev3)) {
	    doadd = false;
	}

	// check if ignoring all aggregates
	// aggregates aren't mounted so dev3 is null
	if (doadd && (filtermask & MASK_ALLAGGR) != 0 && dev3 == null) {
	    doadd = false;
	}

	// FIXME filter on active aggregates
	// shouldn't this always be on?

	// filter on fstype
	// FIXME need to populate the showfstypes list
	if (doadd && (filtermask & MASK_BYTYPE) != 0 && dev3 != null) {
	    doadd = showfstypes.contains(mnttab.getFsType(dev3));
	}

	// FIXME filter on zonename

	if (doadd) {
	    fsdata.add(cks);
	    fsnames.put(cks, (dev3 == null) ? dev2 : dev3);
	}
    }

    /**
     * Update the table.
     */
    public void updateKstat() {
	/*
	 * Add any relevant new statistics.
	 */
	if (kss.chainupdate() != 0) {
	    // update mnttab first as the filter uses it
	    mnttab.update();
	    for (Kstat ks : kss.getAddedKstats()) {
		if (ks.getName().startsWith(VOP_PREFIX)) {
		    ChartableKstat cks = new ChartableKstat(jkstat, ks);
		    allfsdata.add(cks);
		    addIfFiltered(cks);
		}
	    }
	}

	Iterator<ChartableKstat> vki = allfsdata.iterator();
	while (vki.hasNext()) {
	    ChartableKstat cks = vki.next();
	    if (!cks.update()) {
		vki.remove();
		fsdata.remove(cks);
		fsnames.remove(cks);
	    }
	}
	fireTableDataChanged();
    }

    /**
     * Set the display type.
     *
     * @param s the display to show
     */
    public void setNames(String s) {
	columnTitle = s;
	columnNames = COLUMN_MAP.get(columnTitle);
	if (columnNames == null) {
	    columnNames = defnames;
	}
	fireTableStructureChanged();
    }

    /**
     * Return the name of the current display type.
     *
     * @return the current display type
     */
    public String currentTitle() {
	return columnTitle;
    }

    /**
     * Return the Set of the predefined display names.
     *
     * @return the Set of available display names
     */
    public Set<String> titles() {
	return COLUMN_MAP.keySet();
    }

    /**
     * Return the list of known filesystem types, from /etc/mnttab.
     *
     * @return A List of filesystem types that are currently mounted
     */
    public List<String> getFstypeList() {
	return mnttab.getFstypeList();
    }

    @Override
    public int getColumnCount() {
	// 1 extra for the device column
	return columnNames.length + 1;
    }

    @Override
    public int getRowCount() {
	return fsdata.size();
    }

    @Override
    public String getColumnName(int col) {
	return (col == columnNames.length) ? "Device" : columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
	return (col == columnNames.length)
	    ? fsnames.get(fsdata.get(row))
	    : fsdata.get(row).getRate(columnNames[col]);
    }

    @Override
    public Class<?> getColumnClass(int c) {
	return (c ==  columnNames.length) ? String.class : Double.class;
    }
}

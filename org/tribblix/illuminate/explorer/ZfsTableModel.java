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

import java.util.Set;
import java.util.TreeSet;
import javax.swing.table.AbstractTableModel;

/**
 * A Table Model representing zfs properties.
 * @author Peter Tribble
 * @version 1.0
 */
public final class ZfsTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNS = {"PROPERTY", "VALUE", "SOURCE"};
    /**
     * The number of rows in the table, equal to the number of properties.
     */
    private int nrows;
    private transient Zproperty[] zprops;

    /**
     * Create a ZfsTableModel from the given input.
     *
     * @param props a Set of zfs properties whose output will be converted to
     * tabular form
     */
    public ZfsTableModel(final Set<Zproperty> props) {
	nrows = props.size();
	zprops = new TreeSet<>(props).toArray(new Zproperty[0]);
    }

    @Override
    public int getColumnCount() {
	return COLUMNS.length;
    }

    @Override
    public int getRowCount() {
	return nrows;
    }

    @Override
    public Object getValueAt(final int row, final int col) {
	Zproperty zp = zprops[row];
	if (col == 0) {
	    return zp.getProperty();
	} else if (col == 1) {
	    return zp.getValue();
	} else if (col == 2) {
	    return zp.getSource();
	} else {
	    return "Oops!";
	}
    }

    @Override
    public String getColumnName(final int col) {
	return COLUMNS[col];
    }

    @Override
    public Class<?> getColumnClass(final int c) {
	return String.class;
    }
}

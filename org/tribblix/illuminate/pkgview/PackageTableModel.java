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

package org.tribblix.illuminate.pkgview;

import java.util.List;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * A TableModel describing SVR4 packages.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public final class PackageTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private transient List<SVR4Package> pkgs;

    /**
     * Columns to show.
     */
    private String[] columnNames = {"Name", "Version", "Description"};

    /**
     * Create a new PackageTableModel.
     *
     * @param plist a PkgList
     */
    public PackageTableModel(final PkgList plist) {
	pkgs = new ArrayList<>(plist);
    }

    @Override
    public int getColumnCount() {
	return columnNames.length;
    }

    @Override
    public int getRowCount() {
	return pkgs.size();
    }

    @Override
    public String getColumnName(final int col) {
	return columnNames[col];
    }

    /**
     * Return the appropriate data.
     *
     * @see #setValueAt
     *
     * @param row the int row of the selected cell
     * @param col the int column of the selected cell
     *
     * @return the Object at the selected cell
     */
    @Override
    public Object getValueAt(final int row, final int col) {
	SVR4Package pkg = pkgs.get(row);
	if (col == 0) {
	    return pkg.getName();
	} else if (col == 1) {
	    return pkg.getVersion();
	} else {
	    return pkg.getDescription();
	}
    }

    @Override
    public Class<?> getColumnClass(final int col) {
	return String.class;
    }

    /**
     * Return the package at the selected row.
     *
     * @param row the row to select
     *
     * @return the SVR4Package on the given row
     */
    public SVR4Package getPackageAtRow(final int row) {
	return pkgs.get(row);
    }
}

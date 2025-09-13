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

import org.tribblix.illuminate.InfoCommand;
import javax.swing.table.AbstractTableModel;

/**
 * A Table Model representing the output from a command.
 * @author Peter Tribble
 * @version 1.0
 */
public final class CommandTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    /**
     * The names of the columns.
     */
    private String[] columnNames;
    /**
     * Parsed textual data from the command.
     */
    private String[][] data;
    /**
     * How many rows of data we have.
     */
    private int nrows;

    /**
     * Create a CommandTableModel from the given input. It is assumed
     * that the input String consists of multiple lines, with the first
     * line containing the headers and subsequent lines containing data.
     * It is further assumed that all lines contain the same number of
     * data fields.
     *
     * @param ic The InfoCommand whose output will be converted to tabular
     * form
     */
    public CommandTableModel(final InfoCommand ic) {
	this(ic, 0);
    }

    /**
     * Create a CommandTableModel from the given input. It is assumed
     * that the input consists of multiple lines, with the first
     * line containing the headers and subsequent lines containing data.
     * It is further assumed that all lines contain the same number of
     * data fields.
     *
     * @param ic The InfoCommand whose output will be converted to tabular
     * form
     * @param colmax The maximum number of columns. Columns beyond this
     * will be combined.
     */
    public CommandTableModel(final InfoCommand ic, final int colmax) {
	String[] rows = ic.getOutputLines();
	populateModel(rows, rows[0], colmax);
    }

    /**
     * Create a CommandTableModel from the given input. It is assumed
     * that the input consists of multiple lines, with the first
     * line containing the headers and subsequent lines containing data.
     * It is further assumed that all lines contain the same number of
     * data fields. This form replaces the header from the command
     * with an user-specified string for finer control.
     *
     * @param ic The InfoCommand whose output will be converted to tabular
     * form
     * @param hdr the header string to be used
     * @param colmax The maximum number of columns. Columns beyond this
     * will be combined.
     */
    public CommandTableModel(final InfoCommand ic, final String hdr,
			     final int colmax) {
	populateModel(ic.getOutputLines(), hdr, colmax);
    }

    private void populateModel(final String[] rows, final String hdr,
			       final int colmax) {
	columnNames = hdr.trim().split("\\s+", colmax);
	nrows = rows.length - 1;
	data = new String[nrows][columnNames.length];
	for (int i = 1; i < rows.length; i++) {
	    String[] items = rows[i].trim().split("\\s+", colmax);
	    // and if not the right length?
	    if (items.length == columnNames.length) {
		data[i - 1] = items;
	    }
	}
    }

    @Override
    public int getColumnCount() {
	return columnNames.length;
    }

    @Override
    public int getRowCount() {
	return nrows;
    }

    @Override
    public Object getValueAt(final int row, final int col) {
	return data[row][col];
    }

    @Override
    public String getColumnName(final int col) {
	return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(final int c) {
	return String.class;
    }
}

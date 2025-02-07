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

package org.tribblix.illuminate;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import org.tribblix.illuminate.helpers.RunInXterm;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

/**
 * IllumosToolsMenu - standard toolbar menu for illuminate tools.
 * @author Peter Tribble
 * @version 1.0
 */
public final class IllumosToolsMenu extends JMenu implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * A Map to track the tools that have been successfully added.
     */
    private final Map<String, String> toolMap = new HashMap<>();

    /**
     * Create an IllumosToolsMenu.
     */
    public IllumosToolsMenu() {
	super(IlluminateResources.getString("TOOLS.TEXT"));
	setMnemonic(KeyEvent.VK_T);

	addItem("Top", "/usr/bin/top", (String) null);
	addItem("Htop", "/usr/bin/htop", (String) null);
	addItem("Prstat", "/usr/bin/prstat", (String) null);
	addItem("Mpstat", "/usr/bin/mpstat", "1");
	addItem("Vmstat", "/usr/bin/vmstat", "1");
	addItem("IOstat", "/usr/bin/iostat", "-xnz 1");
    }

    /*
     * Add a command to the tools menu. It is only added if the executable
     * exists, and if it hasn't been added already. If called multiple times
     * with different paths, the first location found will be used.
     */
    private void addItem(String text, String cmd, String args) {
	if (new File(cmd).exists()
			&& !toolMap.containsKey(text)) {
	    toolMap.put(text, (args == null) ? cmd : cmd + " " + args);
	    JMenuItem ji = new JMenuItem(text);
	    ji.addActionListener(this);
	    add(ji);
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	new RunInXterm(toolMap.get(((JMenuItem) e.getSource()).getText()));
    }
}

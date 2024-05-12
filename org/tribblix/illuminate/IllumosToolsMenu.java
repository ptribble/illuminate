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
public class IllumosToolsMenu extends JMenu implements ActionListener {

    /**
     * A Map to track the tools that have been successfully added.
     */
    private final Map <String, String> toolMap = new HashMap<>();

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
	if (new File(cmd).exists() &&
			!toolMap.containsKey(text)) {
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

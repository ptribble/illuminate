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
 * Copyright 2026 Peter Tribble
 *
 */

package org.tribblix.illuminate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import org.tribblix.illuminate.helpers.ManFrame;
import uk.co.petertribble.jingle.JingleTextPane;

/**
 * An informational panel, containing a list of items for which information
 * can be shown on the left and a panel showing the informational output on
 * the right.
 */
public final class InfoCommandPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The JingleTextPane where we send output.
     */
    private final JingleTextPane tp;
    /**
     * A label for the current command.
     */
    private final JLabel jcl;
    /**
     * A JButton to display the manual page for the current command.
     */
    private final JButton jmb;
    /**
     * Track the current command we're displaying.
     */
    private transient InfoCommand currentCmd;

    /**
     * Display an information panel.
     */
    public InfoCommandPanel() {
	setLayout(new BorderLayout());

	JList<InfoCommand> ilist = new InfoJList(new InfoCommandList());
	ilist.addMouseListener(mouseListener);
	ilist.addKeyListener(keyListener);

	JPanel jp = new JPanel(new BorderLayout());
	JToolBar jtb = new JToolBar();
	jtb.setFloatable(false);
	jtb.setRollover(true);
	jtb.setLayout(new BorderLayout());
	jcl = new JLabel(IlluminateResources.getString("INFO.OUTPUT.TEXT"));
	jtb.add(jcl, BorderLayout.LINE_START);
	jtb.addSeparator();
	jmb = new JButton(IlluminateResources.getString("ILLUMINATE.MAN.TEXT"));
	jmb.setEnabled(false);
	jmb.addActionListener(this);
	jtb.add(jmb, BorderLayout.LINE_END);
	jp.add(jtb, BorderLayout.PAGE_START);
	tp = new JingleTextPane();
	jp.add(new JScrollPane(tp), BorderLayout.CENTER);

	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		new JScrollPane(ilist), jp);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(150);
	add(psplit);
    }

    void setInfo(final InfoCommand ic) {
	currentCmd = ic;
	jcl.setText(ic.infoLabel());
	jmb.setEnabled(ic.getManpage() != null);
	Cursor c = getCursor();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	tp.setText("<pre>" + ic.getOutput() + "</pre>");
	setCursor(c);
    }

    private void showMan() {
	if (currentCmd != null && currentCmd.getManpage() != null) {
	    new ManFrame(currentCmd.getManpage());
	}
    }

    /**
     * A MouseListener so that clicking on a command in the menu will show its
     * output.
     */
    MouseListener mouseListener = new MouseAdapter() {
	@Override
	public void mouseClicked(final MouseEvent e) {
	    @SuppressWarnings("unchecked")
	    JList<InfoCommand> source = (JList<InfoCommand>) e.getSource();
	    setInfo(source.getSelectedValue());
	}
    };

    /**
     * A KeyListener so that selecting a command in the menu will show its
     * output.
     */
    KeyListener keyListener = new KeyAdapter() {
	@Override
	public void keyPressed(final KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		@SuppressWarnings("unchecked")
		JList<InfoCommand> source =
			(JList<InfoCommand>) e.getSource();
		setInfo(source.getSelectedValue());
	    }
	}
    };

    @Override
    public void actionPerformed(final ActionEvent e) {
	if (e.getSource() == jmb) {
	    showMan();
	}
    }
}

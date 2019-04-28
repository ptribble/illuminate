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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.tribblix.illuminate.helpers.ManFrame;
import uk.co.petertribble.jingle.JingleTextPane;

/**
 * An informational panel, containing a list of items for which information
 * can be shown on the left and a panel showing the informational output on
 * the right.
 */
public class InfoCommandPanel extends JPanel implements ActionListener {

    private JingleTextPane tp;
    private JLabel jcl;
    private JButton jmb;
    private InfoCommand currentCmd;

    /**
     * Display an information panel.
     */
    public InfoCommandPanel() {
	setLayout(new BorderLayout());

	JList <InfoCommand> ilist = new InfoJList(new InfoCommandList());
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

    private void setInfo(InfoCommand ic) {
	currentCmd = ic;
	jcl.setText(ic.infoLabel());
	jmb.setEnabled(ic.getManpage() != null);
	Cursor c = getCursor();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	tp.setText("<pre>" + ic.getOutput() + "</pre>");
	setCursor(c);
    }

    private void showMan() {
	if (currentCmd != null) {
	    if (currentCmd.getManpage() != null) {
		new ManFrame(currentCmd.getManpage());
	    }
	}
    }

    MouseListener mouseListener = new MouseAdapter() {
	public void mouseClicked(MouseEvent e) {
	    @SuppressWarnings("unchecked")
	    JList <InfoCommand> source = (JList <InfoCommand>) e.getSource();
	    setInfo(source.getSelectedValue());
	}
    };

    KeyListener keyListener = new KeyAdapter() {
	public void keyPressed(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		@SuppressWarnings("unchecked")
		JList <InfoCommand> source =
			(JList <InfoCommand>) e.getSource();
		setInfo(source.getSelectedValue());
	    }
	}
    };

    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == jmb) {
	    showMan();
	}
    }
}

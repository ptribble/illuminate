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

package org.tribblix.illuminate.helpers;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * RunCommand - run a command and return the output.
 * @author Peter Tribble
 * @version 1.0
 */
public final class RunCommand {

    private StringBuilder stdout; //NOPMD
    private StringBuilder stderr; //NOPMD

    /**
     * Run a command and record its output.
     *
     * @param cmdarray The command to run
     */
    public RunCommand(String[] cmdarray) {
	stdout = new StringBuilder();
	stderr = new StringBuilder();
	try {
	    Process p = Runtime.getRuntime().exec(cmdarray,
				(String[]) null, new File("/tmp"));

	    try (BufferedReader reader1 =
		new BufferedReader(new InputStreamReader(p.getInputStream(),
						StandardCharsets.UTF_8));
		    BufferedReader reader2 =
		 new BufferedReader(new InputStreamReader(p.getErrorStream(),
						StandardCharsets.UTF_8))) {

		String s;
		while ((s = reader1.readLine()) != null) {
		    stdout.append(s).append('\n');
		}
		while ((s = reader2.readLine()) != null) {
		    stderr.append(s).append('\n');
		}
		try {
		    p.waitFor();
		} catch (InterruptedException ie) { }
	    } catch (IOException ioe1) { }
	} catch (IOException ioe) { }
    }

    /**
     * Returns the standard output.
     *
     * @return The contents of stdout
     */
    public String getOut() {
	return stdout.toString();
    }

    /**
     * Returns the standard error.
     *
     * @return The contents of stderr
     */
    public String getErr() {
	return stderr.toString();
    }
}

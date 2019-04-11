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

import java.util.*;
import java.io.File;
import org.tribblix.illuminate.helpers.RunCommand;
import uk.co.petertribble.jumble.JumbleFile;

/**
 * SmfUtils - shows available SMF services.
 * @author Peter Tribble
 * @version 1.0
 */
public class SmfUtils {

    private Set <SmfService> services;
    private Map <String, SmfService> serviceMap;
    private Map <String, Set <SmfService>> statusMap;

    public SmfUtils() {
	services = new TreeSet <SmfService> ();
	serviceMap = new HashMap <String, SmfService> ();
	statusMap = new LinkedHashMap <String, Set <SmfService>> ();

	RunCommand svcs = new RunCommand("/usr/bin/svcs -aH");
	// parse the svcs output to get the service name and status
	StringTokenizer st = new StringTokenizer(svcs.getOut(), "\n");
	while (st.hasMoreTokens()) {
	    String[] ds = st.nextToken().split("\\s+", 3);
	    SmfService serv = new SmfService(ds[2], ds[0]);
	    serviceMap.put(ds[2], serv);
	    services.add(serv);

	    Set <SmfService> statset = statusMap.get(ds[0]);
	    if (statset == null) {
		statset = new TreeSet <SmfService> ();
		statusMap.put(ds[0], statset);
	    }
	    statset.add(serv);
	}
    }

    /**
     * Get all services.
     *
     * @return the Set of all services
     */
    public Set <SmfService> getServices() {
	return services;
    }

    /**
     * Get a Set of all services in the given status.
     *
     * @param status the status to return services for
     *
     * @return the Set of SmfServices in the given status
     */
    public Set <SmfService> getServices(String status) {
	return statusMap.get(status);
    }

    /**
     * Produce an html formatted table describing the given service.
     *
     * @param svc the service to describe
     *
     * @return an html formatted table describing the given service
     */
    public String getHtmlInfo(SmfService svc) {
	String status = svc.getStatus();
	if ("legacy_run".equals(status)) {
	    StringBuilder sb = new StringBuilder();
	    File fl = getScriptFile(svc);
	    sb.append("<p bgcolor=\"#cccccc\"><b>");
	    if (fl == null) {
		sb.append("This is an unknown legacy script.</b></p>");
	    } else {
		sb.append("This is a legacy script, file name ");
		sb.append(fl.getPath());
		sb.append("</b></p><pre>\n\n\nScript Contents:\n\n");
		sb.append(JumbleFile.getStringContents(fl));
		sb.append("</pre>\n");
	    }
	    return sb.toString();
	} else {
	    return "<pre>" + svc.getExplanation() + "</pre>";
	}
    }

    /**
     * Produce an html formatted table containing dependency information for
     * the given service.
     *
     * @param svc the service to display dependency information for
     *
     * @return  an html formatted table containing dependency information for
     * the given service
     */
    public String getDepInfo(SmfService svc) {
	if ("legacy_run".equals(svc.getStatus())) {
	    return null;
	}
	StringBuilder sb = new StringBuilder();
	sb.append("<h3>").append(svc.getName()).append("</h3>");
	sb.append("<p bgcolor=\"#cccccc\">");
	sb.append("<b>Services this service depends on</b></p>");
	sb.append("<pre>").append(svc.getDependencies()).append("</pre>");
	sb.append("<p bgcolor=\"#cccccc\">");
	sb.append("<b>Services that depend on this service</b></p>");
	sb.append("<pre>").append(svc.getDependents()).append("</pre>");
	return sb.toString();
    }

    /*
     * The legacy FMRIs are constructed from the actual filenames, but
     * modified. In particular, any "." is replaced by "_". So we look for the
     * original by trying to substitute back the other way. If it works we
     * return the File, and if we run out of substitutions and still can't
     * find a file, return null.
     */
    private File getScriptFile(SmfService svc) {
	// start with the FMRI, strip the leading svc: off
	String s = svc.getFMRI().substring(4);
	String script = s.replaceFirst("_", ".");
	File f = new File(script);
	if (f.exists()) {
	    return f;
	}
	while (!script.equals(s)) {
	    s = script;
	    script = s.replaceFirst("_", ".");
	    f = new File(script);
	    if (f.exists()) {
		return f;
	    }
	}
	return null;
    }
}

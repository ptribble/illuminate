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

/**
 * ZapRepository - describe and query a zap repository.
 * @author Peter Tribble
 * @version 1.0
 */
public class ZapRepository {

    // cannot be final as they're assigned in a loop
    private String repoNAME;
    private String repoDESC;
    private String repoURL;
    private String repoSIGNED;

    /**
     * Create a zap repository configuration.
     *
     * @param pkghdl a PackageHandler for this OS image
     * @param repo the repository represented by this ZapRepository
     */
    public ZapRepository(PackageHandler pkghdl, String repo) {
	for (String line : pkghdl.getRepository(repo)) {
	    String[] ds = line.split("=", 2);
	    if ("NAME".equals(ds[0])) {
		repoNAME = ds[1];
	    } else if ("DESC".equals(ds[0])) {
		repoDESC = ds[1];
	    } else if ("URL".equals(ds[0])) {
		repoURL = ds[1];
	    } else if ("SIGNED".equals(ds[0])) {
		repoSIGNED = ds[1];
	    }
	}
    }

    /**
     * Get the name of this zap repository.
     *
     * @return the zap repository's name.
     */
    public String getName() {
	return repoNAME;
    }

    /**
     * Get the description of this zap repository.
     *
     * @return the zap repository's description.
     */
    public String getDescription() {
	return repoDESC;
    }

    /**
     * Get the URL of this zap repository.
     *
     * @return the zap repository's URL.
     */
    public String getURL() {
	return repoURL;
    }

    /**
     * Get the signer of packages in this zap repository, if any.
     *
     * @return the signer of packages in this zap repository.
     */
    public String getSIGNED() {
	return repoSIGNED;
    }
}

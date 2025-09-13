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

import java.util.ResourceBundle;

/**
 * Manage text resources for pkgview.
 *
 * @author Peter Tribble
 */
public final class PkgResources {

    private static final ResourceBundle PKGRES =
			ResourceBundle.getBundle("properties/pkgview");

    private PkgResources() {
    }

    /**
     * Get a text resource.
     *
     * @param key the key of the String to be looked up
     *
     * @return the text resource matching the specified key
     */
    public static String getString(final String key) {
	return PKGRES.getString(key);
    }
}

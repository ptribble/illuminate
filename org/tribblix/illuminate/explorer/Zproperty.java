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

/**
 * Zproperty - represent a ZFS property.
 * @author Peter Tribble
 * @version 1.0
 */
public class Zproperty implements Comparable<Zproperty> {

    private String property;
    private String value;
    private String source;

    /**
     * Create a new Zproperty object, to store details of a ZFS property.
     *
     * @param prop the name of the ZFS property
     * @param val the value of the ZFS property
     * @param src the source of the ZFS property
     */
    public Zproperty(final String prop, final String val,
		     final String src) {
	property = prop;
	value = val;
	source = src;
    }

    /**
     * Return the name of this property.
     *
     * @return the name of the property described by this Zproperty
     */
    public String getProperty() {
	return property;
    }

    /**
     * Return the value of this property.
     *
     * @return the value of the property described by this Zproperty
     */
    public String getValue() {
	return value;
    }

    /**
     * Return the source of this property.
     *
     * @return the source of the property described by this Zproperty
     */
    public String getSource() {
	return source;
    }

    /**
     * Return the String representation of this ZFS property.
     *
     * @return a summary of this property
     */
    @Override
    public String toString() {
	return property + "|" + value + "|" + source;
    }

    /**
     * Compare this Zproperty to the given Zproperty. Note that this
     * comparison, and the implementation of equals and hashCode that
     * are consistent with it, are only valid for properties of the same
     * dataset - the same properties from different datasets should not
     * be compared.
     *
     * @param otherzprop the Zproperty to be compared
     *
     * @return an int representing whether this Zproperty's name is less
     * than, equal to, or greater than the name of the supplied Zproperty
     */
    @Override
    public int compareTo(final Zproperty otherzprop) {
	return property.compareTo(otherzprop.getProperty());
    }

    /**
     * For Comparable.
     *
     * @param o the object to check for equality with this Zproperty
     *
     * @return whether the given object is equal to this Zproperty
     */
    @Override
    public boolean equals(final Object o) {
	if (o instanceof Zproperty) {
	    Zproperty otherzprop = (Zproperty) o;
	    return property.equals(otherzprop.getProperty());
        }
        return false;
    }

    /**
     * For Comparable. As the unique property of an Zproperty is its
     * name, use the hashCode of the underlying node name.
     *
     * @return a unique hashcode for this Zproperty
     */
    @Override
    public int hashCode() {
	return property.hashCode();
    }
}

/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import java.util.List;

public class SubmittersEntity {

    private int count;
    private List<Object> users;

    public SubmittersEntity(int count, List<Object> users) {
        this.count = count;
        this.users = users;
    }

    public int getCount() {
        return count;
    }

    public List<Object> getUsers() {
        return users;
    }
}
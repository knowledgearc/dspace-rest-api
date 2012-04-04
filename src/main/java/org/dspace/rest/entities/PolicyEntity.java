/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.authorize.ResourcePolicy;

import java.sql.SQLException;

public class PolicyEntity extends PolicyEntityId{

    private String action;
    private Object user;
    private Object group;

    public PolicyEntity() {
    }

    public PolicyEntity(ResourcePolicy c) throws SQLException {
        super(c);
        this.action = c.getActionText();
        this.user = c.getEPerson() != null ? new UserEntityTrim(c.getEPerson()) : null;
        this.group = c.getGroup() != null ? c.getGroup().getMemberGroups().length > 0 ? new GroupEntity(c.getGroup()) : new GroupEntityTrim(c.getGroup()) : null;
    }

    public String getAction() {
        return action;
    }

    public Object getUser() {
        return user;
    }

    public Object getGroup() {
        return group;
    }
}
/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.authorize.ResourcePolicy;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;

import java.sql.SQLException;

public class PolicyEntity {

    @EntityId
    private int id;
    private String name;
    private int resourceId;
    private int actionId;
    private String action;
    private Object eperson;
    private Object group;

    public PolicyEntity() {
    }

    public PolicyEntity(ResourcePolicy c, String name) throws SQLException {

        this.id = c.getID();
        this.name = name;
        this.resourceId = c.getResourceID();
        this.actionId = c.getAction();
        this.action = c.getActionText();
        this.eperson = c.getEPerson() != null ? new UserEntityTrim(c.getEPerson()) : null;
        this.group = c.getGroup() != null ? new GroupEntity(c.getGroup()) : null;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getActionId() {
        return actionId;
    }

    public String getAction() {
        return action;
    }

    public Object getEperson() {
        return eperson;
    }

    public Object getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "id:" + this.id + ", stuff.....";
    }
}
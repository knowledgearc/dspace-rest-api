/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.authorize.ResourcePolicy;
import org.dspace.core.Context;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;

import java.sql.SQLException;

/**
 * Entity describing policy
 * @author Lewis
 */
public class PolicyEntity {

    @EntityId
    private int id;
    private String name;
    private int resourceId;
    private int actionId;
    private String action;
    private Object eperson;
    private Object group;

    public PolicyEntity(String uid, Context context, int level, UserRequestParams uparams) {
//        try {
//
//            //context.complete();
//        } catch (SQLException ex) {
//            throw new EntityException("Internal server error", "SQL error", 500);
//        } catch (AuthorizeException ex) {
//            throw new EntityException("Forbidden", "Forbidden", 403);
//        }

    }

    public PolicyEntity(ResourcePolicy c, String name, int level, UserRequestParams uparams) throws SQLException {

        this.id = c.getID();
        this.name = name;
        this.resourceId = c.getResourceID();
        this.actionId = c.getAction();
        this.action = c.getActionText();
        this.eperson = c.getEPerson()!=null?new UserEntity(c.getEPerson()):null;
        this.group = c.getGroup()!=null?new GroupEntity(c.getGroup()):null;


    }

    public PolicyEntity() {
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

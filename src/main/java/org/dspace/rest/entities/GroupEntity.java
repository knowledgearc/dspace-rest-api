/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.rest.util.UserRequestParams;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupEntity extends GroupEntityTrim {

    private List<Object> groups = new ArrayList<Object>();
    private List<Object> users = new ArrayList<Object>();

    public GroupEntity(String uid, Context context, int level, UserRequestParams uparams) throws SQLException {
    }

    public GroupEntity(Group egroup, int level, UserRequestParams uparams) throws SQLException {
    }

    public GroupEntity() {
    }

    public GroupEntity(Group egroup) {
        super(egroup);

        for (EPerson member : egroup.getMembers()) {
            users.add(new UserEntityTrim(member));
        }
        for (Group group : egroup.getMemberGroups()) {
            if (group.getMemberGroups().length > 0) {
                groups.add(new GroupEntity(group));
            } else {
                groups.add(new GroupEntityTrim(group));
            }
        }
    }

    public List<?> getUsers() {
        return this.users;
    }

    public List<?> getGroups() {
        return this.groups;
    }
}

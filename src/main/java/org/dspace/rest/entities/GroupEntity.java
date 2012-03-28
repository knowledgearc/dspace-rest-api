/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupEntity extends GroupEntityTrim {

    private List<Object> groups = new ArrayList<Object>();
    private List<Object> users = new ArrayList<Object>();

    public GroupEntity() {
    }

    public GroupEntity(String uid, Context context) throws SQLException {
        super(uid, context);
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

    public String addUser(EntityReference ref, Map<String, Object> inputVar, Context context) {
        String result;

        String email = (String) inputVar.get("email");
        String password = (String) inputVar.get("password");
        String firstName = (String) inputVar.get("firstName");
        String lastName = (String) inputVar.get("lastName");
        String phone = (String) inputVar.get("phone");
        String netId = (String) inputVar.get("netId");
        String language = (String) inputVar.get("language");
        boolean canLogIn = "true".equals(inputVar.get("canLogIn"));
        boolean requireCertificate = "true".equals(inputVar.get("requireCertificate"));
        boolean selfRegistered = "true".equals(inputVar.get("selfRegistered"));

        try {
            Group group = Group.find(context, Integer.parseInt(ref.getId()));
            if (group != null) {
                if (EPerson.findByEmail(context, email) == null) {
                    EPerson ePerson = EPerson.create(context);
                    if (ePerson != null) {
                        result = String.valueOf(ePerson.getID());
                        ePerson.setEmail(email);
                        ePerson.setFirstName(firstName);
                        ePerson.setLastName(lastName);
                        if (password != null && !"".equals(password)) ePerson.setPassword(password);
                        ePerson.setMetadata("phone", phone);
                        ePerson.setNetid(netId);
                        ePerson.setLanguage(language);
                        ePerson.setCanLogIn(canLogIn);
                        ePerson.setRequireCertificate(requireCertificate);
                        ePerson.setSelfRegistered(selfRegistered);
                        ePerson.update();

                        group.addMember(ePerson);
                        group.update();
                    } else {
                        throw new EntityException("Internal server error", "Could not create ePerson", 500);
                    }
                } else {
                    throw new EntityException("Data error", "Duplicated ePerson", 500);
                }
            } else {
                throw new IllegalArgumentException("Invalid id:" + ref.getId());
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
        return result;
    }

    public List<?> getUsers() {
        return this.users;
    }

    public List<?> getGroups() {
        return this.groups;
    }
}
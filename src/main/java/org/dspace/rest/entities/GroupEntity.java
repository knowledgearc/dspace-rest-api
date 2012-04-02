/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.rest.util.UserRequestParams;
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

    public GroupEntity(String uid, Context context) {
        super(uid, context);
        for (EPerson member : res.getMembers()) {
            users.add(new UserEntityTrim(member));
        }
        for (Group group : res.getMemberGroups()) {
            if (group.getMemberGroups().length > 0) {
                groups.add(new GroupEntity(group));
            } else {
                groups.add(new GroupEntityTrim(group));
            }
        }
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

    public Object groups(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            Group res = Group.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, res, Constants.READ);

            for (Group group : res.getMemberGroups()) {
                if (group.getMemberGroups().length > 0) {
                    groups.add(new GroupEntity(group));
                } else {
                    groups.add(new GroupEntityTrim(group));
                }
            }
            return groups;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        }
    }

    public Object users(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            Group res = Group.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, res, Constants.READ);

            for (EPerson member : res.getMembers()) {
                users.add(new UserEntityTrim(member));
            }
            return users;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        }
    }

    public String createUser(EntityReference ref, Map<String, Object> inputVar, Context context) {
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

    public void assignGroup(EntityReference ref, Map<String, Object> inputVar, Context context) {

        String id = (String) inputVar.get("id");

        try {
            Group group = Group.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, group, Constants.WRITE);
            if (group != null) {
                Group eGroup = Group.find(context, Integer.parseInt(id));
                if (eGroup != null) {
                    group.addMember(eGroup);
                    group.update();
                } else {
                    throw new IllegalArgumentException("Invalid id:" + ref.getId());
                }
            } else {
                throw new IllegalArgumentException("Invalid id:" + ref.getId());
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void assignUser(EntityReference ref, Map<String, Object> inputVar, Context context) {

        String id = (String) inputVar.get("id");

        try {
            Group group = Group.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, group, Constants.WRITE);
            if (group != null) {
                EPerson ePerson = EPerson.find(context, Integer.parseInt(id));
                if (ePerson != null) {
                    group.addMember(ePerson);
                    group.update();
                } else {
                    throw new IllegalArgumentException("Invalid id:" + ref.getId());
                }
            } else {
                throw new IllegalArgumentException("Invalid id:" + ref.getId());
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void removeGroup(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Group group = Group.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, group, Constants.WRITE);
            if ((group != null)) {
                int eid = Integer.parseInt((String) inputVar.get("eid"));
                Group eGroup = Group.find(context, eid);
                if (eGroup != null) {
                    group.removeMember(eGroup);
                    group.update();
                }
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void removeUser(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Group group = Group.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, group, Constants.WRITE);
            if ((group != null)) {
                int eid = Integer.parseInt((String) inputVar.get("eid"));
                EPerson ePerson = EPerson.find(context, eid);
                if (ePerson != null) {
                    group.removeMember(ePerson);
                    group.update();
                }
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public List<?> getUsers() {
        return this.users;
    }

    public List<?> getGroups() {
        return this.groups;
    }
}
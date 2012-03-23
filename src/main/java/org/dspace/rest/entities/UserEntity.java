/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.authenticate.AuthenticationManager;
import org.dspace.authenticate.AuthenticationMethod;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.EPersonDeletionException;
import org.dspace.eperson.Group;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserEntity extends UserEntityId {

    private boolean canLogIn, requireCertificate, selfRegistered;
    private String email, firstName, lastName, fullName, phone, netId, language;
    private List<Object> groups = new ArrayList<Object>();

    public UserEntity(String uid, Context context, int level, UserRequestParams uparams) throws SQLException {
    }

    public UserEntity(String uid, Context context, UserRequestParams uparams) throws SQLException {
        super(uid, context);
        this.email = res.getEmail();
        this.firstName = res.getFirstName();
        this.lastName = res.getLastName();
        this.fullName = res.getFullName();
        this.canLogIn = res.canLogIn();
        this.requireCertificate = res.getRequireCertificate();
        this.selfRegistered = res.getSelfRegistered();
        this.phone = res.getMetadata("phone");
        this.netId = res.getNetid();
        this.language = res.getLanguage();

        Group[] gs = Group.allMemberGroups(context, res);
        for (Group g : gs) {
            this.groups.add(new GroupEntityTrim(g));
        }
    }

    public UserEntity() {
    }

    public UserEntity(EPerson eperson) {
        super(eperson);
        this.email = eperson.getEmail();
        this.firstName = eperson.getFirstName();
        this.lastName = eperson.getLastName();
        this.fullName = eperson.getFullName();
        this.canLogIn = eperson.canLogIn();
        this.requireCertificate = eperson.getRequireCertificate();
        this.selfRegistered = eperson.getSelfRegistered();
        this.phone = eperson.getMetadata("phone");
        this.netId = eperson.getNetid();
        this.language = eperson.getLanguage();
    }

    public UserEntity(EPerson eperson, Context context, UserRequestParams uparams) throws SQLException {
        super(eperson);
        this.email = eperson.getEmail();
        this.firstName = eperson.getFirstName();
        this.lastName = eperson.getLastName();
        this.fullName = eperson.getFullName();
        this.canLogIn = eperson.canLogIn();
        this.requireCertificate = eperson.getRequireCertificate();
        this.selfRegistered = eperson.getSelfRegistered();
        this.phone = eperson.getMetadata("phone");
        this.netId = eperson.getNetid();
        this.language = eperson.getLanguage();

        boolean groups = uparams.getGroups();

        Group[] gs = Group.allMemberGroups(context, eperson);
        for (Group g : gs) {
            this.groups.add(groups ? new GroupEntityTrim(g) : new GroupEntityId(g));
        }
    }

    public UserEntity(int id, String firstName, String lastName, String fullName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
    }

    public List<Object> groups(EntityReference ref, UserRequestParams uparams, Context context) throws SQLException {

        try {
            Integer id = Integer.parseInt(ref.getId());
            EPerson ePerson = EPerson.find(context, id);

            Group[] gs = Group.allMemberGroups(context, ePerson);
            for (Group g : gs) {
                this.groups.add(new GroupEntityTrim(g));
            }

            return this.groups;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public String create(EntityReference ref, Map<String, Object> inputVar, Context context) {
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
                } else {
                    throw new EntityException("Internal server error", "Could not create ePerson", 500);
                }
            } else {
                throw new EntityException("Internal server error", "Duplicated ePerson", 500);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        }
        return result;
    }

    public String edit(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            Integer id = Integer.parseInt(ref.getId());
            EPerson ePerson = EPerson.find(context, id);

            String email = getMapValue(inputVar, "email");
            String password = getMapValue(inputVar, "password");
            String firstName = getMapValue(inputVar, "firstName");
            String lastName = getMapValue(inputVar, "lastName");
            String phone = getMapValue(inputVar, "phone");
            String netId = getMapValue(inputVar, "netId");
            String language = getMapValue(inputVar, "language");
            String canLogIn = getMapValue(inputVar, "canLogIn");
            String requireCertificate = getMapValue(inputVar, "requireCertificate");
            String selfRegistered = getMapValue(inputVar, "selfRegistered");

            if (ePerson != null) {
                EPerson ep = EPerson.findByEmail(context, email);
                if (ep == null || ePerson.getEmail().equalsIgnoreCase(ep.getEmail())) {
                    if (email != null) ePerson.setEmail(email);
                    if (firstName != null) ePerson.setFirstName(firstName);
                    if (lastName != null) ePerson.setLastName(lastName);
                    if (password != null && !"".equals(password)) ePerson.setPassword(password);
                    if (phone != null) ePerson.setMetadata("phone", phone);
                    if (netId != null) ePerson.setNetid(netId);
                    if (language != null) ePerson.setLanguage(language);
                    if (canLogIn != null) ePerson.setCanLogIn("true".equals(canLogIn));
                    if (requireCertificate != null) ePerson.setRequireCertificate("true".equals(requireCertificate));
                    if (selfRegistered != null) ePerson.setSelfRegistered("true".equals(selfRegistered));
                    ePerson.update();
                } else {
                    throw new EntityException("Internal server error", "Duplicated ePerson", 500);
                }
            } else {
                throw new EntityException("Internal server error", "Could not update ePerson", 500);
            }
            return String.valueOf(ePerson.getID());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void remove(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            int id = Integer.parseInt(ref.getId());
            EPerson ePerson = EPerson.find(context, id);
            if ((ePerson != null)) {
                ePerson.delete();
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (EPersonDeletionException e) {
            throw new EntityException("Internal server error", "Deletion", 500);
        }
    }

    public String authenticate(EntityReference ref, Map<String, Object> inputVar, Context context) {
        String email = (String) inputVar.get("email");
        String password = (String) inputVar.get("password");
        try {

            int status = AuthenticationManager.authenticate(context, email, password, null, null);
            if (status == AuthenticationMethod.SUCCESS) {
                return String.valueOf(EPerson.findByEmail(context, email).getID());
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        }

        return "0";

    }

    private String getMapValue(Map<String, Object> inputVar, String key) {
        if (inputVar.containsKey(key)) {
            return inputVar.get(key) == null ? "" : (String) inputVar.get(key);
        }
        return null;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getLanguage() {
        return this.language;
    }

    public boolean getCanLogIn() {
        return canLogIn;
    }

    public boolean getRequireCertificate() {
        return this.requireCertificate;
    }

    public boolean getSelfRegistered() {
        return this.selfRegistered;
    }

    public String getPhone() {
        return phone;
    }

    public String getNetId() {
        return netId;
    }

    public List<Object> getGroups() {
        return groups;
    }

    public int compareTo(Object o1){
        return ((UserEntity)(o1)).getEmail().compareTo(this.getEmail()) * -1;
    }
}

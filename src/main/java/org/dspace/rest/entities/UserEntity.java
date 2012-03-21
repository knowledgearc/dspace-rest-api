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
import org.dspace.eperson.Group;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserEntity extends UserEntityId {

   private Boolean requireCertificate, selfRegistered;
   private String email, firstName, lastName, fullName,
           language, netId;
   private List<Object> groups = new ArrayList<Object>();

   public UserEntity(String uid, Context context, int level, UserRequestParams uparams) throws SQLException {
       super(uid, context);
	   try {
		   this.email = res.getEmail();
		   this.firstName = res.getFirstName();
		   this.fullName = res.getFullName();
		   this.requireCertificate = res.getRequireCertificate();
		   this.selfRegistered = res.getSelfRegistered();
		   this.language = res.getLanguage();
		   this.lastName = res.getLastName();
		   this.netId = res.getNetid();
           Group[] gs = Group.allMemberGroups(context,res);

           // check calling package/class in order to prevent chaining
           boolean includeFull = false;
           level++;
           if (level <= uparams.getDetail()) {
               includeFull = true;
           }

           for (Group g : gs) {
              this.groups.add(includeFull ? new GroupEntity(g, level, uparams) : new GroupEntityId(g));
           }
	//       context.complete();
        }
        catch (Exception ex) { }	   
}

   public UserEntity(Context context, int level, UserRequestParams uparams) throws SQLException {
   }

   public UserEntity(EPerson eperson) {
        super(eperson);
        this.email = eperson.getEmail();
        this.firstName = eperson.getFirstName();
        this.fullName = eperson.getFullName();
        this.requireCertificate = eperson.getRequireCertificate();
        this.selfRegistered = eperson.getSelfRegistered();
        this.language = eperson.getLanguage();
        this.lastName = eperson.getLastName();
        this.netId = eperson.getNetid();
   }

    public UserEntity(int id, String firstName, String lastName, String fullName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
    }

   public UserEntity() {
   }

    public String login(EntityReference ref, Map<String, Object> inputVar, Context context) {
        String email = (String) inputVar.get("email");
        String password = (String) inputVar.get("password");
        try {

            int status = AuthenticationManager.authenticate(context, email, password, null, null);
            if (status == AuthenticationMethod.SUCCESS) {
                return String.valueOf(EPerson.findByEmail(context, email.toLowerCase()).getID());
            }

        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        }

        return "0";

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

   @Override
   public int getId() {
       return this.id;
   }

   public String getLastName() {
       return this.lastName;
   }

   public String getLanguage() {
       return this.language;
   }

   public String getNetId() {
       return this.netId;
   }

   public boolean getRequireCertificate() {
       return this.requireCertificate;
   }

   public boolean getSelfRegistered() {
       return this.selfRegistered;
   }

   public List<Object> getGroups() {
       return groups;
   }

    @Override
    public String toString() {
        return "id:" + this.id + ", full_name:" + this.fullName;
    }
}

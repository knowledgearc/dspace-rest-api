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
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.dspace.eperson.EPerson;
import org.dspace.core.Context;
import java.sql.SQLException;
import java.util.Map;

import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.exception.EntityException;

/**
 * Entity describing users registered on the system
 * @see UserEntityId
 * @see EPerson
 * @author Bojan Suzic, bojan.suzic@gmail.com
 */
public class UserEntity extends UserEntityId {

   @EntityFieldRequired private String name;
   private Boolean requireCertificate, selfRegistered;
   private String handle, email, firstName, lastName, fullName,
           language, netId;
   private int type;

   public UserEntity(String uid, Context context, int level, UserRequestParams uparams) throws SQLException {
       super(uid, context);
       this.handle = res.getHandle();
       this.name = res.getName();
       this.type = res.getType();
       this.email = res.getEmail();
       this.firstName = res.getFirstName();
       this.fullName = res.getFullName();
       this.requireCertificate = res.getRequireCertificate();
       this.selfRegistered = res.getSelfRegistered();
       this.language = res.getLanguage();
       this.lastName = res.getLastName();
       this.netId = res.getNetid();
//       context.complete();
}

   public UserEntity(Context context, int level, UserRequestParams uparams) throws SQLException {
   }

   public UserEntity(EPerson eperson) throws SQLException {
        super(eperson);
        try {
            this.handle = eperson.getHandle();
            this.name = eperson.getName();
            this.type = eperson.getType();
            this.email = eperson.getEmail();
            this.firstName = eperson.getFirstName();
            this.fullName = eperson.getFullName();
            this.requireCertificate = eperson.getRequireCertificate();
            this.selfRegistered = eperson.getSelfRegistered();
            this.language = eperson.getLanguage();
            this.lastName = eperson.getLastName();
            this.netId = eperson.getNetid();
        }
        catch (Exception ex) { }
   }

    public UserEntity(int id, String firstName, String lastName, String fullName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
    }

   public UserEntity() {
       this.id = 111;
       this.handle = "123456789/0";
       this.name = "John";
       this.type = 7;
       this.email = "john.smith@johnsemail.com";
       this.firstName = "John";
       this.fullName = "John Smith";
       this.requireCertificate = false;
       this.selfRegistered = true;
       this.language = "en";
       this.lastName = "Smith";
       this.netId = "1";
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

   public String getName() {
       return this.name;
   }

   public String getHandle() {
       return this.handle;
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
   public int getType() {
      return this.type;
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



    @Override
    public String toString() {
        return "id:" + this.id + ", full_name:" + this.fullName;
    }

    @Override
    public int compareTo(Object o1){
        return ((UserEntity)(o1)).getName().compareTo(this.getName()) * -1;
    }

}

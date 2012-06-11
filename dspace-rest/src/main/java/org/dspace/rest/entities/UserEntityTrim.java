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

public class UserEntityTrim extends UserEntityId {

    private boolean canLogIn, requireCertificate, selfRegistered;
    private String email, firstName, lastName, fullName;
    private String phone, netId, language;

    public UserEntityTrim() {
    }

    public UserEntityTrim(String uid, Context context) {
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
    }

    public UserEntityTrim(EPerson eperson) {
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

    public UserEntityTrim(int id, String firstName, String lastName, String fullName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
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
}

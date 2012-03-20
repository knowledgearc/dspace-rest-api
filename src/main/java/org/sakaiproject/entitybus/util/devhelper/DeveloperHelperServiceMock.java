/**
 * $Id: DeveloperHelperServiceMock.java 17 2009-02-17 12:33:06Z azeckoski $
 * $URL: http://entitybus.googlecode.com/svn/tags/entitybus-1.0.8/utils/src/main/java/org/sakaiproject/entitybus/util/devhelper/DeveloperHelperServiceMock.java $
 * DeveloperHelperServiceMock.java - entity-broker - Jan 12, 2009 3:04:20 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.sakaiproject.entitybus.util.devhelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.azeckoski.reflectutils.ReflectUtils;

import org.sakaiproject.entitybus.EntityReference;

/**
 * This is the Mock for the developer helper service,
 * allows the service to always be available even when someone has not implemented it for the
 * system that is using EB
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class DeveloperHelperServiceMock extends AbstractDeveloperHelperService {

    private Map<String, Object> settings = new HashMap<String, Object>();
    @SuppressWarnings("unchecked")
    public <T> T getConfigurationSetting(String settingName, T defaultValue) {
        T value = null;
        Object o = settings.get(settingName);
        if (defaultValue == null) {
            value = (T) o;
        } else {
            ReflectUtils.getInstance().convert( settings.get(settingName), defaultValue.getClass());
        }
        return value;
    }

    public static String defaultLocationId = "home";
    public static String defaultLocationRef = GROUP_BASE + defaultLocationId;
    public String getCurrentLocationId() {
        return defaultLocationId;
    }

    public String getCurrentLocationReference() {
        return defaultLocationRef;
    }

    public static String defaultToolId = "myTool";
    public static String defaultToolRef = "/tool/" + defaultToolId;
    public String getCurrentToolReference() {
        return defaultToolRef;
    }

    public static String defaultUserId = "az11111";
    public static String defaultUserEid = "aaronz";
    public static String defaultUserRef = USER_BASE + defaultUserId;
    public static String currentUserRef = defaultUserRef;
    public String getCurrentUserId() {
        if (currentUserRef != null) {
            return EntityReference.getIdFromRef(currentUserRef);
        }
        return null;
    }

    public static String lastCurrentUser = null;
    public String restoreCurrentUser() {
        return lastCurrentUser;
    }

    public String setCurrentUser(String userReference) {
        String lastCurrentUser = currentUserRef;
        currentUserRef = userReference;
        return lastCurrentUser;
    }

    public String getCurrentUserReference() {
        return currentUserRef;
    }

    public static String defaultServerURL = "http://localhost:8080";
    public static String defaultPortalURL = defaultServerURL + "/portal";
    public String getPortalURL() {
        return defaultPortalURL;
    }

    public String getServerURL() {
        return defaultServerURL;
    }

    public String getUserRefFromUserEid(String userEid) {
        if (defaultUserEid.equals(userEid)) {
            return getUserRefFromUserId(defaultUserId);
        }
        // just pretend the eid and id are the same for testing (this is not going to be the case in production)
        return getUserRefFromUserId(userEid);
    }

    // PERMS

    public static String defaultEntityRef = "/thing/123";
    public Set<String> getEntityReferencesForUserAndPermission(String userReference,
            String permission) {
        HashSet<String> s = new HashSet<String>();
        if (defaultUserRef.equals(userReference) && defaultPermAllowed.equals(permission)) {
            s.add(defaultEntityRef);
        }
        return s;
    }

    public static String defaultPermAllowed = "allow1";
    public Set<String> getUserReferencesForEntityReference(String reference, String permission) {
        HashSet<String> s = new HashSet<String>();
        if (defaultPermAllowed.equals(permission)) {
            s.add(defaultUserRef);
        }
        return s;
    }

    public boolean isUserAdmin(String userReference) {
        if ("admin".equals(userReference)) {
            return true;
        }
        return false;
    }

    public boolean isUserAllowedInEntityReference(String userReference, String permission,
            String reference) {
        if (defaultUserRef.equals(userReference)) {
            if (defaultPermAllowed.equals(permission)) {
                if (defaultEntityRef.equals(reference)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static HashSet<String> registeredPerms = new HashSet<String>();
    public void registerPermission(String permission) {
        registeredPerms.add(permission);
    }

}

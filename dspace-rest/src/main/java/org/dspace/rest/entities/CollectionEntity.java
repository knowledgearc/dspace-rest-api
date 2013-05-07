/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.app.util.AuthorizeUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.content.Bitstream;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.rest.content.ContentHelper;
import org.dspace.rest.util.UserRequestParams;
import org.dspace.rest.util.Utils;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.exception.EntityException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionEntity extends CollectionEntityTrim {

    private String licence;
    private String short_description, introductory_text, copyright_text, side_bar_text;
    private String provenance;

    public CollectionEntity() {

    }

    public CollectionEntity(String uid, Context context) throws SQLException {
        super(uid, context);
        this.licence = res.getLicense();
        this.short_description = res.getMetadata("short_description");
        this.introductory_text = res.getMetadata("introductory_text");
        this.copyright_text = res.getMetadata("copyright_text");
        this.side_bar_text = res.getMetadata("side_bar_text");
        this.provenance = res.getMetadata("provenance_description");
    }

    public CollectionEntity(Collection collection) throws SQLException {

        super(collection);
        this.licence = collection.getLicense();
        this.short_description = collection.getMetadata("short_description");
        this.introductory_text = collection.getMetadata("introductory_text");
        this.copyright_text = collection.getMetadata("copyright_text");
        this.side_bar_text = collection.getMetadata("side_bar_text");
        this.provenance = collection.getMetadata("provenance_description");
    }

    public String createCollection(EntityReference ref, Map<String, Object> inputVar, Context context) {

        int communityId = 0;
        try {
            communityId = Integer.parseInt((String) inputVar.get("communityId"));
        } catch (NumberFormatException e) {
        }
        String name = (String) inputVar.get("name");
        String shortDescription = (String) inputVar.get("shortDescription");
        String copyrightText = (String) inputVar.get("copyrightText");
        String sidebarText = (String) inputVar.get("sidebarText");
        String introductoryText = (String) inputVar.get("introductoryText");
        String licence = (String) inputVar.get("licence");
        String provenance = (String) inputVar.get("provenance");

        try {
            if (communityId > 0) {
                Community community = Community.find(context, communityId);
                Collection collection = community.createCollection();
                if (collection != null) {
                    collection.setMetadata("name", name);
                    collection.setMetadata("short_description", shortDescription);
                    collection.setMetadata("introductory_text", introductoryText);
                    collection.setMetadata("copyright_text", copyrightText);
                    collection.setMetadata("side_bar_text", sidebarText);
                    collection.setMetadata("provenance_description", provenance);
                    collection.setLicense(licence);
                    collection.update();
                    return String.valueOf(collection.getID());
                } else {
                    throw new EntityException("Internal server error", "Could not create collection", 500);
                }
            } else {
                throw new EntityException("Internal server error", "Could not create collection", 500);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        }
    }

    public void editCollection(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            Integer id = Integer.parseInt(ref.getId());
            Collection collection = Collection.find(context, id);

            String name = Utils.getMapValue(inputVar, "name");
            String shortDescription = Utils.getMapValue(inputVar, "shortDescription");
            String copyrightText = Utils.getMapValue(inputVar, "copyrightText");
            String sidebarText = Utils.getMapValue(inputVar, "sidebarText");
            String introductoryText = Utils.getMapValue(inputVar, "introductoryText");
            String licence = Utils.getMapValue(inputVar, "licence");
            String provenance = Utils.getMapValue(inputVar, "provenance");

            if (collection != null) {
                if (name != null) collection.setMetadata("name", name);
                if (shortDescription != null) collection.setMetadata("short_description", shortDescription);
                if (copyrightText != null) collection.setMetadata("copyright_text", copyrightText);
                if (sidebarText != null) collection.setMetadata("side_bar_text", sidebarText);
                if (introductoryText != null) collection.setMetadata("introductory_text", introductoryText);
                if (provenance != null) collection.setMetadata("provenance_description", provenance);
                if (licence != null) collection.setLicense(licence);
                collection.update();
            } else {
                throw new EntityException("Internal server error", "Could not update collection", 500);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void removeCollection(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Collection collection = Collection.find(context, Integer.parseInt(ref.getId()));
            if ((collection != null)) {
                Community[] communities = collection.getCommunities();
                for (Community community : communities) {
                    community.removeCollection(collection);
                }
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (IOException ie) {
            throw new EntityException("Internal server error", "SQL error, cannot remove collection", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public String createAdmin(EntityReference ref, Map<String, Object> inputVar, Context context) {
        inputVar.put("action", Utils.ADMIN);
        return createRoles(ref, inputVar, context);
    }

    public String createSubmit(EntityReference ref, Map<String, Object> inputVar, Context context) {
        inputVar.put("action", Utils.SUBMIT);
        return createRoles(ref, inputVar, context);
    }

    public String createWFStep1(EntityReference ref, Map<String, Object> inputVar, Context context) {
        inputVar.put("action", Utils.WF_STEP_1);
        return createRoles(ref, inputVar, context);
    }

    public String createWFStep2(EntityReference ref, Map<String, Object> inputVar, Context context) {
        inputVar.put("action", Utils.WF_STEP_2);
        return createRoles(ref, inputVar, context);
    }

    public String createWFStep3(EntityReference ref, Map<String, Object> inputVar, Context context) {
        inputVar.put("action", Utils.WF_STEP_3);
        return createRoles(ref, inputVar, context);
    }

    public String createRoles(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            Integer id = Integer.parseInt(ref.getId());
            Collection collection = Collection.find(context, id);

            if (collection != null) {
                int act = Utils.getActionRole((String) inputVar.get("action"));
                switch (act) {
                    case 1: {
                        Group group = collection.createAdministrators();
                        collection.update();
                        return String.valueOf(group.getID());
                    }
                    case 2: {
                        Group group = collection.createSubmitters();
                        collection.update();
                        return String.valueOf(group.getID());
                    }
                    case 4:
                    case 5:
                    case 6: {
                        Group group = collection.createWorkflowGroup(act - 3);
                        collection.update();
                        return String.valueOf(group.getID());
                    }
                    default:
                        return null;
                }
            } else {
                throw new EntityException("Not found", "Entity not found", 404);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void removeRoles(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer id = Integer.parseInt(ref.getId());
            Collection collection = Collection.find(context, id);

            if (collection != null) {
                int act = Utils.getActionRole((String) inputVar.get("eid"));
                switch (act) {
                    case 1: {
                        Group group = collection.getAdministrators();
                        if (group != null) {
                            collection.removeAdministrators();
                            collection.update();
                            group.delete();
                        }
                        break;
                    }
                    case 2: {
                        Group group = collection.getSubmitters();
                        if (group != null) {
                            collection.removeSubmitters();
                            collection.update();
                            group.delete();
                        }
                        break;
                    }
                    case 4:
                    case 5:
                    case 6: {
                        Group group = collection.getWorkflowGroup(act - 3);
                        if (group != null) {
                            AuthorizeUtil.authorizeManageWorkflowsGroup(context, collection);
                            collection.setWorkflowGroup(act - 3,null);
                            collection.update();
                            group.delete();
                        }
                        break;
                    }
                }
            } else {
                throw new EntityException("Not found", "Entity not found", 404);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }

        try {
            Integer elid = Integer.parseInt((String) inputVar.get("id"));
            Collection col = Collection.find(context, elid);
            if ((col != null)) {
                col.removeAdministrators();
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public Object getCount(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            return ContentHelper.countItemsCollection(context);
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public Object getItemsCount(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            return ContentHelper.countItemsItem(context, Integer.parseInt(ref.getId()));
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public Object getItems(EntityReference ref, UserRequestParams uparams, Context context) {

        try {
            List<Object> entities = new ArrayList<Object>();

            Item[] items = ContentHelper.findAllItem(context, Integer.parseInt(ref.getId()), uparams.getStart(), uparams.getLimit());
            for (Item item : items) {
                entities.add(new ItemEntityTrim(item, context));
            }
            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public Object getAdmin(EntityReference ref, UserRequestParams uparams, Context context) {
        uparams.setAction(Utils.ADMIN);
        return getRoles(ref, uparams, context);
    }

    public Object getSubmit(EntityReference ref, UserRequestParams uparams, Context context) {
        uparams.setAction(Utils.SUBMIT);
        return getRoles(ref, uparams, context);
    }

    public Object getWFStep1(EntityReference ref, UserRequestParams uparams, Context context) {
        uparams.setAction(Utils.WF_STEP_1);
        return getRoles(ref, uparams, context);
    }

    public Object getWFStep2(EntityReference ref, UserRequestParams uparams, Context context) {
        uparams.setAction(Utils.WF_STEP_2);
        return getRoles(ref, uparams, context);
    }

    public Object getWFStep3(EntityReference ref, UserRequestParams uparams, Context context) {
        uparams.setAction(Utils.WF_STEP_3);
        return getRoles(ref, uparams, context);
    }

    public Object getRoles(EntityReference ref, UserRequestParams uparams, Context context) {

        try {
            Collection res = Collection.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, res, Constants.READ);
            Group role;
            int act = Utils.getActionRole(uparams.getAction());
            switch (act) {
                case 1: {
                    role = res.getAdministrators();
                    break;
                }
                case 2: {
                    role = res.getSubmitters();
                    break;
                }
                case 4:
                case 5:
                case 6: {
                    role = res.getWorkflowGroup(act - 3);
                    break;
                }
                default: {
                    List<GroupEntityTrim> groups = new ArrayList<GroupEntityTrim>();
                    role = res.getAdministrators();
                    if (role != null) {
                        groups.add(new GroupEntityTrim(role));
                    }
                    role = res.getSubmitters();
                    if (role != null) {
                        groups.add(new GroupEntityTrim(role));
                    }
                    role = res.getWorkflowGroup(1);
                    if (role != null) {
                        groups.add(new GroupEntityTrim(role));
                    }
                    role = res.getWorkflowGroup(2);
                    if (role != null) {
                        groups.add(new GroupEntityTrim(role));
                    }
                    role = res.getWorkflowGroup(3);
                    if (role != null) {
                        groups.add(new GroupEntityTrim(role));
                    }
                    return groups;
                }
            }

            if (role != null) {
                return new GroupEntity(role);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
        return new GroupEntityTrim();
    }

    public String createLogo(EntityReference ref, Object inputVar, Context context) {

        try {
            Collection col = Collection.find(context, Integer.parseInt(ref.getId()));
            if (col != null) {
                Bitstream bitstream = col.setLogo((InputStream) inputVar);
                col.update();
                return String.valueOf(bitstream.getID());
            } else {
                throw new EntityException("Not found", "Entity not found", 404);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (IOException ie) {
            throw new EntityException("Internal server error", "SQL error, cannot create logo", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void removeLogo(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Collection col = Collection.find(context, Integer.parseInt(ref.getId()));
            if ((col != null)) {
                col.setLogo(null);
                col.update();
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (IOException ie) {
            throw new EntityException("Internal server error", "SQL error, cannot remove logo", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public Object getLogo(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            Collection res = Collection.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, res, Constants.READ);
            Bitstream logo = res.getLogo();

            if (logo != null) {
                return new BitstreamEntityId(logo);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
        return new BitstreamEntityId();
    }

    public String createPolicies(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            Collection col = Collection.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, col, Constants.WRITE);
            int actionId = Constants.getActionID((String) inputVar.get("action"));
            String userId = (String) inputVar.get("userId");
            String groupId = (String) inputVar.get("groupId");

            if (col != null && actionId != -1) {
                ResourcePolicy policy = ResourcePolicy.create(context);
                policy.setResource(col);
                policy.setAction(actionId);
                EPerson user;
                if (!"".equals(userId)) {
                    try {
                        user = EPerson.find(context, Integer.parseInt(userId));
                        policy.setEPerson(user);
                    } catch (NumberFormatException ex) {
                    }
                }
                Group group;
                if (!"".equals(groupId)) {
                    try {
                        group = Group.find(context, Integer.parseInt(groupId));
                        policy.setGroup(group);
                    } catch (NumberFormatException ex) {
                    }
                }
                policy.update();
                return String.valueOf(policy.getID());
            } else {
                throw new EntityException("Not found", "Entity not found", 404);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void removePolicies(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Collection col = Collection.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, col, Constants.WRITE);
            if (col != null) {
                int eid = Integer.parseInt((String) inputVar.get("eid"));
                ResourcePolicy policy = ResourcePolicy.find(context, eid);
                if (policy != null) {
                    policy.delete();
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

    public Object getPolicies(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            Collection res = Collection.find(context, Integer.parseInt(ref.getId()));
//            AuthorizeManager.authorizeAction(context, res, Constants.READ);
            List<Object> entities = new ArrayList<Object>();

            List<ResourcePolicy> policies = AuthorizeManager.getPolicies(context, res);
            for (ResourcePolicy policy : policies) {
                entities.add(new PolicyEntity(policy));
            }
            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
//        } catch (AuthorizeException ex) {
//            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public String getLicence() {
        return this.licence;
    }

    public String getShortDescription() {
        return this.short_description;
    }

    public String getCopyrightText() {
        return this.copyright_text;
    }

    public String getSidebarText() {
        return this.side_bar_text;
    }

    public String getIntroductoryText() {
        return this.introductory_text;
    }

    public String getProvenance() {
        return this.provenance;
    }
}
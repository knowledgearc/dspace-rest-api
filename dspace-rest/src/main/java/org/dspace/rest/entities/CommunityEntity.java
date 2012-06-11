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
import org.dspace.authorize.ResourcePolicy;
import org.dspace.content.Bitstream;
import org.dspace.content.Community;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.exception.EntityException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommunityEntity extends CommunityEntityTrim {

    @EntityFieldRequired
    private String short_description, introductory_text, copyright_text, side_bar_text;

    public CommunityEntity() {
    }

    public CommunityEntity(String uid, Context context, UserRequestParams uparams) throws SQLException {
        super(uid, context, uparams);
        this.short_description = res.getMetadata("short_description");
        this.introductory_text = res.getMetadata("introductory_text");
        this.copyright_text = res.getMetadata("copyright_text");
        this.side_bar_text = res.getMetadata("side_bar_text");
    }

    public CommunityEntity(Community community, UserRequestParams uparams) throws SQLException {
        this(community, uparams, true, true);
    }

    public CommunityEntity(Community community, UserRequestParams uparams, boolean hasCollections, boolean hasSubCommunities) throws SQLException {

        super(community, uparams, hasCollections, hasSubCommunities);
        this.short_description = community.getMetadata("short_description");
        this.introductory_text = community.getMetadata("introductory_text");
        this.copyright_text = community.getMetadata("copyright_text");
        this.side_bar_text = community.getMetadata("side_bar_text");
    }

    public String createCommunity(EntityReference ref, Map<String, Object> inputVar, Context context) {
        String result;

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

        try {
            Community community;
            if (communityId > 0) {
                Community com = Community.find(context, communityId);
                community = com.createSubcommunity();
            } else {
                community = Community.create(null, context);
            }
            if (community != null) {
                result = String.valueOf(community.getID());
                community.setMetadata("name", name);
                community.setMetadata("short_description", shortDescription);
                community.setMetadata("copyright_text", copyrightText);
                community.setMetadata("side_bar_text", sidebarText);
                community.setMetadata("introductory_text", introductoryText);
                community.update();
            } else {
                throw new EntityException("Internal server error", "Could not create community", 500);
            }

        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (IOException ex) {
            throw new EntityException("Internal server error", "SQL error, cannot create community", 500);
        }
        return result;
    }

    public void editCommunity(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            Integer id = Integer.parseInt(ref.getId());
            Community community = Community.find(context, id);

            String name = (String) inputVar.get("name");
            String shortDescription = (String) inputVar.get("shortDescription");
            String copyrightText = (String) inputVar.get("copyrightText");
            String sidebarText = (String) inputVar.get("sidebarText");
            String introductoryText = (String) inputVar.get("introductoryText");

            if (community != null) {
                community.setMetadata("name", name);
                community.setMetadata("short_description", shortDescription);
                community.setMetadata("copyright_text", copyrightText);
                community.setMetadata("side_bar_text", sidebarText);
                community.setMetadata("introductory_text", introductoryText);
                community.update();
            } else {
                throw new EntityException("Internal server error", "Could not update community", 500);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (IOException e) {
            throw new EntityException("Internal server error", "SQL error, cannot update community", 500);
        }
    }

    public void removeCommunity(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Community com = Community.find(context, Integer.parseInt(ref.getId()));
            if ((com != null)) {
                com.delete();
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (IOException ie) {
            throw new EntityException("Internal server error", "SQL error, cannot remove community", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public String createAdministrators(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            Community com = Community.find(context, Integer.parseInt(ref.getId()));
            if (com != null) {
                com.createAdministrators();
                com.update();
                Group group = com.getAdministrators();
                return String.valueOf(group.getID());
            } else {
                throw new EntityException("Not found", "Entity not found", 404);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (IOException ie) {
            throw new EntityException("Internal server error", "SQL error, cannot create adminitrators", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void removeAdministrators(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Community com = Community.find(context, Integer.parseInt(ref.getId()));
            if ((com != null)) {
                com.removeAdministrators();
                com.update();
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (IOException ie) {
            throw new EntityException("Internal server error", "SQL error, cannot remove adminitrators", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public Object getAdministrators(EntityReference ref, UserRequestParams uparams, Context context) {

        try {
            Community res = Community.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, res, Constants.READ);
            Group administrators = res.getAdministrators();

            if (administrators != null) {
                return new GroupEntity(administrators);
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
            Community com = Community.find(context, Integer.parseInt(ref.getId()));
            if (com != null) {
                Bitstream bitstream = com.setLogo((InputStream) inputVar);
                com.update();
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
            Community com = Community.find(context, Integer.parseInt(ref.getId()));
            if ((com != null)) {
                com.setLogo(null);
                com.update();
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
            Community res = Community.find(context, Integer.parseInt(ref.getId()));
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
            Community com = Community.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, com, Constants.WRITE);
            int actionId = Constants.getActionID((String) inputVar.get("action"));
            String userId = (String) inputVar.get("userId");
            String groupId = (String) inputVar.get("groupId");

            if (com != null && actionId != -1) {
                ResourcePolicy policy = ResourcePolicy.create(context);
                policy.setResource(com);
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
            Community com = Community.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, com, Constants.WRITE);
            if ((com != null)) {
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
            Community res = Community.find(context, Integer.parseInt(ref.getId()));
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
}
/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.dspace.rest.entities.CommunityEntity;
import org.dspace.rest.entities.CommunityEntityTrim;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.capabilities.Createable;
import org.sakaiproject.entitybus.entityprovider.capabilities.Deleteable;
import org.sakaiproject.entitybus.entityprovider.capabilities.Updateable;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommunitiesProvider extends AbstractBaseProvider implements CoreEntityProvider, Deleteable, Updateable, Createable {

    private static Logger log = Logger.getLogger(CommunitiesProvider.class);

    public CommunitiesProvider(EntityProviderManager entityProviderManager) throws SQLException, NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = CommunityEntity.class;
//        func2actionMapGET.put("getId", "id");
//        func2actionMapGET.put("getName", "name");
//        func2actionMapGET.put("getCountItems", "countItems");
//        func2actionMapGET.put("getHandle", "handle");
//        func2actionMapGET.put("getType", "type");
//        func2actionMapGET.put("getCollections", "collections");
//        func2actionMapGET.put("getCanEdit", "canedit");
//        func2actionMapGET.put("getParentCommunity", "anchestor");
//        func2actionMapGET.put("getSubCommunities", "children");
//        func2actionMapGET.put("getAdministrators", "administrators");
//        func2actionMapGET.put("getRecentSubmissions", "recent");
//        func2actionMapGET.put("getShortDescription", "shortDescription");
//        func2actionMapGET.put("getCopyrightText", "copyrightText");
//        func2actionMapGET.put("getSidebarText", "sidebarText");
//        func2actionMapGET.put("getIntroductoryText", "introductoryText");
//        func2actionMapGET.put("getLogo", "logo");
//        func2actionMapGET.put("getPolicies", "policies");
//        func2actionMapPUT.put("setName", "name");
//        func2actionMapPUT.put("setShortDescription", "shortDescription");
//        func2actionMapPUT.put("setCopyrightText", "copyrightText");
//        func2actionMapPUT.put("setSidebarText", "sidebarText");
//        func2actionMapPUT.put("setIntroductoryText", "introductoryText");
//        func2actionMapPUT.put("addCollection", "collections");
//        func2actionMapPUT.put("addSubcommunity", "children");
//        func2actionMapPOST.put("createAdministrators", "createAdministrators");
//        inputParamsPOST.put("createAdministrators", new String[]{"id"});
//        func2actionMapPOST.put("createCollection", "createCollection");
//        inputParamsPOST.put("createCollection", new String[]{"name", "id"});
//        func2actionMapPOST.put("createSubcommunity", "createSubcommunity");
//        inputParamsPOST.put("createSubcommunity", new String[]{"name", "id"});
//        func2actionMapDELETE.put("removeChildren", "children");
//        func2actionMapDELETE.put("removeSubcollections", "collections");
//        func2actionMapDELETE.put("removeAdministrators", "administrators");
//        func2actionMapDELETE.put("delete", "");
        entityConstructor = processedEntity.getDeclaredConstructor(String.class, Context.class, Integer.TYPE, UserRequestParams.class);
        initMappings(processedEntity);
        //createActions(processedEntity);
        //createPUTActions(processedEntity);
    }

    public String getEntityPrefix() {
        return "communities";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "entity_exists:" + id);

        // sample entity
        if (id.equals(":ID:")) {
            return true;
        }

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        // extract query parameters
        refreshParams(context);
        boolean result = false;
        try {
            Community comm = Community.find(context, Integer.parseInt(id));
            if (comm != null) {
                result = true;
            }
        } catch (SQLException ex) {
            result = false;
        }

        removeConn(context);
        return result;
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_community:" + reference.getId());
        String segments[] = {};

        if (reqStor.getStoredValue("pathInfo") != null) {
            segments = reqStor.getStoredValue("pathInfo").toString().split("/", 10);
        }

        if (segments.length > 3) {
            return super.getEntity(reference);
        } else {

            if (reference.getId().equals(":ID:")) {
                return new CommunityEntity();
            }

            if (reference.getId() == null) {
                return new CommunityEntity();
            }

            Context context;
            try {
                context = new Context();
            } catch (SQLException ex) {
                throw new EntityException("Internal server error", "SQL error", 500);
            }

            UserRequestParams uparams = refreshParams(context);
            if (entityExists(reference.getId())) {
                // return just entity containg id or full info
//                if (idOnly) {
//                    return new CommunityEntityId(reference.getId(), context);
//                } else {
                try {
                    return new CommunityEntity(reference.getId(), context, uparams);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
//                }
            }

            removeConn(context);
            throw new IllegalArgumentException("Invalid id:" + reference.getId());
        }
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_communities");

        try {
            Context context = new Context();

            UserRequestParams uparams = refreshParams(context);
            List<Object> entities = new ArrayList<Object>();

            Community[] communities = Community.findAllTop(context);
            for (Community c : communities) {
                entities.add(trim ? new CommunityEntityTrim(c, uparams, true, true) : new CommunityEntity(c, uparams, true, true));
            }

            context.complete();
            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public Object getSampleEntity() {
        return new CommunityEntity();
    }
}
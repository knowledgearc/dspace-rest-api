/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.core.Context;
import org.dspace.rest.content.ContentHelper;
import org.dspace.rest.entities.CollectionEntity;
import org.dspace.rest.entities.CollectionEntityTrim;
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

public class CollectionsProvider extends AbstractBaseProvider implements CoreEntityProvider, Createable, Updateable, Deleteable {

    private static Logger log = Logger.getLogger(UserProvider.class);

    public CollectionsProvider(EntityProviderManager entityProviderManager) throws SQLException, NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = CollectionEntity.class;
//        func2actionMapGET.put("getId", "id");
//        func2actionMapGET.put("getName", "name");
//        func2actionMapGET.put("getLicence", "licence");
//        func2actionMapGET.put("getItems", "items");
//        func2actionMapGET.put("getHandle", "handle");
//        func2actionMapGET.put("getCanEdit", "canedit");
//        func2actionMapGET.put("getCommunities", "communities");
//        func2actionMapGET.put("getCountItems", "countItems");
//        func2actionMapGET.put("getType", "type");
//        func2actionMapGET.put("getShortDescription", "shortDescription");
//        func2actionMapGET.put("getIntroText", "introText");
//        func2actionMapGET.put("getCopyrightText", "copyrightText");
//        func2actionMapGET.put("getSidebarText", "sidebarText");
//        func2actionMapGET.put("getProvenance", "provenance");
//        func2actionMapGET.put("getLogo", "logo");
//        func2actionMapGET.put("getPolicies", "policies");
//        func2actionMapPUT.put("setShortDescription", "shortDescription");
//        func2actionMapPUT.put("setIntroText", "introText");
//        func2actionMapPUT.put("setCopyrightText", "copyrightText");
//        func2actionMapPUT.put("setSidebarText", "sidebarText");
//        func2actionMapPUT.put("setProvenance", "provenance");
//        func2actionMapPUT.put("setLicence", "licence");
//        func2actionMapPUT.put("setName", "name");
//        func2actionMapPOST.put("createAdministrators", "createAdministrators");
//        inputParamsPOST.put("createAdministrators", new String[]{"id"});
//        func2actionMapPOST.put("createSubmitters", "createSubmitters");
//        inputParamsPOST.put("createSubmitters", new String[]{"id"});
//        func2actionMapPOST.put("createTemplateItem", "createTemplateItem");
//        inputParamsPOST.put("createTemplateItem", new String[]{"id"});
//        func2actionMapPOST.put("createWorkflowGroup", "createWorkflowGroup");
//        inputParamsPOST.put("createWorkflowGroup", new String[]{"id", "step"});
//        func2actionMapDELETE.put("removeTemplateItem", "templateitem");
//        func2actionMapDELETE.put("removeSubmitters", "submitters");
//        func2actionMapDELETE.put("removeAdministrators", "administrators");
        func2actionMapGET.put("getItems", "items");
        func2actionMapGET.put("getRoles", "roles");
        func2actionMapGET.put("getLogo", "logo");
        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "collections";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "collection_exists:" + id);

        Context context = null;
        try {
            context = new Context();

            refreshParams(context);

            Collection comm = Collection.find(context, Integer.parseInt(id));
            return comm != null ? true : false;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } finally {
            removeConn(context);
        }
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_collection:" + reference.getId());
        String segments[] = {};

        if (reqStor.getStoredValue("pathInfo") != null) {
            segments = reqStor.getStoredValue("pathInfo").toString().split("/");
        }

        if (segments.length > 3) {
            return super.getEntity(reference);
        }

        Context context = null;
        try {
            context = new Context();

            refreshParams(context);
            if (entityExists(reference.getId())) {
                return new CollectionEntity(reference.getId(), context);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_collections");

        Context context = null;
        try {
            context = new Context();

            refreshParams(context);
            List<Object> entities = new ArrayList<Object>();

            entities.add(ContentHelper.countItemsCollection(context));
            Collection[] collections = ContentHelper.findAllCollection(context, _start, _limit);
            for (Collection c : collections) {
                entities.add(trim ? new CollectionEntityTrim(c) : new CollectionEntity(c));
            }

            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getSampleEntity() {
        return new CollectionEntity();
    }
}

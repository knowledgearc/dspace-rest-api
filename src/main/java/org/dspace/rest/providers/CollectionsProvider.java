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

    public CollectionsProvider(EntityProviderManager entityProviderManager) throws NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = CollectionEntity.class;
        func2actionMapGET.put("getCount", "count");
        func2actionMapGET.put("getItemsCount", "itemscount");
        func2actionMapGET.put("getItems", "items");
        func2actionMapGET.put("getRoles", "roles");
        func2actionMapGET.put("getAdmin", "admin");
        func2actionMapGET.put("getSubmit", "submit");
        func2actionMapGET.put("getWFStep1", "workflow_step_1");
        func2actionMapGET.put("getWFStep2", "workflow_step_2");
        func2actionMapGET.put("getWFStep3", "workflow_step_3");
        func2actionMapGET.put("getLogo", "logo");
        func2actionMapGET.put("getPolicies", "policies");
        func2actionMapPOST.put("createCollection", "");
        inputParamsPOST.put("createCollection", new String[]{"name", "communityId"});
        func2actionMapPOST.put("createAdmin", "admin");
        func2actionMapPOST.put("createSubmit", "submit");
        func2actionMapPOST.put("createWFStep1", "workflow_step_1");
        func2actionMapPOST.put("createWFStep2", "workflow_step_2");
        func2actionMapPOST.put("createWFStep3", "workflow_step_3");
        func2actionMapPOST.put("createLogo", "logo");
        func2actionMapPOST.put("createPolicies", "policies");
        func2actionMapPUT.put("editCollection", "");
        func2actionMapDELETE.put("removeCollection", "");
        func2actionMapDELETE.put("removeRoles", "roles");
        func2actionMapDELETE.put("removeLogo", "logo");
        func2actionMapDELETE.put("removePolicies", "policies");
        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "collections";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "collection_exists:" + id);

        try {
            Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            return true;
        }

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            Collection comm = Collection.find(context, Integer.parseInt(id));
            return comm != null ? true : false;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getEntity(EntityReference ref) {
        log.info(userInfo() + "get_collection:" + ref.getId());
        String segments[] = getSegments();

        if (segments.length > 3) {
            if (segments[3].startsWith("roles")) {
                return super.getEntity(ref, segments[segments.length - 1]);
            }
            return super.getEntity(ref);
        } else {
            try {
                Integer.parseInt(ref.getId());
            } catch (NumberFormatException ex) {
                return super.getEntity(ref, ref.getId());
            }
        }

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            if (entityExists(ref.getId())) {
                return new CollectionEntity(ref.getId(), context);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + ref.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_collections");

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            List<Object> entities = new ArrayList<Object>();
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
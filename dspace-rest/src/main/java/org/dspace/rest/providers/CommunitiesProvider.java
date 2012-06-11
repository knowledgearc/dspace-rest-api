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

public class CommunitiesProvider extends AbstractBaseProvider implements CoreEntityProvider, Createable, Updateable, Deleteable {

    private static Logger log = Logger.getLogger(CommunitiesProvider.class);

    public CommunitiesProvider(EntityProviderManager entityProviderManager) throws NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = CommunityEntity.class;
        func2actionMapGET.put("getAdministrators", "administrators");
        func2actionMapGET.put("getLogo", "logo");
        func2actionMapGET.put("getPolicies", "policies");
        func2actionMapPOST.put("createCommunity", "");
        inputParamsPOST.put("createCommunity", new String[]{"name"});
        func2actionMapPOST.put("createAdministrators", "administrators");
        inputParamsPOST.put("createAdministrators", new String[]{});
        func2actionMapPOST.put("createLogo", "logo");
        func2actionMapPOST.put("createPolicies", "policies");
        inputParamsPOST.put("createPolicies", new String[]{"action"});
        func2actionMapPUT.put("editCommunity", "");
        func2actionMapDELETE.put("removeCommunity", "");
        func2actionMapDELETE.put("removeAdministrators", "administrators");
        func2actionMapDELETE.put("removeLogo", "logo");
        func2actionMapDELETE.put("removePolicies", "policies");
        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "communities";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "community_exists:" + id);

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            Community comm = Community.find(context, Integer.parseInt(id));
            return comm != null ? true : false;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } finally {
            removeConn(context);
        }
    }

    public Object getEntity(EntityReference ref) {
        log.info(userInfo() + "get_community:" + ref.getId());
        String segments[] = getSegments();

        if (segments.length > 3) {
            return super.getEntity(ref);
        }

        Context context = null;
        try {
            context = new Context();
            UserRequestParams uparams = refreshParams(context);

            if (entityExists(ref.getId())) {
                return new CommunityEntity(ref.getId(), context, uparams);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + ref.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_communities");

        Context context = null;
        try {
            context = new Context();
            UserRequestParams uparams = refreshParams(context);

            List<Object> entities = new ArrayList<Object>();
            Community[] communities = Community.findAllTop(context);
            for (Community c : communities) {
                entities.add(trim ? new CommunityEntityTrim(c, uparams) : new CommunityEntity(c, uparams));
            }

            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getSampleEntity() {
        return new CommunityEntity();
    }
}
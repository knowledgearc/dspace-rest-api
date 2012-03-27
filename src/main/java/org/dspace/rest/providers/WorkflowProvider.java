/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.core.Context;
import org.dspace.rest.content.ContentHelper;
import org.dspace.rest.entities.PoolEntity;
import org.dspace.rest.entities.WorkflowEntity;
import org.dspace.workflow.WorkflowItem;
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

public class WorkflowProvider extends AbstractBaseProvider implements CoreEntityProvider, Createable, Updateable, Deleteable {

    private static Logger log = Logger.getLogger(WorkflowProvider.class);

    public WorkflowProvider(EntityProviderManager entityProviderManager) throws SQLException, NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = WorkflowEntity.class;
        func2actionMapGET.put("getSubmitters", "submitters");
        func2actionMapPUT.put("accept", "accept");
        func2actionMapPUT.put("approve", "approve");
        func2actionMapPOST.put("reject", "reject");
        func2actionMapPOST.put("returnToPool", "returnToPool");
        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "workflows";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "workflow_exists:" + id);

        if ("submitters".equals(id)) {
            return true;
        }
        Context context = null;
        try {
            context = new Context();

            refreshParams(context);

            WorkflowItem comm = WorkflowItem.find(context, Integer.parseInt(id));
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
        log.info(userInfo() + "get_workflow:" + reference.getId());
        String segments[] = {};

        if (reqStor.getStoredValue("pathInfo") != null) {
            segments = reqStor.getStoredValue("pathInfo").toString().split("/");
        }

        if (segments.length > 3) {
            return super.getEntity(reference);
        } else if ("submitters".equals(reference.getId())) {
            return super.getEntity(reference, "submitters");
        }

        Context context = null;
        try {
            context = new Context();

            refreshParams(context);
            if (entityExists(reference.getId())) {
                return new WorkflowEntity(reference.getId(), context);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_workflows");

        Context context = null;
        try {
            context = new Context();

            refreshParams(context);

            List<Object> results = new ArrayList<Object>();

            List<Object> entities = new ArrayList<Object>();
            WorkflowItem[] workflowItems = ContentHelper.findAllWorkflow(context, reviewer, submitter, fields, status, _start, _limit);
            for (WorkflowItem wfi : workflowItems) {
                entities.add(new WorkflowEntity(wfi));
            }

            results.add(new PoolEntity(ContentHelper.countItemsWorkflow(context, reviewer, submitter, fields, status), entities));

//            results.add("count:"+ContentHelper.countItemsWorkflow(context, reviewer, submitter, fields, status));
//            results.add(entities);

            return results;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getSampleEntity() {
        return new WorkflowEntity();
    }
}
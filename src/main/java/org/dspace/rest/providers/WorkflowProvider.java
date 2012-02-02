/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */


package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.rest.entities.WorkflowEntity;
import org.dspace.rest.entities.WorkflowItemEntity;
import org.dspace.rest.util.UserRequestParams;
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

/**
 * Provides interface for workflow entities
 * @author Lewis
 */
public class WorkflowProvider extends AbstractBaseProvider implements CoreEntityProvider, Createable, Updateable, Deleteable {

    private static Logger log = Logger.getLogger(WorkflowProvider.class);

    public WorkflowProvider(EntityProviderManager entityProviderManager) throws SQLException, NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = WorkflowEntity.class;
        func2actionMapPOST.put("submitAccept", "submitAccept");
        inputParamsPOST.put("submitAccept", new String[]{"id"});
        func2actionMapPOST.put("submitReturnPool", "submitReturnPool");
        inputParamsPOST.put("submitReturnPool", new String[]{"id"});
        func2actionMapPOST.put("submitApprove", "submitApprove");
        inputParamsPOST.put("submitApprove", new String[]{"id"});
        func2actionMapPOST.put("submitReject", "submitReject");
        inputParamsPOST.put("submitReject", new String[]{"id","reason"});
        entityConstructor = processedEntity.getDeclaredConstructor(new Class<?>[]{String.class, Context.class, Integer.TYPE, UserRequestParams.class});
        initMappings(processedEntity);

    }

    public String getEntityPrefix() {
        return "workflow";
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

        refreshParams(context);

        boolean result = false;
        try {
            WorkflowItem col = WorkflowItem.find(context, Integer.parseInt(id));
            if (col != null) {
                result = true;
            }
        } catch (SQLException ex) {
            result = false;
        }

        // close connection to prevent connection problems
        removeConn(context);
        return result;
    }

    /**
     * Returns information about particular entity
     * @param reference
     * @return
     */
    @Override
    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_entity:" + reference.getId());
        String segments[] = {};

        if (reqStor.getStoredValue("pathInfo") != null) {
            segments = reqStor.getStoredValue("pathInfo").toString().split("/", 10);
        }

        // first check if there is sub-field requested
        // if so then invoke appropriate method inside of entity
        if (segments.length > 3) {
            return super.getEntity(reference);
        } else {

            Context context;
            try {
                context = new Context();


            UserRequestParams uparams = refreshParams(context);

//            // sample entity
            if (reference.getId().equals(":ID:")) {
                return new WorkflowItemEntity();
            }
//
            if (reference.getId() == null) {
                return new WorkflowItemEntity();
            }

            if (entityExists(reference.getId())) {
                // return basic entity or full info
                return new WorkflowItemEntity(reference.getId(), context, 1, uparams);
            }
            } catch (SQLException ex) {
                throw new EntityException("Internal server error", "SQL error", 500);
            }

            removeConn(context);
            throw new IllegalArgumentException("Invalid id:" + reference.getId());
        }
    }

    /**
     * List all workflow items in the system, sort and format if requested
     * @param ref
     * @param search
     * @return
     */
    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_entities");

        Context context;
        try {
            context = new Context();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

        UserRequestParams uparams = refreshParams(context);

        List<Object> results = new ArrayList<Object>();
        List<Object> entities = new ArrayList<Object>();
        WorkflowItem[] workflowItems;
        WorkflowEntity workflowEntity = new WorkflowEntity();

        try {
            workflowEntity.setCountItems(WorkflowItem.countItemsforREST(context));

            if (uparams.getPerPage() > 0) {
                String db = ConfigurationManager.getProperty("db.name");
                if ("postgres".equals(db)) {
                    workflowItems = WorkflowItem.findAllbyPostgres(context, uparams.getPerPage(), uparams.getPage());
                } else if ("oracle".equals(db)) {
                    workflowItems = WorkflowItem.findAllbyOracle(context, uparams.getPerPage(), uparams.getPage());
                } else {
                    workflowItems = WorkflowItem.findAll(context);
                }
            } else {
                workflowItems = WorkflowItem.findAll(context);
            }

            System.out.println(" number of workflowitems " + workflowItems.length);
            for (WorkflowItem wfi : workflowItems) {
                entities.add(new WorkflowItemEntity(wfi,1,uparams));
            }
            workflowEntity.setWorkflowItems(entities);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        results.add(workflowEntity);
        removeConn(context);
//        if (!idOnly && sortOptions.size() > 0) {
//            Collections.sort(entities, new GenComparator(sortOptions));
//        }

//        removeTrailing(entities);

        return results;
    }

    /*
     * Here is sample collection entity defined
     */
    public Object getSampleEntity() {
        return new WorkflowEntity();
    }
}

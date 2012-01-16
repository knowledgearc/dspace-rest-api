/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.extension.EntityData;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Enables users to discover through items according to different criteria
 * @see org.dspace.rest.entities.SearchResultsInfoEntity
 * @author Lewis
 */
public class DiscoverProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(DiscoverProvider.class);

    /**
     * Handles provider for discover accross items
     * @param entityProviderManager
     * @throws java.sql.SQLException
     */
    public DiscoverProvider(EntityProviderManager entityProviderManager) throws SQLException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "discover";
    }

    public boolean entityExists(String id) {
        return true;
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_entity:" + reference.getId());
        throw new EntityException("Not Acceptable", "The data is not available", 406);
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info("DiscoverProvider - get_entities");

//        Context context;
//        try {
//            context = new Context();
//        } catch (SQLException ex) {
//            throw new EntityException("Internal server error", "SQL error", 500);
//        }

        // refresh parameters for this request
//        UserRequestParams uparams;
//        uparams = refreshParams(context);
        List<Object> entities = new ArrayList<Object>();

        try {
            HttpClient client = new HttpClient();
            HttpMethod method=new GetMethod(ConfigurationManager.getProperty("discovery", "search.server")+"/select");
            log.info("DiscoverProvider method - " + ConfigurationManager.getProperty("discovery", "search.server"));
            NameValuePair[] nameValuePairs;

            log.info("DiscoverProvider search.getRestrictions().length - " + search.getRestrictions().length);
            log.info("DiscoverProvider format - " + format);
            if (search.getRestrictions().length > 0) {
                if ("json".equals(format)) {
                    nameValuePairs = new NameValuePair[search.getRestrictions().length];
                }else{
                    nameValuePairs = new NameValuePair[search.getRestrictions().length-1];
                }
                int n=0;
                for (int i = 0; i < search.getRestrictions().length; i++) {
                    log.info("DiscoverProvider search.getRestrictions()[i].getProperty() - " + search.getRestrictions()[i].getProperty());
                    log.info("DiscoverProvider search.getRestrictions()[i].getStringValue() - " + search.getRestrictions()[i].getStringValue());
                    if(!"org.apache.catalina.ASYNC_SUPPORTED".equals(search.getRestrictions()[i].getProperty())){
                        nameValuePairs[n] = new NameValuePair(search.getRestrictions()[i].getProperty(),search.getRestrictions()[i].getStringValue());
                        n++;
                    }
                }
                if ("json".equals(format)) {
                    nameValuePairs[search.getRestrictions().length-1] = new NameValuePair("wt", "json");
                }
                method.setQueryString(nameValuePairs);
            }

            client.executeMethod(method);
            String s = method.getResponseBodyAsString();
            log.info("DiscoverProvider result string - " + s);

            EntityData ed = new EntityData(s);
            entities.add(ed);

            method.releaseConnection();

        } catch (IOException e) {
            throw new EntityException("Internal server error", "IO error, cannot call solr server", 500);
        }


        return entities;
    }

    /**
     * Returns a Entity object with sample data
     */
    public Object getSampleEntity() {
        return null;
    }
}

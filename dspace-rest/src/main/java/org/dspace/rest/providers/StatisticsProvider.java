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
import java.util.ArrayList;
import java.util.List;

public class StatisticsProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(StatisticsProvider.class);

    public StatisticsProvider(EntityProviderManager entityProviderManager) {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
    }

    public String getEntityPrefix() {
        return "statistics";
    }

    public boolean entityExists(String id) {
        return true;
    }

    public Object getEntity(EntityReference ref) {
        log.info(userInfo() + "get_entity:" + ref.getId());
        throw new EntityException("Not Acceptable", "The data is not available", 406);
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info("StatisticsProvider - get_entities");

        List<Object> entities = new ArrayList<Object>();

        try {
            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(ConfigurationManager.getProperty("solr-statistics", "server") + "/select");
            log.info("StatisticsProvider method - " + ConfigurationManager.getProperty("solr-statistics", "server"));

            log.info("StatisticsProvider search.getRestrictions().length - " + search.getRestrictions().length);
            log.info("StatisticsProvider format - " + format);
            List<NameValuePair> nameValuePairsList = new ArrayList<NameValuePair>();
            if (search.getRestrictions().length > 0) {
                for (int i = 0; i < search.getRestrictions().length; i++) {
                    log.info("StatisticsProvider search.getRestrictions()[i].getProperty() - " + search.getRestrictions()[i].getProperty());
                    log.info("StatisticsProvider search.getRestrictions()[i].getStringValue() - " + search.getRestrictions()[i].getStringValue());
                    if (!"org.apache.catalina.ASYNC_SUPPORTED".equals(search.getRestrictions()[i].getProperty())) {
                        nameValuePairsList.add(new NameValuePair(search.getRestrictions()[i].getProperty(), search.getRestrictions()[i].getStringValue()));
                    }
                }
                if ("json".equals(format)) {
                    nameValuePairsList.add(new NameValuePair("wt", "json"));
                }
            }

            if (search.getOrders().length > 0) {
                for (int i = 0; i < search.getOrders().length; i++) {
                    log.info("StatisticsProvider search.getOrders()[i].getProperty() - " + search.getOrders()[i].getProperty());
                    nameValuePairsList.add(new NameValuePair("sort", search.getOrders()[i].getProperty()));
                }
            }

            NameValuePair[] nameValuePairs = new NameValuePair[nameValuePairsList.size()];
            nameValuePairsList.toArray(nameValuePairs);
            method.setQueryString(nameValuePairs);

            client.executeMethod(method);
            String s = method.getResponseBodyAsString();
//            log.info("StatisticsProvider result string - " + s);

            entities.add(new EntityData(s));

            method.releaseConnection();

        } catch (IOException e) {
            throw new EntityException("Internal server error", "IO error, cannot call solr server", 500);
        }

        return entities;
    }

    public Object getSampleEntity() {
        return null;
    }
}
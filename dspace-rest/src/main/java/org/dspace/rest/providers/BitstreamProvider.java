/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.core.Context;
import org.dspace.rest.entities.BitstreamEntity;
import org.dspace.rest.util.RecentSubmissionsException;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.EntityView;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybus.entityprovider.capabilities.Createable;
import org.sakaiproject.entitybus.entityprovider.capabilities.Deleteable;
import org.sakaiproject.entitybus.entityprovider.capabilities.Updateable;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class BitstreamProvider extends AbstractBaseProvider implements CoreEntityProvider, Createable, Updateable, Deleteable {

    private static Logger log = Logger.getLogger(BitstreamProvider.class);

    public BitstreamProvider(EntityProviderManager entityProviderManager) throws NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = BitstreamEntity.class;
        func2actionMapDELETE.put("removeBitstream", "");
        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "bitstreams";
    }

    @EntityCustomAction(action = "download", viewKey = EntityView.VIEW_SHOW)
    public Object receive(EntityReference reference, EntityView view, Map<String, Object> params) throws SQLException, RecentSubmissionsException {
        log.info(userInfo() + "download_action:" + reference.getId());
        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            Bitstream bst = Bitstream.find(context, Integer.parseInt(reference.getId()));

            if (bst != null) {
                HttpServletResponse response = this.entityProviderManager.getRequestGetter().getResponse();
                ServletOutputStream stream = response.getOutputStream();
                response.setContentType(bst.getFormat().getMIMEType());
                response.addHeader("Content-Disposition", "attachment; filename=" + bst.getName());
                response.setContentLength((int) bst.getSize());
                BufferedInputStream buf = new BufferedInputStream(bst.retrieve());

                int readBytes;
                while ((readBytes = buf.read()) != -1) {
                    stream.write(readBytes);
                }

                if (stream != null) {
                    stream.close();
                }
                if (buf != null) {
                    buf.close();
                }
            }
        } catch (IOException ex) {
            throw new EntityException("Internal Server error", "Unable to open file", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "The resource is not available for current user", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } finally {
            removeConn(context);
        }

        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "bitstream_exists:" + id);

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            Bitstream comm = Bitstream.find(context, Integer.parseInt(id));
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
        log.info(userInfo() + "get_bitstream:" + ref.getId());
        String segments[] = getSegments();

        if (segments.length > 3) {
            return super.getEntity(ref);
        }

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            if (entityExists(ref.getId())) {
                return new BitstreamEntity(ref.getId(), context);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + ref.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_bitstreams");
        return null;
    }

    public Object getSampleEntity() {
        return new BitstreamEntity();
    }
}
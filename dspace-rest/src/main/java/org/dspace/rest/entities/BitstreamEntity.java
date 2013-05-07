/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.exception.EntityException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class BitstreamEntity extends BitstreamEntityId {

    @EntityFieldRequired
    private String name;
    private int storeNumber;
    private long sequenceId, size;
    private String checkSumAlgorithm, description, checkSum, formatDescription, source, userFormatDescription, mimeType;
//    List<Object> bundles = new ArrayList<Object>();

    public BitstreamEntity() {
    }

    public BitstreamEntity(String uid, Context context) {
        super(uid, context);
        this.name = res.getName();
        this.checkSum = res.getChecksum();
        this.checkSumAlgorithm = res.getChecksumAlgorithm();
        this.description = res.getDescription();
        this.formatDescription = res.getFormatDescription();
        this.sequenceId = res.getSequenceID();
        this.size = res.getSize();
        this.source = res.getSource();
        this.storeNumber = res.getStoreNumber();
        this.userFormatDescription = res.getUserFormatDescription();
        this.mimeType = res.getFormat().getMIMEType();
    }

    public BitstreamEntity(Bitstream bitstream) {
        super(bitstream);
        this.name = bitstream.getName();
        this.checkSum = bitstream.getChecksum();
        this.checkSumAlgorithm = bitstream.getChecksumAlgorithm();
        this.description = bitstream.getDescription();
        this.formatDescription = bitstream.getFormatDescription();
        this.sequenceId = bitstream.getSequenceID();
        this.size = bitstream.getSize();
        this.source = bitstream.getSource();
        this.storeNumber = bitstream.getStoreNumber();
        this.userFormatDescription = bitstream.getUserFormatDescription();
        this.mimeType = bitstream.getFormat().getMIMEType();
    }

    public void removeBitstream(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            int itemID=0;
            Bitstream bitstream = Bitstream.find(context, Integer.parseInt(ref.getId()));
            if ((bitstream != null)) {
                Bundle[] bundles = bitstream.getBundles();
                for (Bundle bundle : bundles) {
                    bundle.removeBitstream(bitstream);
                    itemID = bundle.getItems()[0].getID();
                }
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (IOException ie) {
            throw new EntityException("Internal server error", "SQL error, cannot remove bitstream", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getCheckSum() {
        return this.checkSum;
    }

    public String getCheckSumAlgorithm() {
        return this.checkSumAlgorithm;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFormatDescription() {
        return this.formatDescription;
    }

    public long getSequenceId() {
        return this.sequenceId;
    }

    public long getSize() {
        return this.size;
    }

    public String getSource() {
        return this.source;
    }

    public int getStoreNumber() {
        return this.storeNumber;
    }

    public String getUserFormatDescription() {
        return this.userFormatDescription;
    }

    public String getName() {
        return this.name;
    }
}
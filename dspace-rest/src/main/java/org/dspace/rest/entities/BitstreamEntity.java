/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.content.Bitstream;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;

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
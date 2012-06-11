/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.content.Bundle;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;

import java.util.ArrayList;
import java.util.List;

public class BundleEntity extends BundleEntityId {

    @EntityFieldRequired
    private String name;
    private int pid;
    List<Object> bitstreams = new ArrayList<Object>();

    public BundleEntity() {
    }

    public BundleEntity(String uid, Context context) {
        super(uid, context);
        this.pid = res.getPrimaryBitstreamID();
        this.name = res.getName();
    }

    public BundleEntity(Bundle bundle) {
        super(bundle);
        this.name = bundle.getName();
        this.pid = bundle.getPrimaryBitstreamID();
    }

    public BundleEntity(Bundle bundle, List<Object> bitstreams) {
        super(bundle);
        this.name = bundle.getName();
        this.pid = bundle.getPrimaryBitstreamID();
        this.bitstreams = bitstreams;
    }

    public int getPrimaryBitstreamId() {
        return this.pid;
    }

    public String getName() {
        return this.name;
    }

    public List<Object> getBitstreams()
    {
        return bitstreams;
    }
}
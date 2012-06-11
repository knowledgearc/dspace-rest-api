/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.util;

public class UserRequestParams {

    private String query = "";
    private String order = "";
    private String sort = "";
    private int start = 0;
    private int limit = 0;

    protected String[] fields;
    protected String status = "";
    protected String submitter = "";
    protected String reviewer = "";

    private boolean collections = false;
    private boolean trim = false;
    protected boolean parents = false;
    protected boolean children = false;
    protected boolean groups = false;
    protected boolean replies = false;

    protected String[] type;

    protected String action = "";

    public void setQuery(String param) {
        this.query = param;
    }

    public void setOrder(String param) {
        this.order = param;
    }

    public void setSort(String param) {
        this.sort = param;
    }

    public void setStart(int param) {
        this.start = param;
    }

    public void setLimit(int param) {
        this.limit = param;
    }

    public String getQuery() {
        return this.query;
    }

    public String getOrder() {
        return order;
    }

    public String getSort() {
        return this.sort;
    }

    public int getStart() {
        return this.start;
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean getTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public boolean getCollections() {
        return collections;
    }

    public void setCollections(boolean collections) {
        this.collections = collections;
    }

    public boolean getParents() {
        return parents;
    }

    public void setParents(boolean parents) {
        this.parents = parents;
    }

    public boolean getChildren() {
        return children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }

    public boolean getGroups() {
        return groups;
    }

    public void setGroups(boolean groups) {
        this.groups = groups;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean getReplies() {
        return replies;
    }

    public void setReplies(boolean replies) {
        this.replies = replies;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String[] getType()
    {
        return type;
    }

    public void setType(String[] type)
    {
        this.type = type;
    }
}

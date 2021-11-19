package org.shaq.graphql.models;

import java.util.HashMap;

/**
 * Created by galhen on 03/06/2018.
 */
public class GraphQLQueryModel {

    private String query;
    private String operationName;
    private HashMap<String,Object> variables;

    public GraphQLQueryModel(String query, String operationName) {
        this.query = query;
        this.operationName = operationName;
        this.variables = new HashMap<String, Object>();
    }

    public void addVariable(String name, Object value) {
        this.variables.put(name,value);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public HashMap<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(HashMap<String, Object> variables) {
        this.variables = variables;
    }
}

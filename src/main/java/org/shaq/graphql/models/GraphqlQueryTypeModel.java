package org.shaq.graphql.models;


public enum GraphqlQueryTypeModel {

    QUERY,
    MUTATION;


    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}

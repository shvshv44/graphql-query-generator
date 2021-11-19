package org.shaq.graphql.models;

import com.google.gson.JsonElement;

/**
 * This class warps a GraphQL response data for GSON deserializing
 */
public class GraphQLDataModel <T> {

    public T data;
    public JsonElement errors;
    public JsonElement extensions;

}

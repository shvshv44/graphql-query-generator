package org.shaq.graphql.models;

import com.google.gson.JsonElement;

/**
 * This class warp a graphql response data for GSON convert
 */
public class GraphQLDataModel <T> {

    public T data;
    public JsonElement errors;
    public JsonElement extensions;

}

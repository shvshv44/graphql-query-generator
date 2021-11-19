package org.shaq.graphql.exceptions;

public class GraphQLQueryGenerationException extends RuntimeException {

    public GraphQLQueryGenerationException() {
        super();
    }

    public GraphQLQueryGenerationException(String message) {
        super(message);
    }
}

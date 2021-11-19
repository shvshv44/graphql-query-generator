package org.shaq.graphql.annotations;

import org.shaq.graphql.models.GraphqlQueryTypeModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLQueryType {
    GraphqlQueryTypeModel value();
}

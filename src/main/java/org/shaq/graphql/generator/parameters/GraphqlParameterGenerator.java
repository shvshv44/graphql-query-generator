package org.shaq.graphql.generator.parameters;

import org.shaq.graphql.annotations.parameters.GraphQLParameter;
import org.shaq.graphql.annotations.parameters.GraphQLParameterObject;
import org.shaq.graphql.annotations.parameters.GraphQLParametersClass;
import org.shaq.graphql.exceptions.GraphQLQueryGeneratorException;
import org.shaq.graphql.generator.GraphqlQueryStringBuilder;

import java.lang.reflect.Field;
import java.util.HashMap;

public class GraphqlParameterGenerator {

    public String generateParameterForClientQuery(Class<?> classToGenerate) {
        GraphqlQueryStringBuilder builder = new GraphqlQueryStringBuilder();
        if (classToGenerate.isAnnotationPresent(GraphQLParametersClass.class)) {
             findAllParameterNameAndType(classToGenerate,builder);
        }

        return builder.createFinalQuery();
    }

    public String generateParameterForServerQuery(Class<?> classToGenerate) {
        GraphqlQueryStringBuilder builder = new GraphqlQueryStringBuilder();
        if (classToGenerate.isAnnotationPresent(GraphQLParametersClass.class)) {
            buildQueryServerParameterForObject(classToGenerate,builder);
        }

        return builder.createFinalQuery();
    }

    public <T> HashMap<String,Object> generateParameterHashWithValues(T objectToGenerate) {
        HashMap<String,Object> generatedHash = new HashMap<>();
        if (objectToGenerate.getClass().isAnnotationPresent(GraphQLParametersClass.class)) {
            putAllParametersAndValuesInHash(objectToGenerate,generatedHash);
        }

        return generatedHash;
    }

    private void findAllParameterNameAndType(Class<?> classToGenerate, GraphqlQueryStringBuilder builder) {
        Field[] fields = classToGenerate.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(GraphQLParameterObject.class)) {
                findAllParameterNameAndType(field.getType(), builder);
            } else if(field.isAnnotationPresent(GraphQLParameter.class)) {
                GraphQLParameter parameterAnnotation = field.getAnnotation(GraphQLParameter.class);
                builder.append("$").append(parameterAnnotation.name()).append(":").append(parameterAnnotation.type());
            }
            builder.append(", ");
        }
        builder.removeLastCharacters(2);
    }

    private void buildQueryServerParameterForObject(Class<?> classToGenerate, GraphqlQueryStringBuilder builder) {
        Field[] fields = classToGenerate.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(GraphQLParameterObject.class)) {
                builder.append(field.getName()).append(":").append("{");
                buildQueryServerParameterForObject(field.getType(), builder);
                builder.append("}");
            } else if(field.isAnnotationPresent(GraphQLParameter.class)) {
                GraphQLParameter parameterAnnotation = field.getAnnotation(GraphQLParameter.class);
                builder.append(field.getName()).append(":");
                builder.append("$").append(parameterAnnotation.name());
            }
            builder.append(" ");
        }
    }

    private <T> void putAllParametersAndValuesInHash(T objectToGenerate, HashMap<String,Object> hash){
        Field[] fields = objectToGenerate.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(objectToGenerate);
                if(field.isAnnotationPresent(GraphQLParameterObject.class)) {
                    putAllParametersAndValuesInHash(fieldValue, hash);
                } else if(field.isAnnotationPresent(GraphQLParameter.class)) {
                    GraphQLParameter parameterAnnotation = field.getAnnotation(GraphQLParameter.class);
                    hash.put(parameterAnnotation.name(), fieldValue);
                }
            } catch (IllegalAccessException e) {
                throw new GraphQLQueryGeneratorException("The value of parameter " + field + " cannot be accessed.");
            }
        }
    }


}

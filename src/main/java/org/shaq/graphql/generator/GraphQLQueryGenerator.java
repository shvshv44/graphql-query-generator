package org.shaq.graphql.generator;


import org.shaq.graphql.annotations.*;
import org.shaq.graphql.exceptions.GraphQLQueryGeneratorException;
import org.shaq.graphql.generator.parameters.GraphqlParameterGenerator;
import org.shaq.graphql.models.GraphqlQueryTypeModel;

import java.lang.reflect.Field;


public class GraphQLQueryGenerator {

    private GraphqlParameterGenerator paramGenerator;

    public GraphQLQueryGenerator() {
        paramGenerator = new GraphqlParameterGenerator();
    }

    public String generateQuery(Class<?> classToGenerate, boolean prettyPrinting) throws GraphQLQueryGeneratorException {
        if (classToGenerate.isAnnotationPresent(GraphQLQuery.class)) {

            GraphQLQuery graphQLQueryAnnotation = classToGenerate.getAnnotation(GraphQLQuery.class);
            GraphqlQueryStringBuilder builder = new GraphqlQueryStringBuilder();

            addQueryType(classToGenerate, builder);
            builder.append(" ").append(graphQLQueryAnnotation.name());
            writeParametersOfQuery(builder, graphQLQueryAnnotation.parametersClass());
            builder.append(" {");

            if (prettyPrinting) builder.newLine();
            generateObject(classToGenerate, builder, prettyPrinting, (prettyPrinting)? 1:0);

            builder.append("} ");

            return builder.createFinalQuery();

        } else {
            throw new GraphQLQueryGeneratorException("The class " + classToGenerate.getSimpleName() + " has no GraphQLQuery declaration.");
        }
    }

    public String generateQuery(Class<?> classToGenerate) throws GraphQLQueryGeneratorException {
        return  generateQuery(classToGenerate,false);
    }

    private void addQueryType(Class<?> classToGenerate,GraphqlQueryStringBuilder builder) throws GraphQLQueryGeneratorException {
        String type = GraphqlQueryTypeModel.QUERY.toString(); //default
        if (classToGenerate.isAnnotationPresent(GraphQLQueryType.class)) {
            type = classToGenerate.getAnnotation(GraphQLQueryType.class).value().toString();
            if(type == null) {
                throw new GraphQLQueryGeneratorException("The class " + classToGenerate.getSimpleName() + " has no valid Query Type.");
            }
        }

        builder.append(type);
    }

    private void writeParametersOfQuery(GraphqlQueryStringBuilder builder, Class parameters) {
        if (parameters != null) {
            builder.append(" (");
            builder.append(paramGenerator.generateParameterForClientQuery(parameters));
            builder.append(") ");
        }
    }

    private void writeParametersOfObject(GraphqlQueryStringBuilder builder, Class parameters) {
        if (parameters != null) {
            builder.append(" (");
            builder.append(paramGenerator.generateParameterForServerQuery(parameters));
            builder.append(") ");
        }
    }

    private void generateObject(Class<?> classToGenerate, GraphqlQueryStringBuilder builder, boolean prettyPrinting, int level) {
        Field [] fields = classToGenerate.getDeclaredFields();

        for (int index = 0; index < fields.length; index++) {

            Field field = fields[index];

            if (!isExcluded(field)) {
                String fieldName = handleFieldName(field);
                builder.appendByLevel(fieldName, (prettyPrinting) ? level + 1 : 0);
                handlePrameterizedObject(field, builder);

                if (isGraphqlQueryObject(field)) {
                    handleFieldGraphqlObject(field, builder, prettyPrinting, level);
                }

                if (prettyPrinting) builder.newLine();
                else builder.append(" ");
            }
        }

        handleInheritedObject(classToGenerate,builder,prettyPrinting,level);
    }

    private void handleFieldGraphqlObject(Field field, GraphqlQueryStringBuilder builder, boolean prettyPrinting, int level) {
        builder.append(" {");

        if (prettyPrinting) builder.newLine();

        if (isFieldGraphqlInnerObject(field)) {
            Class<?> innerClass = field.getAnnotation(GraphQLQueryInnerObject.class).value();
            generateObject(innerClass, builder, prettyPrinting,level + 1);
        } else if (isFieldGraphqlObject(field)) {
            generateObject(field.getType(), builder, prettyPrinting, (prettyPrinting)? level + 1: 0);
        }

        builder.appendByLevel("} ", (prettyPrinting) ? level + 1 : 0);
    }

    private void handleInheritedObject(Class<?> classToGenerate, GraphqlQueryStringBuilder builder, boolean prettyPrinting, int level) {
        if (classToGenerate.isAnnotationPresent (GraphQLQueryInheritedObject.class)) {
            Class<?> [] inheritedClasses = classToGenerate.getAnnotation(GraphQLQueryInheritedObject.class).value();

            for (Class<?> inheritedClass : inheritedClasses) {
                if (inheritedClass.getDeclaredFields().length > 0) {
                    builder.appendByLevel("... on " + inheritedClass.getSimpleName() + " {" , (prettyPrinting)? level + 1: 0);
                    if (prettyPrinting) builder.newLine();
                    generateObject(inheritedClass, builder, prettyPrinting,level + 1);
                    if (prettyPrinting) builder.newLine();
                    builder.appendByLevel("} ", (prettyPrinting)? level + 1: 0);
                }

                if (prettyPrinting) builder.newLine();
                else builder.append(" ");
            }
        }
    }

    private void handlePrameterizedObject(Field field, GraphqlQueryStringBuilder builder) {
        if (field.isAnnotationPresent(GraphQLQueryPrameterizedObject.class)) {
            GraphQLQueryPrameterizedObject prameterizedObject = field.getAnnotation(GraphQLQueryPrameterizedObject.class);
            writeParametersOfObject(builder, prameterizedObject.parametersClass());
        }
    }

    public String handleFieldName(Field field){
        if(isFieldGraphqlSerializedName(field)) {
            return field.getAnnotation(GraphQLSerializedName.class).value();
        }
        return field.getName();
    }

    private boolean isGraphqlQueryObject(Field field) {
        return  isFieldGraphqlInnerObject(field) ||
                isFieldGraphqlObject(field);
    }

    private boolean isFieldGraphqlObject(Field field) {
        return field.getType().isAnnotationPresent(GraphQLQueryObject.class) ||
                field.getType().isAnnotationPresent(GraphQLQueryInheritedObject.class);
    }

    private boolean isFieldGraphqlInnerObject(Field field) {
        return field.isAnnotationPresent(GraphQLQueryInnerObject.class);
    }

    private boolean isFieldGraphqlSerializedName(Field field) {
        return field.isAnnotationPresent(GraphQLSerializedName.class);
    }

    private boolean isExcluded(Field field) {
        return field.isAnnotationPresent(GraphQLQueryExcludeField.class);
    }

}

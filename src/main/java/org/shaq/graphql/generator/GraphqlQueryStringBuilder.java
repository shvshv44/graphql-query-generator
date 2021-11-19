package org.shaq.graphql.generator;


public class GraphqlQueryStringBuilder {

    private StringBuilder builder;

    public GraphqlQueryStringBuilder() {
        this.builder = new StringBuilder("");
    }

    public GraphqlQueryStringBuilder append (String stringToAppend) {
        builder.append(stringToAppend);
        return this;
    }

    public GraphqlQueryStringBuilder tabsByLevel(int level) {
        while (level-- != 0) {
            builder.append("\t");
        }
        return this;
    }

    public GraphqlQueryStringBuilder appendByLevel(String stringToAppend, int level) {
        tabsByLevel(level);
        builder.append(stringToAppend);
        return this;
    }

    public GraphqlQueryStringBuilder newLine() {
        builder.append("\n");
        return this;
    }

    public String createFinalQuery() {
        return builder.toString();
    }

    public GraphqlQueryStringBuilder removeLastCharacters(int numberToRemove) {
        if(builder.length() >= numberToRemove)  {
            builder.delete(builder.length() - numberToRemove,builder.length());
        }
        return this;
    }
}

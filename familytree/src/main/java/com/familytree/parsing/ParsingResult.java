package com.familytree.parsing;

/**
 * The ParsingResult class represents the result of parsing a given text file.
 * It contains counts for various parsing outcomes such as created members,
 * duplicated members, created relationships, and duplicated relationships.
 * @author maddie
 */
public class ParsingResult {
    /** The count of members created during parsing. */
    private int created;

    /** The count of duplicated members found during parsing. */
    private int duplicated;

    /** The count of relationships created during parsing. */
    private int relationshipsCreated;

    /** The count of duplicated relationships found during parsing. */
    private int relationshipsDuplicated;

    /**
     * Gets the count of members created during parsing.
     * @return The count of created members.
     */
    public int getCreated() {
        return created;
    }

    /**
     * Sets the count of members created during parsing.
     * @param created The count of created members.
     */
    public void setCreated(int created) {
        this.created = created;
    }

    /**
     * Gets the count of duplicated members found during parsing.
     * @return The count of duplicated members.
     */
    public int getDuplicated() {
        return duplicated;
    }

    /**
     * Sets the count of duplicated members found during parsing.
     * @param duplicated The count of duplicated members.
     */
    public void setDuplicated(int duplicated) {
        this.duplicated = duplicated;
    }

    /**
     * Gets the count of relationships created during parsing.
     * @return The count of created relationships.
     */
    public int getRelationshipsCreated() {
        return relationshipsCreated;
    }

    /**
     * Sets the count of relationships created during parsing.
     * @param relationshipsCreated The count of created relationships.
     */
    public void setRelationshipsCreated(int relationshipsCreated) {
        this.relationshipsCreated = relationshipsCreated;
    }

    /**
     * Gets the count of duplicated relationships found during parsing.
     * @return The count of duplicated relationships.
     */
    public int getRelationshipsDuplicated() {
        return relationshipsDuplicated;
    }

    /**
     * Sets the count of duplicated relationships found during parsing.
     * @param relationshipsDuplicated The count of duplicated relationships.
     */
    public void setRelationshipsDuplicated(int relationshipsDuplicated) {
        this.relationshipsDuplicated = relationshipsDuplicated;
    }
}

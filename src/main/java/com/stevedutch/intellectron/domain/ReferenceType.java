package com.stevedutch.intellectron.domain;

/**
 * Defines the types of relationships or connections that can exist between Zettel notes.
 */
public enum ReferenceType {
    FOLLOWS_FROM,      // Direct continuation of thought
    CONTRADICTS,       // Opposing viewpoint
    SUPPORTS,          // Supporting evidence
    RELATES_TO,        // General relation
    BRANCHES_FROM      // New line of thinking
} 
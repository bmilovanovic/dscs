package com.example.aninterface;

/**
 * Abstracts an entity that can be parsed into an storable object.
 */
public interface Parsable {

    /**
     * Fetch the document from a web, and parse it's html into more structured form.
     * @return An object that can be stored.
     */
    Storable parse();
}

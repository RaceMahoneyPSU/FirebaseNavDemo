package com.example.firebasenavdemo.ui.fragments.model;

import com.google.firebase.firestore.DocumentId;

/**
 * A simple data model class (POJO) representing an item with a title and a subtitle.
 * This class is designed to be used with Firebase Firestore.
 */
public class SimpleItem {

    /**
     * The unique ID of the document from Firestore.
     * The @DocumentId annotation tells Firestore to automatically populate this field
     * with the document's ID when data is retrieved.
     */
    @DocumentId
    public String id;

    /**
     * The main title of the item.
     */
    public String title;

    /**
     * A secondary piece of information or description for the item.
     */
    public String subtitle;

    /**
     * No-argument constructor.
     * This is required by Firebase Firestore for deserializing documents
     * from the database back into SimpleItem objects.
     */
    public SimpleItem() {}

    /**
     * Constructs a new SimpleItem with a title and subtitle.
     * Useful for creating new items before sending them to Firestore.
     *
     * @param title    The title of the item.
     * @param subtitle The subtitle of the item.
     */
    public SimpleItem(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }
}

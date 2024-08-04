package com.example.callhistory.model;

import java.util.ArrayList;
import java.util.List;

public class AlphabetGroup {
    private char letter;
    private List<ContactItem> contacts;

    public AlphabetGroup(char letter) {
        this.letter = letter;
        contacts = new ArrayList<>();
    }

    public char getLetter() {
        return letter;
    }

    public List<ContactItem> getContacts() {
        return contacts;
    }

    public void addContact(ContactItem contact) {
        contacts.add(contact);
    }
}


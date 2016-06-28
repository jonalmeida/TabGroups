package com.jonalmeida.tabgroups;

import java.util.LinkedList;

public class Group extends LinkedList<Tab> {
    private String groupName = "Default";

    public Group(String name) {
        this.groupName = name;
    }

    public String name() {
        return groupName;
    }
}

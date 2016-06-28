package com.jonalmeida.tabgroups;

import android.support.v7.app.AppCompatActivity;

import java.util.LinkedHashMap;

public class GroupData extends LinkedHashMap<String, Group> {
    private static GroupData ourInstance = new GroupData();

    public RecyclerAdapter adapter;
    public static GroupData getInstance() {
        return ourInstance;
    }

    private GroupData() {}

    public void addNewUrl(String url, String keyword) {
        if (containsKey(keyword)) {
            Group group = get(keyword);
            // Shell tab with just a url and topKeyword.
            group.add(new Tab(url, keyword));
        }
    }

    public void addTab(Tab tab, AppCompatActivity activity) {
        if (containsKey(tab.topKeyword())) {
            Group group = get(tab.topKeyword());
            group.add(tab);
        } else {
            Group group;
            group = new Group(tab.topKeyword());
            if (tab.topKeyword() == null) {
                if (containsKey("Default")) {
                    group = get("Default");
                } else {
                    group = new Group("Default");
                }
            }
            group.add(tab);
            put(group.name(), group);
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}

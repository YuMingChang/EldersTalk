package com.example.changgg.elderstalk.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Contact {
    private String name;
    private static String TAG = "Contact";
    public Contact() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<Contact> generateSampleList(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        List<Contact> list = new ArrayList<>();
        for(int i=0; i<files.length; i++){
            Contact contact = new Contact();
            contact.setName(files[i].getName());
            list.add(contact);
        }
        return list;
    }


}

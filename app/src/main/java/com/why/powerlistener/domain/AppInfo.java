package com.why.powerlistener.domain;

public class AppInfo {

    private int id;

    private String name;

    private boolean isSave;

    public AppInfo(int id, String name, boolean isSave) {
        this.id = id;
        this.name = name;
        this.isSave = isSave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isSave=" + isSave +
                '}';
    }
}

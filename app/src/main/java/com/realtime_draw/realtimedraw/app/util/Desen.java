package com.realtime_draw.realtimedraw.app.util;




public class Desen {
    private long id;
    private String nume_desen;
    private String isPublic;
    private String isGroup;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return nume_desen;
    }

    public void setName(String nume_desen) {
        this.nume_desen = nume_desen;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return nume_desen;
    }



    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }



    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

}

package com.lamu.util;

/**
 * Created by songliang on 2015/12/15.
 *
 * @author songliang
 */
public enum FileType {
    JPEG("FFD8FF"),
    PNG("89504E47");
    private String name;

    FileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

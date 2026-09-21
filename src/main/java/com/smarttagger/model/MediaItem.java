package com.smarttagger.model;
import java.util.List;
public abstract class MediaItem {
    protected String fileName;
    protected String description;
    public MediaItem(String fn, String desc) { this.fileName = fn; this.description = desc; }
    public abstract List<String> generateTags();
}

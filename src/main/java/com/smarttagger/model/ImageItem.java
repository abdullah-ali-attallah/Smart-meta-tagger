package com.smarttagger.model;
import java.util.*;
public class ImageItem extends MediaItem {
    private String resolution;
    public ImageItem(String fn, String desc, String res) { super(fn, desc); this.resolution = res; }
    @Override public List<String> generateTags() {
        List<String> tags = new ArrayList<>();
        tags.add("#Image");
        if(resolution != null && !resolution.isEmpty()) tags.add("#" + resolution.replaceAll("[^a-zA-Z0-9]", ""));
        for(String word : description.split(" ")) if(word.length() > 3) tags.add("#" + word.replaceAll("[^a-zA-Z0-9]", ""));
        return tags;
    }
}

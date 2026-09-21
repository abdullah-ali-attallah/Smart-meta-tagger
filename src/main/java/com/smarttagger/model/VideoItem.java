package com.smarttagger.model;
import java.util.*;
public class VideoItem extends MediaItem {
    private int duration;
    public VideoItem(String fn, String desc, int dur) { super(fn, desc); this.duration = dur; }
    @Override public List<String> generateTags() {
        List<String> tags = new ArrayList<>();
        tags.add("#Video");
        if(duration < 60) tags.add("#Shorts");
        for(String word : description.split(" ")) if(word.length() > 3) tags.add("#" + word.replaceAll("[^a-zA-Z0-9]", ""));
        return tags;
    }
}

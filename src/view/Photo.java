package view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String filePath;
    private String description;
    private List<String> tags;

    public Photo(String filePath) {
        this.filePath = filePath;
        this.description = "";
        this.tags = new ArrayList<>();
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Override
    public String toString() {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String desc = description.isEmpty() ? "" : " - " + description;
        String tagStr = tags.isEmpty() ? "" : " (" + String.join(", ", tags) + ")";
        return fileName + desc + tagStr;
    }
}
    
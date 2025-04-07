package view;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Model class representing a Photo in an album.
 * Stores the file path, caption (description), date taken (as last modified date), and tags associated with the photo.
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 2L;

    /** The file path of the photo. */
    private String filePath;
    /** The caption/description of the photo. */
    private String description;
    /** The date and time the photo was taken (using last modified timestamp). */
    private LocalDateTime date;
    /** The list of tags associated with the photo (tag name-value pairs). */
    private List<Tag> tags;

    /**
     * Constructs a Photo with the given file path.
     * Initializes description to empty and sets the photo's date to the file's last modified date.
     * @param filePath the file path of the photo
     */
    public Photo(String filePath) {
        this.filePath = filePath;
        this.description = "";
        this.tags = new ArrayList<>();
        File file = new File(filePath);
        // Set date taken as last modified time (in system default timezone)
        this.date = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
    }

    /**
     * Returns the file path of the photo.
     * @return the photo's file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns the description (caption) of the photo.
     * @return the photo's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the date and time the photo was taken.
     * @return the photo's date/time
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Returns the list of tags associated with the photo.
     * @return a list of Tag objects for this photo
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Sets the description (caption) of the photo.
     * @param description the new description for the photo
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Adds a tag to this photo.
     * The tag is defined by a name and value. If a tag with the same name and value already exists, or
     * if the tag is of a type that allows only a single value (e.g. "location") and one already exists, 
     * this method will not add a duplicate.
     * @param tagName the name of the tag
     * @param tagValue the value of the tag
     * @return true if the tag was added successfully, false if it was not added (due to duplication or rule conflict)
     */
    public boolean addTag(String tagName, String tagValue) {
        String nameLower = tagName.toLowerCase();
        String valueLower = tagValue.toLowerCase();
        // No duplicate tag with same name and value
        for (Tag t : tags) {
            if (t.getName().equalsIgnoreCase(tagName) && t.getValue().equalsIgnoreCase(tagValue)) {
                return false;
            }
        }
        // If tag type is single-value (location), ensure none already exists
        if (nameLower.equals("location")) {
            for (Tag t : tags) {
                if (t.getName().equalsIgnoreCase("location")) {
                    return false;
                }
            }
        }
        Tag newTag = new Tag(tagName, tagValue);
        tags.add(newTag);
        return true;
    }

    /**
     * Removes a tag from this photo.
     * @param tagName the name of the tag to remove
     * @param tagValue the value of the tag to remove
     */
    public void removeTag(String tagName, String tagValue) {
        Tag toRemove = null;
        for (Tag t : tags) {
            if (t.getName().equalsIgnoreCase(tagName) && t.getValue().equalsIgnoreCase(tagValue)) {
                toRemove = t;
                break;
            }
        }
        if (toRemove != null) {
            tags.remove(toRemove);
        }
    }

    /**
     * Checks if this photo has a tag with the given name and value.
     * @param name the tag name
     * @param value the tag value
     * @return true if the photo has a matching tag, false otherwise
     */
    public boolean hasTag(String name, String value) {
        for (Tag t : tags) {
            if (t.getName().equalsIgnoreCase(name) && t.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        // Display file name, optional description, and tags
        String fileName = new File(filePath).getName();
        String descStr = description.isEmpty() ? "" : " - " + description;
        String tagStr = tags.isEmpty() ? "" : " (" + tags.stream().map(Tag::toString).collect(Collectors.joining(", ")) + ")";
        return fileName + descStr + tagStr;
    }
}

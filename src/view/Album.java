package view;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing an album containing photos.
 * Stores the album name and list of photos.
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The name of the album. */
    private String name;
    /** The list of photos in the album. */
    private List<Photo> photos;

    /**
     * Constructs an Album with a given name.
     * @param name the name of the album
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    /**
     * Returns the name of the album.
     * @return the album name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the album.
     * @param name the new album name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of photos in the album.
     * @return the list of Photo objects in this album
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Adds a photo to the album.
     * @param photo the Photo to add
     */
    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    /**
     * Removes a photo from the album.
     * @param photo the Photo to remove
     */
    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    @Override
    public String toString() {
        // Album display includes name, number of photos, and date range of photos if any
        int count = photos.size();
        if (count == 0) {
            return name + " (0 photos)";
        }
        // Determine earliest and latest photo dates
        LocalDate earliest = null;
        LocalDate latest = null;
        for (Photo p : photos) {
            LocalDate photoDate = p.getDate().toLocalDate();
            if (earliest == null || photoDate.isBefore(earliest)) {
                earliest = photoDate;
            }
            if (latest == null || photoDate.isAfter(latest)) {
                latest = photoDate;
            }
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String startDate = earliest != null ? earliest.format(df) : "";
        String endDate = latest != null ? latest.format(df) : "";
        if (earliest != null && latest != null && earliest.equals(latest)) {
            // If only one date (all photos taken on the same day)
            return name + " (" + count + " photos, " + startDate + ")";
        }
        return name + " (" + count + " photos, " + startDate + " - " + endDate + ")";
    }
}

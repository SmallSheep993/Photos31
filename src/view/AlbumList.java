package view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a collection of albums for a user.
 * Provides methods to manage the list of albums.
 */
public class AlbumList implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The list of albums belonging to the user. */
    private List<Album> albums;

    /**
     * Constructs an AlbumList with no albums.
     */
    public AlbumList() {
        albums = new ArrayList<>();
    }

    /**
     * Returns the list of albums.
     * @return the list of Album objects
     */
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * Adds an album to the collection.
     * @param album the Album to add
     */
    public void addAlbum(Album album) {
        albums.add(album);
    }

    /**
     * Removes an album from the collection.
     * @param album the Album to remove
     */
    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    /**
     * Checks if an album with the given name exists (case-insensitive).
     * @param name the album name to check
     * @return true if an album with the name exists in this collection, false otherwise
     */
    public boolean albumExists(String name) {
        for (Album album : albums) {
            if (album.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}

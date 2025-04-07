package view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlbumList implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Album> albums;

    public AlbumList() {
        albums = new ArrayList<>();
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void addAlbum(Album album) {
        albums.add(album);
    }

    public void removeAlbum(Album album) {
        albums.remove(album);
    }

    public boolean albumExists(String name) {
        for (Album album : albums) {
            if (album.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}

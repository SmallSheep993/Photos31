package view;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for saving and loading AlbumList objects to and from disk.
 * Data is stored as serialized object files named <username>_albums.dat.
 */
public class AlbumListHelper {

    /**
     * Saves the given AlbumList for the specified user to disk.
     * @param username the username whose album list is to be saved
     * @param albumList the AlbumList object to save
     */
    public static void save(String username, AlbumList albumList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(username + "_albums.dat"))) {
            oos.writeObject(albumList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the AlbumList for the specified user from disk.
     * @param username the username whose album list to load
     * @return the AlbumList for the user, or a new AlbumList if none was found
     */
    public static AlbumList load(String username) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(username + "_albums.dat"))) {
            return (AlbumList) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new AlbumList();
        }
    }
}

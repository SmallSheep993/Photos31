package view;

import java.io.*;

public class AlbumListHelper {

    public static void save(String username, AlbumList albumList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(username + "_albums.dat"))) {
            oos.writeObject(albumList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AlbumList load(String username) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(username + "_albums.dat"))) {
            return (AlbumList) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new AlbumList();
        }
    }
}

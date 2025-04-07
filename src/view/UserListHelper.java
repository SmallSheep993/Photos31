package view;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for saving and loading the UserList to and from disk.
 * The user list is stored in a file "users.dat".
 */
public class UserListHelper {

    /**
     * Saves the given UserList to disk.
     * @param userList the UserList to save
     */
    public static void save(UserList userList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            oos.writeObject(userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the UserList from disk.
     * @return the loaded UserList, or a new UserList if none exists
     */
    public static UserList load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.dat"))) {
            return (UserList) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new UserList();
        }
    }
}

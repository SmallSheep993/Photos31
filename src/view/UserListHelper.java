package view;

import java.io.*;

public class UserListHelper {

    public static void save(UserList userList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/userList.ser"))) {
            oos.writeObject(userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UserList load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/userList.ser"))) {
            return (UserList) ois.readObject();
        } catch (Exception e) {
            return new UserList();
        }
    }
}

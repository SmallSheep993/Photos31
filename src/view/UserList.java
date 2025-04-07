package view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserList implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> users;

    public UserList() {
        users = new ArrayList<>();
    }

    public List<String> getUsers() {
        return users;
    }

    public void addUser(String username) {
        users.add(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public boolean userExists(String username) {
        return users.contains(username);
    }

   
}

package view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing the list of users in the application (excluding the admin user).
 */
public class UserList implements Serializable {
    private static final long serialVersionUID = 1L;
    /** The list of usernames. */
    private List<String> users;

    /**
     * Constructs an empty UserList.
     */
    public UserList() {
        users = new ArrayList<>();
    }

    /**
     * Returns the list of usernames.
     * @return list of usernames as strings
     */
    public List<String> getUsers() {
        return users;
    }

    /**
     * Adds a user to the list.
     * @param username the username to add
     */
    public void addUser(String username) {
        users.add(username);
    }

    /**
     * Removes a user from the list.
     * @param username the username to remove
     */
    public void removeUser(String username) {
        users.removeIf(u -> u.equalsIgnoreCase(username));
    }

    /**
     * Checks if a username exists in the list (case-insensitive).
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(String username) {
        for (String u : users) {
            if (u.equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
}

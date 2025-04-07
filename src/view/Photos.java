package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import view.Album;
import view.Photo;
import view.AlbumList;
import view.AlbumListHelper;
import view.UserList;
import view.UserListHelper;

/**
 * Main application class for the Photos program.
 * Handles initialization of data (including stock user and photos) and launching the JavaFX UI.
 */
public class Photos extends Application {
    /**
     * Entry point of the Photos application.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize user list and stock user/album if not present
        UserList users = UserListHelper.load();
        // Ensure stock user exists in user list
        if (!users.userExists("stock")) {
            users.addUser("stock");
        }
        // Always ensure the special admin user exists conceptually (admin is not stored in file)
        // Save any updates to user list
        UserListHelper.save(users);
        // Ensure stock user's album and photos are pre-loaded
        AlbumList stockAlbumList = AlbumListHelper.load("stock");
        if (stockAlbumList.getAlbums().isEmpty() || !stockAlbumList.albumExists("stock")) {
            Album stockAlbum = new Album("stock");
            // Example stock photos filenames (actual images should be placed in the project data/stock directory)
            String stockDir = "data/stock/";
            String[] stockPhotos = { "stock1.jpg", "stock2.jpg", "stock3.jpg", "stock4.jpg", "stock5.jpg" };
            for (String filename : stockPhotos) {
                File photoFile = new File(stockDir + filename);
                if (photoFile.exists()) {
                    Photo photo = new Photo(photoFile.getAbsolutePath());
                    stockAlbum.addPhoto(photo);
                }
            }
            stockAlbumList.addAlbum(stockAlbum);
            AlbumListHelper.save("stock", stockAlbumList);
        }

        // Load login UI
        Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        primaryStage.setTitle("Photos31 Login");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Auto-save all data on program exit to prevent data loss
        UserList users = UserListHelper.load();
        UserListHelper.save(users);
        // Note: Individual album data is saved on each operation throughout the program
    }
}

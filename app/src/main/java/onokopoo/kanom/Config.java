package onokopoo.kanom;

import android.app.Application;

/**
 * Created by onokopoo on 2/21/2016.
 */
public class Config extends Application {
    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://10.35.23.78/AndroidFileUpload/upload.php";
    public static String URL_LOGIN = "http://10.35.23.78/login/login.php";
    public static String URL_REGISTER = "http://10.35.23.78/login/register.php";
    public static String URL_VIEW = "http://10.35.23.78/login/view.php";
    public static String URL_FAVORITE = "http://10.35.23.78/login/favorite.php";
    public static String URL_HISTORY = "http://10.35.23.78/login/history.php";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

    private String username;
    private String email;
    private String user_id;

    public String getUsernme() {
        return username;
    }

    public void setUsername(String aName) {
        username = aName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String aEmail) {
        email = aEmail;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String auser_id) {
        user_id = auser_id;
    }
}
package fr.uga.julioju.sempic;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class Namespaces {

    private final static String NS = "http://fr.uga.julioju.sempic/ResourcesCreated/";

    private final static String photoNS = Namespaces.NS + "photo/";

    private final static String albumNS = Namespaces.NS + "album/";

    private final static String userNS = Namespaces.NS + "user/";

    public static String getPhotoUri(long photoId) {
        return photoNS + photoId;
    }

    public static String getAlbumUri(long albumId) {
        return albumNS + albumId;
    }

    public static String getUserUri(String login) {
        return userNS + login;
    }

}

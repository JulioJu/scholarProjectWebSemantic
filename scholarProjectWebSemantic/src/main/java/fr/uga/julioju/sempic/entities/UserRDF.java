package fr.uga.julioju.sempic.entities;

public class UserRDF {

    public enum UserGroup {
        NORMAL_USER_GROUP,
        ADMIN_GROUP
    }

    private String login;

    private String password;

    private UserGroup userGroup;

    public UserRDF(
            String login,
            String password,
            UserGroup userGroup) {
        this.login = login;
        this.password = password;
        this.userGroup = userGroup;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

}

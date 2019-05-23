package fr.uga.julioju.sempic.entities;

import javax.validation.constraints.NotNull;

public class UserRDF {

    public enum UserGroup {
        NORMAL_USER_GROUP,
        ADMIN_GROUP
    }

    @NotNull
    private String login;

    @NotNull
    private String password;

    @NotNull
    private UserGroup userGroup;

    public UserRDF() {
    }

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

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserRDF)) {
            return false;
        }
        UserRDF userRDF = (UserRDF) o;
        return this.login.equals(userRDF.login);
    }

}

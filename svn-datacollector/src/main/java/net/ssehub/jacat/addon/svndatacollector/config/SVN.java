package net.ssehub.jacat.addon.svndatacollector.config;

public class SVN {

    private String username;
    private String password;
    private String url;

    public SVN(String username, String password, String url) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    public SVN() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}

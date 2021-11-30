package net.ssehub.jacat.addon.exercise_submitter_server_datacollector.config;


public class ExerciseSubmitterServerConfig {

    private String url;
    
    private String authUrl;
    
    private String stuMgmtUrl;
    
    private String username;
    
    private String password;
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }
    
    public void setStuMgmtUrl(String stuMgmtUrl) {
        this.stuMgmtUrl = stuMgmtUrl;
    }
    
    public String getStuMgmtUrl() {
        return stuMgmtUrl;
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
    
}

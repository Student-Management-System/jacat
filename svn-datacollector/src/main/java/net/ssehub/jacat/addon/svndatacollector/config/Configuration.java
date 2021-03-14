package net.ssehub.jacat.addon.svndatacollector.config;

public class Configuration {

    private SVN svn;

    public Configuration(SVN svn) {
        this.svn = svn;
    }

    public Configuration() {
    }

    public SVN getSvn() {
        return svn;
    }

    public void setSvn(SVN svn) {
        this.svn = svn;
    }
}

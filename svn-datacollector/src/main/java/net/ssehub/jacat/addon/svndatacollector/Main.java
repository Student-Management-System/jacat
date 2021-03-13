package net.ssehub.jacat.addon.svndatacollector;

import net.ssehub.jacat.api.addon.Addon;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends Addon {

    @Override
    public void onEnable() {
        String username = "";
        char[] password = {};
        String url = "url";

        SVNClientManager clientManager = SVNClientManager.newInstance(null,
                BasicAuthenticationManager.newInstance(username, password));
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        Path workdir;
        try {
            workdir = Files.createTempDirectory("");
        } catch (IOException e) {
            throw new RuntimeException("Could not create temporary directory", e);
        }

        SVNDataCollector collector = new SVNDataCollector(this.getLogger(), updateClient, workdir, url);
        this.getWorker().registerDataCollector(this, collector);
    }

}

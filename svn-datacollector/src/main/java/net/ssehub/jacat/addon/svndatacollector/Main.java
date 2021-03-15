package net.ssehub.jacat.addon.svndatacollector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.ssehub.jacat.addon.svndatacollector.config.Configuration;
import net.ssehub.jacat.api.addon.Addon;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends Addon {

    private Path pluginDir = Path.of( "addons", "svn-dc");
    private Configuration configuration;

    @Override
    public void onEnable() {
        this.pluginDir = this.getWorker().getWorkingDir().resolve(this.pluginDir);

        initializeConfiguration();

        SVNClientManager clientManager = SVNClientManager.newInstance(null,
                BasicAuthenticationManager.newInstance(configuration.getSvn().getUsername(),
                        configuration.getSvn().getPassword().toCharArray()));
        SVNUpdateClient updateClient = clientManager.getUpdateClient();

        Path workdir = pluginDir.resolve("tmp");
        if (!workdir.toFile().exists() && !workdir.toFile().mkdir()) {
            throw new RuntimeException("Could not create temporary directory");
        }

        SVNURL svnUrl;
        String url = configuration.getSvn().getUrl();
        try {
            svnUrl = SVNURL.parseURIEncoded(url);
        } catch (SVNException e) {
            this.getLogger().error("Couldn't parse URL: " + url, e);
            throw new RuntimeException("Couldn't parse URL: " + url, e);
        }

        try {
            SVNInfo svnInfo = clientManager.getWCClient()
                    .doInfo(svnUrl, SVNRevision.HEAD, SVNRevision.HEAD);
            this.getLogger().info("Connected to: " + url);
        } catch (SVNException e) {
            throw new RuntimeException("Couldn't connect to " + url + ", " + e.getCause(), e.getCause());
        }

        SVNDataCollector collector = new SVNDataCollector(this.getLogger(), updateClient,
                workdir, svnUrl);
        this.getWorker().registerDataCollector(this, collector);
    }

    private void initializeConfiguration() {
        if (!pluginDir.toFile().exists() && !pluginDir.toFile().mkdir()) {
            throw new RuntimeException("Cannot create config directory for 'svndatacollector'");
        }

        Path configFile = pluginDir.resolve(Path.of("config.yml"));
        try {
            copyExampleConfig(configFile);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create config file for 'svndatacollector'", e);
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();

        try {
            this.configuration = mapper.readValue(configFile.toFile(), Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config file for 'svndatacollector'", e);
        }

    }

    private void copyExampleConfig(Path target) throws IOException {
        if (!target.toFile().exists()) {
            InputStream config = this.getClass().getClassLoader().getResourceAsStream("config.yml");
            Files.copy(config, target);
        }
    }

}

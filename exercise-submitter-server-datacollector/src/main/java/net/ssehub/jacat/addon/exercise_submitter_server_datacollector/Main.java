package net.ssehub.jacat.addon.exercise_submitter_server_datacollector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import net.ssehub.jacat.addon.exercise_submitter_server_datacollector.config.Configuration;
import net.ssehub.jacat.api.addon.Addon;

public class Main extends Addon {

    private Path pluginDir = Path.of("addons", "exericse-submitter-server-dc");
    
    private Configuration configuration;
    
    @Override
    public void onEnable() {
        this.pluginDir = getWorker().getWorkingDir().resolve(this.pluginDir);
        
        initializeConfiguration();
        
        Path workdir = pluginDir.resolve("tmp");
        if (!Files.isDirectory(workdir)) {
            try {
                Files.createDirectory(workdir);
            } catch (IOException e) {
                throw new RuntimeException("Could not create temporary directory", e);
            }
        }
        
        ExerciseSubmitterServerDataCollector collector
                = new ExerciseSubmitterServerDataCollector(getLogger(), configuration.getExerciseSubmitterServer(), workdir);
        this.getWorker().registerDataCollector(this, collector);
    }
    
    private void initializeConfiguration() {
        if (!Files.isDirectory(pluginDir)) {
            try {
                Files.createDirectory(pluginDir);
            } catch (IOException e) {
                throw new RuntimeException("Cannot create config directory", e);
            }
        }
        
        Path configFile = pluginDir.resolve(Path.of("config.yml"));
        if (!Files.isRegularFile(configFile)) {
            try {
                Files.copy(getClass().getResourceAsStream("config.yml"), configFile);
            } catch (IOException e) {
                getLogger().warn("Failed to write example configuration file: " + e.getMessage());
            }
        }
        
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        
        try {
            this.configuration = mapper.readValue(Files.newInputStream(configFile), Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read configuration file", e);
        }
    }

}

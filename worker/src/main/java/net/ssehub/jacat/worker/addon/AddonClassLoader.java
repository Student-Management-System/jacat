package net.ssehub.jacat.worker.addon;

import net.ssehub.jacat.api.addon.Addon;
import net.ssehub.jacat.api.addon.AddonDescription;
import net.ssehub.jacat.worker.JacatWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class AddonClassLoader extends URLClassLoader {
    public static final String WORKER_FIELD_NAME = "worker";
    public static final String DESCRIPTION_FIELD_NAME = "description";
    public static final String LOGGER_FIELD_NAME = "logger";

    private final File addonJarFile;
    private Addon addon;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public AddonClassLoader(final File addonJarFile, AddonDescription addonDescription, JacatWorker jacatWorker) throws MalformedURLException {
        super(new URL[] {addonJarFile.toURI().toURL()}, AddonClassLoader.class.getClassLoader());
        this.addonJarFile = addonJarFile;

        try {
            String loggerName = "A:[" + addonDescription.getName() + "]";
            logger.info("Going to load: " + loggerName + " - " + addonJarFile.getAbsolutePath());
            Class<?> jarClass = Class.forName(addonDescription.getMainClass(), true, this);
            Object addon = jarClass.getDeclaredConstructor().newInstance();

            setCustomValue(addon, WORKER_FIELD_NAME, jacatWorker);
            setCustomValue(addon, DESCRIPTION_FIELD_NAME, addonDescription);
            setCustomValue(addon, LOGGER_FIELD_NAME, LoggerFactory.getLogger(loggerName));

            try {
                ((Addon) addon).onEnable();
            } catch (RuntimeException e) {
                throw new AddonNotLoadableException(e);
            }

            this.addon = (Addon) addon;

            logger.info("A:[" + addonDescription.getName() + "] successfully loaded.");
        } catch (InstantiationException
                | InvocationTargetException
                | IllegalAccessException
                | ClassNotFoundException
                | NoSuchMethodException
                | NoSuchFieldException e) {
            e.printStackTrace();
            throw new AddonNotLoadableException(e);
        }
    }

    public Addon getLoadedAddon() {
        return this.addon;
    }

    private void setCustomValue(Object addon, String declaredField, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field platform = addon.getClass().getSuperclass().getDeclaredField(declaredField);
        platform.setAccessible(true);
        platform.set(addon, value);
    }
}

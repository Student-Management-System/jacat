package net.ssehub.jacat.api.addon.result;

import java.util.Map;

public abstract class AbstractResultProcessor {

    public abstract void process(Map<String, Object> result);

}

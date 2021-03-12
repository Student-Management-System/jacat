package net.ssehub.jacat.platform.analysis.api;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ssehub.jacat.api.addon.data.DataSection;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAnalysisDto {

    private DataSection data;

    private Map<String, Object> request = new HashMap<>();

    @JsonAnySetter
    public void setRequestParameter(String key, Object value) {
        this.request.put(key, value);
    }
}

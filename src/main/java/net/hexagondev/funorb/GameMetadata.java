package net.hexagondev.funorb;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

@Data
@Builder
public class GameMetadata {

    private String name;
    private String internalName;
    private String baseUrl;
    private String mainClass;
    @Singular
    private Map<String, String> parameters;

    public String getParameter(String key) {
        return parameters.get(key);
    }
}

package com.youyinda.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "third-party")
public class ThirdPartyApiProperties {

    private PrintConfig print;

    private ExpressConfig express;

    private SyncConfig sync;

    @Data
    public static class PrintConfig {
        private List<ProviderConfig> providers;
    }

    @Data
    public static class ExpressConfig {
        private List<ProviderConfig> providers;
    }

    @Data
    public static class ProviderConfig {
        private String code;
        private String name;
        private String apiUrl;
        private String appId;
        private String appSecret;
        private Integer timeout;
    }

    @Data
    public static class SyncConfig {
        private Boolean enabled;
        private String cron;
        private String alertEmail;
    }
}

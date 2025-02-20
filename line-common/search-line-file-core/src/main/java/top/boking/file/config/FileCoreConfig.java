package top.boking.file.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("top.boking.file")
@Slf4j
public class FileCoreConfig {

    public FileCoreConfig() {
        log.info("FileCoreConfig init");
    }
}

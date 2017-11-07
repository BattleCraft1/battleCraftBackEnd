package pl.edu.pollub.battleCraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.edu.pollub.battleCraft.config.storage.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class BattleCraftApplication {

    public static void main(String[] args) {
        SpringApplication.run(BattleCraftApplication.class, args);
    }

}

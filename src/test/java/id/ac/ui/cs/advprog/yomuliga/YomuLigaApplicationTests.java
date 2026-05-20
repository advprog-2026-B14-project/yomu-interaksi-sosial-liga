package id.ac.ui.cs.advprog.yomuliga;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "achievement.service.base-url=http://localhost:8083",
        "achievement.service.daily-mission-path=/api/dummy/path",
        "learning.service.base-url=http://localhost:8081",
        "learning.service.token=dummy-token",
        "learning.service.stats-path=/api/dummy/stats"
})

@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
class YomuLigaApplicationTests {

    @Test
    void contextLoads() {
    }

}
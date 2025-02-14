package org.acme;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import io.quarkus.logging.Log;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.time.Duration;
import java.util.Map;
import org.junit.ClassRule;
import org.testcontainers.containers.MySQLContainer;

public class MySQLTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    @ClassRule
    public static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0.28");
//                    .withUsername("root")
//                    .withPassword("test")
//                    .withDatabaseName("test_db");

    @Override
    public Map<String, String> start() {
        Log.info("Starting database container");
        mysql.start();
        await().then().pollInterval(Duration.ofMillis(200)).untilAsserted(() -> {
            Log.info("Waiting for database to start");
            mysql.isRunning();
        });
        Log.warn("jdbc url: " + mysql.getJdbcUrl());
        return Map.of(
                "properties.db.url",
//                mysql.getJdbcUrl().replace("jdbc:mysql:", "jdbc:aws-wrapper:mysql"),
                mysql.getJdbcUrl(),
                "properties.db.user",
                mysql.getUsername(),
                "properties.db.password",
                mysql.getPassword());
    }

    @Override
    public void stop() {
        mysql.close();
    }
}

package org.acme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.awaitility.Awaitility.await;

import io.quarkus.test.common.TestResourceScope;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.jdbcclient.JDBCPool;
import jakarta.inject.Inject;
import java.time.Instant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@QuarkusTest
@Tag("integration")
@WithTestResource(value = MySQLTestResourceLifecycleManager.class, scope = TestResourceScope.MATCHING_RESOURCES)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseIT {

    private Database database;

    @Inject
    JDBCPool jdbcPool;

    @BeforeAll
    void setup() {
        database = new Database(jdbcPool);
    }

    @Test
    void should_save() {
        var time = Instant.now();
        assertThatNoException().isThrownBy(() -> database.save(1, time));
        await().untilAsserted(() -> assertThat(database.select(1)).isEqualTo(time.toEpochMilli()));
    }
}

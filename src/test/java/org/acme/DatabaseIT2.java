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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@QuarkusTest
@WithTestResource(value = MySQLTestResourceLifecycleManager.class, scope = TestResourceScope.RESTRICTED_TO_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseIT2 {

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
        assertThatNoException().isThrownBy(() -> database.save(2, time));
        await().untilAsserted(() -> assertThat(database.select(2)).isEqualTo(time.toEpochMilli()));
    }

}

package org.acme;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import org.junit.jupiter.api.Test;

class VertxJDBCPoolConfigTest {

    @Test
    void should_create_valid_jdbc_client_with_valid_properties() {
        Vertx vertx = Vertx.vertx();
        VertxJDBCPoolConfig vertxJDBCPoolConfig = new VertxJDBCPoolConfig();
        vertxJDBCPoolConfig.dbUrl = "testUrl";
        vertxJDBCPoolConfig.dbUser = "testUser";
        vertxJDBCPoolConfig.dbPassword = "testPassword";
        JDBCPool jdbcPool = vertxJDBCPoolConfig.createJdbcPool(vertx);
        assertThat(jdbcPool).isNotNull();
    }
}

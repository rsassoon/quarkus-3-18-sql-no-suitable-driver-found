package org.acme;

import io.quarkus.logging.Log;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Tuple;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
public class Database {

    private static final String REPLACE_QUERY = "REPLACE INTO test_table (id,time_long) VALUES (?,?) ";

    private static final String SELECT_QUERY = "SELECT time_long FROM test_table WHERE id = ?";

    private final JDBCPool jdbcPool;

    public Database(JDBCPool jdbcPool) {
        this.jdbcPool = jdbcPool;
    }

    public void save(int id, Instant time) {
        jdbcPool.preparedQuery(REPLACE_QUERY)
                .execute(Tuple.of(id, time.toEpochMilli()))
                .onSuccess(rows -> Log.info("Saved successfully"))
                .onFailure(e -> {
                    Log.error("Failed to save", e);
                });
    }

    public long select(int id) {
        var result = jdbcPool.preparedQuery(SELECT_QUERY)
                .execute(Tuple.of(id))
                .onSuccess(rows -> Log.info("Successfully retrieved time"))
                .onFailure(failure -> {
                    Log.errorv(failure, "Failed to get time");
                })
                .toCompletionStage()
                .toCompletableFuture()
                .join();
        return result.iterator().hasNext() ? result.iterator().next().getLong("time_long") : -1L;
    }
}

package org.acme;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import java.util.Map;

public class KafkaTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        return InMemoryConnector.switchOutgoingChannelsToInMemory("channel-out");
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}

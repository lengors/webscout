package io.github.lengors.webscout.domain.hazelcast.events.models

import com.hazelcast.topic.ITopic
import io.github.lengors.webscout.domain.events.models.Event
import org.springframework.core.ResolvableType
import java.util.UUID

data class HazelcastEventListener(
    val uuid: UUID,
    val eventType: ResolvableType,
    val eventTopic: ITopic<in Event>,
)

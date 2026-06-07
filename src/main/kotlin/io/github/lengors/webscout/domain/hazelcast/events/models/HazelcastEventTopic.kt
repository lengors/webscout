package io.github.lengors.webscout.domain.hazelcast.events.models

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.topic.ITopic
import io.github.lengors.webscout.domain.events.models.Event
import org.springframework.core.ResolvableType
import java.lang.reflect.Type
import java.util.Objects

@ConsistentCopyVisibility
data class HazelcastEventTopic private constructor(
    val type: Type,
    val topic: ITopic<Event>,
) {
    val resolvableType: ResolvableType by lazy {
        ResolvableType.forType(type)
    }

    constructor(type: Type, hazelcastInstance: HazelcastInstance) : this(
        type,
        hazelcastInstance.getTopic(type.typeName),
    )

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is HazelcastEventTopic) return false
        return Objects.equals(type, other.type)
    }

    override fun hashCode(): Int = Objects.hash(type)
}

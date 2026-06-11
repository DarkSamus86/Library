package org.darksamus86.library.notification.event;

import java.io.Serializable;

public record UserRegisteredEvent(
        Long userId,
        String email,
        String username
) implements Serializable {
}


package com.company.aneon.f10.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Người code: ANEON
 * Chức năng: Implementation tạm thời của NotificationPort.
 * Ghi nhận thông báo vào log thay vì gửi qua nhà cung cấp bên ngoài.
 */
@Component
public class LoggingNotificationAdapter implements NotificationPort {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingNotificationAdapter.class);

    @Override
    public void notifyEventApproval(
            UUID eventId,
            String decision,
            UUID recipientId) {

        log.info(
            "Event notification received: eventId={}, decision={}, recipientId={}",
            eventId,
            decision,
            recipientId
        );
    }
}

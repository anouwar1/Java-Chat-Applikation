package edu.hm.dako.auditLogServer;

import edu.hm.dako.common.AuditLogPDU;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread zum Abarbeiten der empfangenen AuditLog-PDUs
 *
 * @author mandl
 */
class AuditLogWriter implements Runnable {

    private static final Logger log = LogManager.getLogger(AuditLogWriter.class);
    private static int status;
    final int RUNNING = 1;
    final int SHUTTING_DOWN = 2;
    final int SHUTDOWN = 3;
    protected AuditLogManager auditLogManager;

    public AuditLogWriter(AuditLogManager auditLogManager) {
        this.auditLogManager = auditLogManager;
        status = RUNNING;
    }

    public void run() {

        Thread.currentThread().setName("AuditLogWriterThread");

        System.out.println("AuditLogWriterThread gestartet");
        log.info("AuditLogWriterThread gestartet");

        while (status != SHUTDOWN) {

            // Warten, bis eine AuditLog-PDU in der Queue ist und dann aus der Queue
            // lesen
            log.debug("AuditLogWriterThread wartet vor dequeue");
            AuditLogPDU pdu = auditLogManager.dequeue();

            if (pdu != null) {
                // Nachricht analysieren und verarbeiten

                switch (pdu.getPduType()) {
                    // Login eines Chat-Clients
                    case LOGIN_REQUEST -> auditLogManager.writeAuditLogLogin(pdu);
                    // Logout eines Chat-Clients
                    case LOGOUT_REQUEST -> auditLogManager.writeAuditLogLogin(pdu);
                    // Chat-Nachricht eines Chat-Clients
                    case CHAT_MESSAGE_REQUEST -> auditLogManager.writeAuditLogChatMessage(pdu);
                    default -> log.debug("Ankommende PDU nicht erkannt, sie wird verworfen");
                }
            }

            // Wenn die Queue leer ist und der Server herunterfaehrt, kann der Thread
            // beendet werden
            if ((status == SHUTTING_DOWN) && (auditLogManager.queueEmpty())) {
                status = SHUTDOWN;
            }
        }

        if (!auditLogManager.queueEmpty()) {
            log.info("Warteschlange noch nicht geleert");
            System.out.println("Warteschlange noch nicht geleert");
        }

        log.info("AuditLogWriterThread beendet");
        System.out.println("AuditLogWriterThread beendet");
    }

    public void shutdown() {

        System.out.println("AuditLogWriterThread: Shutdown veranlasst");
        status = SHUTTING_DOWN;
    }
}
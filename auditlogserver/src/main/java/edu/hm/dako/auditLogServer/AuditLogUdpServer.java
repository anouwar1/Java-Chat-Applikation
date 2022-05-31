package edu.hm.dako.auditLogServer;

import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.connection.udp.UdpServerConnection;
import edu.hm.dako.connection.udp.UdpServerSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

/**
 * Einfacher AuditLog Server fuer die Protokollierung von Chat-Nachrichten eines Chat-Servers.
 * Implementierung auf Basis von UDP.
 * Programm wird nie beendet.
 *
 * @author Peter Mandl
 */
public class AuditLogUdpServer {

    // UDP-Serverport fuer AuditLog-Service
    static final int AUDIT_LOG_SERVER_PORT = 40001;
    // Standard-Puffergroessen fuer Serverport in Bytes
    static final int DEFAULT_SENDBUFFER_SIZE = 30000;
    static final int DEFAULT_RECEIVEBUFFER_SIZE = 800000;
    // Name der AuditLog-Datei
    static final String auditLogFile = "ChatAuditLog.dat";
    private static final Logger log = LogManager.getLogger(AuditLogUdpServer.class);
    // Zaehler fuer ankommende AuditLog-PDUs
    protected long counter = 0;
    UdpServerSocket serverSocket = null;
    UdpServerConnection con = null;

    public static void main(String[] args) {
        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File("log4j2.auditLogUdpServer.xml");
        context.setConfigLocation(file.toURI());
        new AuditLogUdpServer().runServer();
    }

    /**
     * AuditLogServer fuehrt seine Arbeit aus
     */
    private void runServer() {

        System.out.println("AuditLog-UdpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);

        // Socket erzeugen
        try {
            this.createSocket();
            log.info("AuditLog-UdpServer gestartet, Port " + AUDIT_LOG_SERVER_PORT);
        } catch (Exception ignored) {
        }

        while (true) {
            // AuditLog-Datei erzeugen und oeffnen
            AuditLogManager auditLogManager = new AuditLogManager(auditLogFile);

            // Thread fuer die Warteschlangenabarbeitunng erzeugen
            AuditLogWriter logWriterRunnable = new AuditLogWriter(auditLogManager);
            Thread logWriterThread = new Thread(logWriterRunnable);
            logWriterThread.start();

            // Socket erzeugen
            try {
                // Auf Verbindungsaufbauwunsch warten
                this.waitForConnection();
                boolean connectedToChatServer = true;

                // Ersten Logsatz schreiben
                auditLogManager.auditLogBegin();

                // Ankommende AuditLog-Requests verarbeiten
                while (connectedToChatServer) {

                    // Nachricht lesen
                    AuditLogPDU pdu = this.receiveAuditPdu();

                    // Nachricht analysieren und verarbeiten
                    switch (pdu.getPduType()) {
                        case FINISH_AUDIT_REQUEST -> {
                            // LogWriterThread Shutdown einleiten und warten bis er sich beendet
                            System.out.println("Kommando zum Beenden des Audits empfangen");
                            logWriterRunnable.shutdown();
                            System.out.println(
                                    "AuditLogWriterThreadLogWriterThread Shutdown veranlasst, warten auf Threadende ...");
                            logWriterThread.join();
                            System.out.println("AuditLogWriterThreadLogWriterThread nach Shutdown beendet");
                            // Kommunikationsendpunkt schliessen
                            this.closeConnectionToChatServer();
                            // AuditLog abschliessen
                            System.out.println("Insgesamt empfangene AuditLog-Calls: "
                                    + auditLogManager.getNumberOfAuditCalls());
                            auditLogManager.auditLogEnd();
                            auditLogManager.close();
                            System.out.println("Verbindung zum Chat-Server geschlossen");
                            connectedToChatServer = false;
                        }
                        // Nachricht in die Queue einstellen
                        default -> auditLogManager.queue(pdu);
                    }
                }
            } catch (Exception e) {
                // AuditLog-Server soll wieder einen erneuten Verbindungsaufbauwunsch entgegennehmen,
                // das ist bei UDP nur ein Dummy
                this.closeConnectionToChatServer();
                // Ein wenig warten
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * Server-Socket erzeugen
     *
     * @throws Exception Fehler beim Erzeugen eines Socket
     */
    private void createSocket() throws Exception {
        try {
            serverSocket = new UdpServerSocket(AUDIT_LOG_SERVER_PORT, DEFAULT_SENDBUFFER_SIZE,
                    DEFAULT_RECEIVEBUFFER_SIZE);
        } catch (Exception e) {
            System.out.println("Exception bei der Erzeugung eines Sockets");
            throw new Exception();
        }
    }

    /**
     * Auf Verbindungsaufbauwunsch eines Clients warten
     *
     * @throws Exception Fehler beim Verbindungsaufbau
     */
    private void waitForConnection() throws Exception {
        try {
            con = (UdpServerConnection) serverSocket.accept();
            System.out.println(
                    "Kommunikationsendpunkt eingerichtet mit UDP-Port: " + AUDIT_LOG_SERVER_PORT);
        } catch (Exception e) {
            System.out.println("Exception");
            throw new Exception();
        }
    }

    /**
     * Nachricht vom Client empfangen und zuruecksenden
     *
     * @throws Exception Fehler beim Empfang
     */
    private AuditLogPDU receiveAuditPdu() throws Exception {
        try {
            AuditLogPDU receivedPdu = (AuditLogPDU) con.receive();
            counter++;
            System.out.println("Audit-Log counter: " + counter);
            return receivedPdu;
        } catch (Exception e) {
            System.out.println("Exception beim Empfang");
            throw new Exception();
        }
    }

    /**
     * Verbindung schliessen
     */
    private void closeConnectionToChatServer() {
        try {
            con.close();
            ;
        } catch (Exception e) {
            System.out.println("Exception beim close");
        }
    }
}
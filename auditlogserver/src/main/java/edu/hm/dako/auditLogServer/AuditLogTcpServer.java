package edu.hm.dako.auditLogServer;

import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.connection.tcp.TcpConnection;
import edu.hm.dako.connection.tcp.TcpServerSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

/**
 * Einfacher AuditLog Server fuer die Protokollierung von Chat-Nachrichten eines Chat-Servers.
 * Implementierung auf Basis von TCP.
 * Programm wird nie beendet.
 *
 * @author Peter Mandl
 */
public class AuditLogTcpServer {

    // Serverport fuer AuditLog-Service
    static final int AUDIT_LOG_SERVER_PORT = 40001;
    // Standard-Puffergroessen fuer Serverport in Bytes
    static final int DEFAULT_SENDBUFFER_SIZE = 30000;
    static final int DEFAULT_RECEIVEBUFFER_SIZE = 800000;
    // Name der AuditLog-Datei
    static final String auditLogFile = "ChatAuditLog.dat";
    private static final Logger log = LogManager.getLogger(AuditLogTcpServer.class);
    // Zaehler fuer ankommende AuditLog-PDUs
    protected long counter = 0;
    TcpServerSocket serverSocket = null;
    TcpConnection con = null;

    public static void main(String[] args) throws InterruptedException {
        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File("log4j2.auditLogTcpServer.xml");
        context.setConfigLocation(file.toURI());
        new AuditLogTcpServer().runServer();
    }

    /**
     * AuditLogServer fuehrt seine Arbeit aus
     */
    private void runServer() {
        System.out.println("AuditLog-TcpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);
        log.info("AuditLog-TcpServer gestartet, Port: " + AUDIT_LOG_SERVER_PORT);

        // Socket erzeugen
        try {
            createSocket();
            System.out.println("Neuer Listenport angemeldet");
        } catch (Exception ignored) {
        }

        while (true) {
            /* Server wartet eine auf eine Verbindunganfrage und haelt die Verbindung solange,
             * bis der Client (der ChatServer) sie beendet.
             * Danach wird sofort wieder auf einen Verbindungsaufbauwunsch gewartet.
             * Die Verbindung wird entgegebngenommen und ein neuer Abschnitt im AuditLog beginnt
             */

            // AuditLog-Datei erzeugen und oeffnen
            AuditLogManager auditLogManager = new AuditLogManager(auditLogFile);

            // Thread fuer die Warteschlangenbeararbeitung erzeugen
            AuditLogWriter logWriterRunnable = new AuditLogWriter(auditLogManager);
            Thread logWriterThread = new Thread(logWriterRunnable);
            logWriterThread.start();

            try {
                // Auf Verbindungsaufbauwunsch warten
                this.waitForConnection();

                // Verbindung steht!

                // Ersten Logsatz schreiben
                auditLogManager.auditLogBegin();

                // Ankommende AuditLog-Requests verarbeiten
                boolean connectedToChatServer = true;
                while (connectedToChatServer) {

                    // Nachricht lesen
                    AuditLogPDU pdu;
                    pdu = receiveAuditPdu();

                    switch (pdu.getPduType()) {
                        // LogWriterThread Shutdown einleiten und warten bis er sich beendet hat
                        case FINISH_AUDIT_REQUEST -> {
                            System.out.println("Kommando zum Beenden des Audits empfangen");
                            logWriterRunnable.shutdown();
                            System.out.println("AuditLogWriterThread Shutdown veranlasst, warten auf Threadende ...");
                            logWriterThread.join();
                            System.out.println("AuditLogWriterThread Shutdown zu Ende");
                            this.closeConnectionToChatServer();
                            System.out.println(
                                    "Insgesamt empfangene AuditLog-Calls: " + auditLogManager.getNumberOfAuditCalls());
                            // Kommunikationsendpunkt schliessen
                            auditLogManager.auditLogEnd();
                            // AuditLog schliessen
                            auditLogManager.close();
                            connectedToChatServer = false;
                            System.out.println("Verbindung zum Chat-Server geschlossen");
                            // Verbindungsaufbau soll wieder moeglich werden vorher ein wenig warten
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        // Nachricht in die Queue einstellen
                        default -> auditLogManager.queue(pdu);
                    }
                }
            } catch (Exception e) {
                // AuditLog-Server soll wieder einen Verbindungsaufbauwunsch entgegennehmen
                this.closeConnectionToChatServer();
                // Verbindungsaufbau soll wieder moeglich werden, vorher ein wenig warten
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
     * @throws Exception Fehler beim Anlegne eines Serversocket
     */
    private void createSocket() throws Exception {
        try {
            serverSocket = new TcpServerSocket(AUDIT_LOG_SERVER_PORT, DEFAULT_SENDBUFFER_SIZE,
                    DEFAULT_RECEIVEBUFFER_SIZE);
        } catch (Exception e) {
            System.out.println("Exception");
            throw new Exception();
        }
    }

    /**
     * Auf Verbindungsaufbauwunsch eines Clients warten
     *
     * @throws Exception Fehler bei der Verbindungsannahme
     */
    private void waitForConnection() throws Exception {

        System.out.println("Warten auf Verbindungsaufbau ...");
        try {
            con = (TcpConnection) serverSocket.accept();
            System.out.println(
                    "Kommunikationsendpunkt eingerichtet mit TCP-Port: " + AUDIT_LOG_SERVER_PORT);
        } catch (Exception e) {
            System.out.println("Exception");
            throw new Exception();
        }
    }

    /**
     * Nachricht vom Client empfangen
     *
     * @throws Exception Fehler beim Empfangen einer Nachricht
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
        } catch (Exception e) {
            System.out.println("Exception beim close");
        }
    }
}

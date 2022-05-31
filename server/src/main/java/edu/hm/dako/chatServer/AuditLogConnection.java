package edu.hm.dako.chatServer;

import edu.hm.dako.connection.udp.UdpClientConnection;
import edu.hm.dako.connection.udp.UdpClientConnectionFactory;
import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogPduType;
import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.tcp.TcpConnection;
import edu.hm.dako.connection.tcp.TcpConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Verwaltet eine logische Verbindung zum AuditLog-Server ueber UDP oder TCP
 * @author P. Mandl
 */
public class AuditLogConnection {

    public static final int AUDITLOG_CONNECTION_TYPE_TCP = 1;
    public static final int AUDITLOG_CONNECTION_TYPE_UDP = 2;
    // Puffergroessen
    static final int DEFAULT_SENDBUFFER_AUDITLOG_SIZE = 400000;
    static final int DEFAULT_RECEIVEBUFFER_AUDITLOG_SIZE = 40000;
    private static final Logger log = LogManager.getLogger(AuditLogConnection.class);
    // Verbindungstyp
    private final int connectionType; // UDP oder TCP
    protected UdpClientConnection udpConnectionToAuditLogServer = null;
    protected TcpConnection tcpConnectionToAuditLogServer = null;
    String auditLogServer;
    int auditLogPort;
    // Zaehlt abgehende AuditLog-Saetze
    private long counter = 0;

    /**
     * Konstruktor
     * @param connectionType Verbindungstyp (UDP oder TCP)
     * @param auditLogServer Host des AuditLog-Servers
     * @param auditLogPort Port fuer AuditLog-Server
     */
    public AuditLogConnection(int connectionType, String auditLogServer, int auditLogPort) {

        this.auditLogServer = auditLogServer;
        this.auditLogPort = auditLogPort;

        if ((connectionType != AUDITLOG_CONNECTION_TYPE_TCP) &&
                (connectionType != AUDITLOG_CONNECTION_TYPE_UDP)) {
            this.connectionType = AUDITLOG_CONNECTION_TYPE_TCP;
        } else {
            this.connectionType = connectionType;
        }
    }

    /**
     * Logische Verbindung zum AuditLog-Server aufbauen
     * @throws Exception - Fehler im Socket, Verbindung kann nicht aufgebaut werden
     */
    public void connectToAuditLogServer() throws Exception {
        try {

            if (connectionType == AUDITLOG_CONNECTION_TYPE_UDP) {
                // Verbindung zum AuditLog-Server und Verbindungsparameter
                UdpClientConnectionFactory udpFactory = new UdpClientConnectionFactory();
                udpConnectionToAuditLogServer = (UdpClientConnection) udpFactory.connectToServer(auditLogServer,
                        auditLogPort, 0, DEFAULT_SENDBUFFER_AUDITLOG_SIZE, DEFAULT_RECEIVEBUFFER_AUDITLOG_SIZE);

            } else {

                TcpConnectionFactory tcpFactory = new TcpConnectionFactory();
                tcpConnectionToAuditLogServer = (TcpConnection) tcpFactory.connectToServer(auditLogServer,
                        auditLogPort, 0, DEFAULT_SENDBUFFER_AUDITLOG_SIZE, DEFAULT_RECEIVEBUFFER_AUDITLOG_SIZE);

            }
            System.out.println("Verbindung zu AuditLog-Server steht");
        } catch (Exception e) {
            log.error("Exception bei Verbindungsaufbau zum Auditlog-Server");
            //ExceptionHandler.logExceptionAndTerminate(e);
            throw new Exception();
        }
    }

    /**
     * Senden eines AuditLog-Satzes zum AuditLog-Server
     * @param pdu Chat-PDU zum Entnehmen von Parametern für den AuditLog-Satz
     * @param type Typ der AuditLog-PDU, der zu senden ist
     * @throws Exception Fehler beim Senden zum AuditLog-Server
     */
    public synchronized void send(ChatPDU pdu, AuditLogPduType type) throws Exception {

        // AuditLog-Satz erzeugen
        AuditLogPDU auditLogPdu = createAuditLogPdu(pdu);
        auditLogPdu.setPduType(type);

        // AuditLog-Satz senden
        try {
            if (connectionType == AUDITLOG_CONNECTION_TYPE_UDP) {
                udpConnectionToAuditLogServer.send(auditLogPdu);
            } else {
                tcpConnectionToAuditLogServer.send(auditLogPdu);
            }
            counter++;
            System.out.println("AuditLog-Satz gesendet: " + counter);

        } catch (Exception e) {
            System.out.println("Fehler beim Senden eines AuditLog-Satzes");
            ExceptionHandler.logException(e);
            throw new Exception();
        }
    }

    /**
     * Schliessen der Verbindung zum AuditLog-Server
     * @throws Exception - Fehler beim Schliessen der Verbindung
     */
    public synchronized void close() throws Exception {
        try {

            AuditLogPDU closePdu = new AuditLogPDU();
            closePdu.setPduType(AuditLogPduType.FINISH_AUDIT_REQUEST);

            if (connectionType == AUDITLOG_CONNECTION_TYPE_UDP) {
                udpConnectionToAuditLogServer.send(closePdu);
                udpConnectionToAuditLogServer.close();
            } else {
                tcpConnectionToAuditLogServer.send(closePdu);
                tcpConnectionToAuditLogServer.close();
            }
            System.out.println("Verbindung zum AuditLog-Server beendet, Gesendete AuditLog-Saetze: " + counter);

        } catch (Exception e) {
            System.out.println("Fehler beim Schliessen der Verbindung zum AuditLog-Server");
            ExceptionHandler.logException(e);
            throw new Exception();
        }
    }

    /**
     * AuditLog-PDU erzeugen
     * @param chatPdu - Empfangene Chat-PDU, aus der Daten entnommen werden
     * @return Befuellte Auditlog-PDU
     */
    private AuditLogPDU createAuditLogPdu(ChatPDU chatPdu) {

        AuditLogPDU pdu = new AuditLogPDU();
        pdu.setPduType(AuditLogPduType.UNDEFINED);
        pdu.setAuditTime(System.currentTimeMillis());
        pdu.setUserName(chatPdu.getUserName());
        pdu.setClientThreadName(chatPdu.getClientThreadName());
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setMessage(chatPdu.getMessage());
        return (pdu);
    }
}
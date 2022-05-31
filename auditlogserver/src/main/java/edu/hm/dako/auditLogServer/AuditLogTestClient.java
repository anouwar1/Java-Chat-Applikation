package edu.hm.dako.auditLogServer;

import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.tcp.TcpConnection;
import edu.hm.dako.connection.tcp.TcpConnectionFactory;
import edu.hm.dako.connection.udp.UdpClientConnection;
import edu.hm.dako.connection.udp.UdpClientConnectionFactory;
import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogPduType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

/**
 * Testprogramm zum Test des AuditLog-Servers ueber TCP und UDP
 *
 * @author Peter Mandl
 */
public class AuditLogTestClient {

    public static final int AUDITLOG_CONNECTION_TYPE_TCP = 1;
    public static final int AUDITLOG_CONNECTION_TYPE_UDP = 2;
    static final String AUDITLOG_SERVER = "localhost";
    static final int AUDITLOG_SERVER_PORT = 40001;
    static final int DEFAULT_SENDBUFFER_AUDITLOG_SIZE = 40000;
    static final int DEFAULT_RECEIVEBUFFER_AUDITLOG_SIZE = 400000;

    static final int NR_OF_MSG = 10; // Anzahl zu sendender Nachrichten
    static final int MAX_LENGTH = 10; // Nachrichtenlaenge
    private final int connectionType; // Verbindungstyp
    Logger log = LogManager.getLogger(AuditLogTestClient.class);
    UdpClientConnectionFactory udpFactory = null;
    UdpClientConnection udpConnection = null;
    TcpConnectionFactory tcpFactory = null;
    TcpConnection tcpConnection = null;
    long counter = 0;

    public AuditLogTestClient(int connectionType) {
        this.connectionType = connectionType;
        if (connectionType == AUDITLOG_CONNECTION_TYPE_UDP) {
            udpFactory = new UdpClientConnectionFactory();
        } else {
            tcpFactory = new TcpConnectionFactory();
        }
        System.out.println("Client gestartet");
    }

    public static void main(String[] args) {


        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File("log4j2.auditLogTestClient.xml");
        context.setConfigLocation(file.toURI());


        // Hier TCP oder UPD als Transportprotokoll fuer die Kommunikation mit dem AuditLog-Server konfigurieren
        // AuditLogTestClient client = new
        // AuditLogTestClient(AUDITLOG_CONNECTION_TYPE_TCP);
        AuditLogTestClient client = new AuditLogTestClient(AUDITLOG_CONNECTION_TYPE_UDP);

        try {
            client.connectToAuditLogServer();

            AuditLogPDU requestPDU = createAuditLogLoginPDU();
            client.audit(requestPDU);

            for (int i = 0; i < NR_OF_MSG; i++) {
                requestPDU = createAuditLogChatMessagePDU();
                client.audit(requestPDU);
                System.out.println(i + 1 + ". Message gesendet, Laenge =  " + MAX_LENGTH);
            }

            requestPDU = createAuditLogLogoutPDU();
            client.audit(requestPDU);

            client.close();
        } catch (Exception e) {
            ExceptionHandler.logExceptionAndTerminate(e);
            System.exit(1);
        }
    }

    /**
     * AuditLog-PDU erzeugen
     *
     * @return PDU Erzeugte Nachricht die protokolliert werden soll
     */
    private static AuditLogPDU createAuditLogChatMessagePDU() {
        char[] charArray = new char[MAX_LENGTH];
        for (int j = 0; j < MAX_LENGTH; j++) {
            charArray[j] = 'A';
        }
        Thread.currentThread().setName("Test-AuditLog-Client");

        String message = String.valueOf(charArray);
        AuditLogPDU pdu = new AuditLogPDU();
        pdu.setAuditTime(System.currentTimeMillis());
        pdu.setUserName("Mandl");
        pdu.setPduType(AuditLogPduType.CHAT_MESSAGE_REQUEST);
        pdu.setClientThreadName(Thread.currentThread().getName());
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setMessage(message);

        System.out.println(pdu.toString());
        return (pdu);
    }

    /**
     * AuditLogLogin-PDU erzeugen
     *
     * @return PDU Erzeugte Nachricht die protokolliert werden soll
     */
    private static AuditLogPDU createAuditLogLoginPDU() {

        Thread.currentThread().setName("Test-AuditLog-Client");
        AuditLogPDU pdu = new AuditLogPDU();
        pdu.setAuditTime(System.currentTimeMillis());
        pdu.setUserName("Mandl");
        pdu.setPduType(AuditLogPduType.LOGIN_REQUEST);
        pdu.setClientThreadName(Thread.currentThread().getName());
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setMessage(null);

        System.out.println(pdu.toString());
        return (pdu);
    }

    /**
     * AuditLogLogout-PDU erzeugen
     *
     * @return PDU Nachricht, die protokolliert werden soll
     */
    private static AuditLogPDU createAuditLogLogoutPDU() {

        Thread.currentThread().setName("Test-AuditLog-Client");
        AuditLogPDU pdu = new AuditLogPDU();
        pdu.setAuditTime(System.currentTimeMillis());
        pdu.setUserName("Mandl");
        pdu.setPduType(AuditLogPduType.LOGOUT_REQUEST);
        pdu.setClientThreadName(Thread.currentThread().getName());
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setMessage(null);

        System.out.println(pdu.toString());
        return (pdu);
    }

    /**
     * Verbindung zum Server aufbauen
     *
     * @throws Exception Fehler beim Verbindungsaufbau
     */
    private void connectToAuditLogServer() throws Exception {
        try {
            if (connectionType == AUDITLOG_CONNECTION_TYPE_UDP) {
                udpConnection = (UdpClientConnection) udpFactory.connectToServer(AUDITLOG_SERVER,
                        AUDITLOG_SERVER_PORT, 0, DEFAULT_SENDBUFFER_AUDITLOG_SIZE,
                        DEFAULT_RECEIVEBUFFER_AUDITLOG_SIZE);
            } else {
                tcpConnection = (TcpConnection) tcpFactory.connectToServer(AUDITLOG_SERVER,
                        AUDITLOG_SERVER_PORT, 0, DEFAULT_SENDBUFFER_AUDITLOG_SIZE,
                        DEFAULT_RECEIVEBUFFER_AUDITLOG_SIZE);
            }
            System.out.println("Verbindung steht");
        } catch (Exception e) {
            System.out.println("Exception during connect");
            ExceptionHandler.logExceptionAndTerminate(e);
            throw new Exception();
        }
    }

    /**
     * AuditLog-Request senden
     *
     * @throws Exception Fehker beim Senden einer Nachricht
     */
    private void audit(AuditLogPDU requestPdu) throws Exception {

        // AuditLog-Satz senden
        try {
            if (connectionType == AUDITLOG_CONNECTION_TYPE_UDP) {
                udpConnection.send(requestPdu);
            } else {
                tcpConnection.send(requestPdu);
            }
            counter++;
            System.out.println("AuditLog-Satz gesendet: " + counter);

        } catch (Exception e) {
            System.out.println("Fehler beim Senden deines AuditLog-Satzes");
            ExceptionHandler.logException(e);
            throw new Exception();
        }
    }

    /**
     * Verbindung abbauen
     *
     * @throws Exception Fehler beim Schliessen der Verbindung zum Server
     */
    private void close() throws Exception {

        try {

            AuditLogPDU closePdu = new AuditLogPDU();
            closePdu.setPduType(AuditLogPduType.FINISH_AUDIT_REQUEST);

            if (connectionType == AUDITLOG_CONNECTION_TYPE_UDP) {
                udpConnection.send(closePdu);
                System.out.println("CLOSE gesendet: " + closePdu.getMessage());
                udpConnection.close();
            } else {
                tcpConnection.send(closePdu);
                System.out.println("CLOSE gesendet: " + closePdu.getMessage());
                tcpConnection.close();
            }
            System.out.println("Verbindung abgebaut");
        } catch (Exception e) {
            System.out.println("Exception beim close");
            ExceptionHandler.logException(e);
            throw new Exception();
        }
    }
}

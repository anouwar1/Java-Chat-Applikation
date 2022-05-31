package edu.hm.dako.auditLogServer;

import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.ExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Die Klasse verwaltet das AuditLog
 *
 * @author Peter Mandl
 */
public class AuditLogManager {

    private static final Logger log = LogManager.getLogger(AuditLogManager.class);
    // Warteschlange der noch nicht verarbeiteten AuditLog-Nachrichten
    private final LinkedBlockingQueue<AuditLogPDU> queue;
    // Filestream fuer AuditLog-Datei
    FileWriter fstream;
    // Gepufferte Ausgabe
    BufferedWriter out;
    String auditFileName;
    // Anzahl der Audit-Aufrufe
    private int numberOfAuditCalls;

    /**
     * Konstruktion
     *
     * @param fileName Name des AuditLog-Files
     */
    public AuditLogManager(String fileName) {

        queue = new LinkedBlockingQueue<>();

        this.numberOfAuditCalls = 0;
        this.auditFileName = fileName;

        // Name der AuditLog-Datei
        File file = new File(fileName);

        // Datei anlegen, wenn notwendig
        try {
            boolean exist = file.createNewFile();
            if (!exist) {
                log.debug("Datei " + fileName + " existierte bereits");
            } else {
                log.debug("Datei " + fileName + " erfolgreich angelegt");
            }

            // Datei zum Erweitern oeffnen
            fstream = new FileWriter(fileName, true);
            out = new BufferedWriter(fstream);

        } catch (IOException e) {
            log.error("Fehler beim Oeffnen oder Erzeugen der AuditLog-Datei " + fileName);
        }
    }

    /**
     * Anzahl der mitgezaehlten AuditLog-Calls ausgeben
     *
     * @return - Anzahl der Audit-Aufrufe
     */
    public int getNumberOfAuditCalls() {

        return numberOfAuditCalls;
    }

    /**
     * Schreiben eines Audit-Logsatzes in eine Datei im CSV-Dateiformat in folgender Form:
     * 01 Nummer des AuditLog-Satzes als String
     * 02 Logdatum/-zeit
     * 03 AuditLog-Type (Login-Request, Logout-Request, ChatMessage-Request)
     * 04 Chat-Clientname
     * 05 Chat-Client-Threadname
     * 06 Chat-Server-Worker-Threadname
     * 07 Chat-Message (nur bei einem ChatMessage-Request)
     *
     * @param pdu AuditLog-Nachricht
     */
    public void writeAuditLogChatMessage(AuditLogPDU pdu) {

        numberOfAuditCalls++;
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter();
        Date dateAndTime = new Date(pdu.getAuditTime());

        try {
            sb.append(
                    formatter.format("%7d | %s | %s | %s | %s | %s | %s%n", numberOfAuditCalls,
                            dateAndTime, pdu.getPduType().toString(), pdu.getUserName(),
                            pdu.getClientThreadName(), pdu.getServerThreadName(), pdu.getMessage()));

            out.append(sb);
            formatter.close();
            log.debug("AuditLog-Satz in Datei " + auditFileName + " geschrieben");
            out.flush();

        } catch (IOException e) {
            log.error("Fehler beim Schreiben des AuditLog-Satzes in Datei " + auditFileName);
        }
    }

    /**
     * Schreiben eines AuditLog-Satzes vom Typ Login in das AuditLog-File
     *
     * @param pdu AuditLog-Nachricht
     */
    public void writeAuditLogLogin(AuditLogPDU pdu) {

        numberOfAuditCalls++;

        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter();
        Date dateAndTime = new Date(pdu.getAuditTime());

        try {
            sb.append(formatter.format("%7d | %s | %s | %s | %s | %s%n", numberOfAuditCalls,
                    dateAndTime, pdu.getPduType().toString(), pdu.getUserName(),
                    pdu.getClientThreadName(), pdu.getServerThreadName()));

            out.append(sb);
            formatter.close();
            log.debug("AuditLog-Satz in Datei " + auditFileName + " geschrieben");
            out.flush();

        } catch (IOException e) {
            log.error("Fehler beim Schreiben des AuditLog-Satzes in Datei " + auditFileName);
        }
    }

    /**
     * Schreiben eines Anfangs-Satzes in das AuditLog-File
     */
    public void auditLogBegin() {

        numberOfAuditCalls = 0;

        // Datum und Uhrzeit ermitteln
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
        Date currentDateTime = new Date();
        log.debug(dateFormatter.format(currentDateTime));
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter();

        try {
            sb.append(formatter.format("%n%s%s%s%n", "*** Beginn AuditLog: ",
                    currentDateTime, " ***"));
            out.append(sb);
            formatter.close();
            log.debug("AuditLog-Satz in Datei " + auditFileName + " geschrieben");
            out.flush();

        } catch (IOException e) {
            log.error(
                    "Fehler beim Schreiben des AuditLog-Beginn-Satzes in Datei " + auditFileName);
        }
    }

    /**
     * Schreiben eines Ende-Satzes in das AuditLog-File
     */
    public void auditLogEnd() {

        // Datum und Uhrzeit ermitteln
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
        Date currentDateTime = new Date();

        log.debug(dateFormatter.format(currentDateTime));
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter();

        try {
            sb.append(formatter.format("%s%s%s%n", "*** Ende AuditLog: ",
                    currentDateTime, " ***"));
            out.append(sb);
            formatter.close();
            log.debug("AuditLog-Satz in Datei " + auditFileName + " geschrieben");
            out.flush();
        } catch (IOException e) {
            log.error(
                    "Fehler beim Schreiben des  AuditLog-Ende-Satzes in Datei " + auditFileName);
        }
    }

    /**
     * AuditLog schliessen
     */
    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            log.error("Fehler beim Schliessen des AuditLogs");
        }
    }

    /**
     * Einstellen einer PDU in die Queue
     *
     * @param pdu AuditLog-Nachricht
     */
    public void queue(AuditLogPDU pdu) {
        try {
            queue.put(pdu);
            log.debug("Thread: " + Thread.currentThread().getName() + ", Queue-Laenge: " + queue.size());
        } catch (InterruptedException e) {
            ExceptionHandler.logException(e);
        }
    }

    /**
     * Auslesen einer PDU aus der Queue
     *
     * @return AuditLog-Nachricht
     */
    public AuditLogPDU dequeue() {
        try {
            AuditLogPDU pdu;
            pdu = queue.poll(5000, TimeUnit.MILLISECONDS);
            log.debug("Thread: " + Thread.currentThread().getName() + ", Queue-Laenge: "
                    + queue.size());
            return pdu;
        } catch (InterruptedException e) {
            ExceptionHandler.logException(e);
            return null;
        }
    }

    /**
     * Pruefen, ob Queue leer ist
     *
     * @return true oder false
     */
    public boolean queueEmpty() {
        return (queue.isEmpty());
    }
}

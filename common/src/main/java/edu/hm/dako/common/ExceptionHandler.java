package edu.hm.dako.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Konstriert Exception Handler
 */
public class  ExceptionHandler {

    private static Logger log = LogManager.getLogger(ExceptionHandler.class);

    public static void logExceptionAndTerminate(Exception exception) {
        handleException(exception, true);
    }

    public static void logException(Exception exception) {
        handleException(exception, false);
    }

    /**
     * Behandelt Ausnahmen
     * @param exception Ausnahme
     * @param terminateVm Kennzeichen, ob Exception zur Beendigung des Programms fuehrt
     */
    private static void handleException(Exception exception, boolean terminateVm) {
        try {
            throw exception;
        } catch (java.io.EOFException e) {
            log.error("End of File bei Verbindung: " + e);
        } catch (SocketException e) {
            log.error("Exception bei der Socket-Nutzung: " + e);
        } catch (UnknownHostException e) {
            log.error("Exception bei Adressebelegung: " + e);
        } catch (IOException e) {
            log.debug("Senden oder Empfangen von Nachrichten nicht moeglich: " + e);
        } catch (InterruptedException e) {
            log.error("Sleep unterbrochen");
        } catch (ClassNotFoundException e) {
            log.error("Empfangene Objektklasse nicht bekannt:" + e);
        } catch (Exception e) {
            // exception.printStackTrace();
            log.error("Schwerwiegender Fehler");
        }
        if (terminateVm) {
            System.exit(1);
        }
    }
}

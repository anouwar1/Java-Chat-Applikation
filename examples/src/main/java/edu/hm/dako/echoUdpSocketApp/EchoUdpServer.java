package edu.hm.dako.echoUdpSocketApp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Echo-Server auf Basis von UDP Datagram-Sockets
 * @author P.Mandl
 * @version 2.0
 */
public class EchoUdpServer {

    protected DatagramSocket socket;

    /**
     * Konstruktor
     * @param port - Port des Serverdienstes
     * @throws IOException - Fehler bei der Socket-Erzeugung
     */
    public EchoUdpServer(int port) throws IOException {
        socket = new DatagramSocket(port);
    }

    /**
     * Hauptprogramm
     * @param args - nicht verwendet
     */
    public static void main(String[] args) {
        final int serverPort = 56000;
        try {
            EchoUdpServer echo = new EchoUdpServer(serverPort);
            System.out.println("UDP Echo Server started");
            echo.execute();
        } catch (IOException e) {
            System.out.println("Port " + serverPort + " is already in use");
            System.out.println("UDP Echo Server not started");
        }
    }

    /**
     * Warten auf Nachricht und Nachricht zuruecksenden, bis ein Fehler eintritt
     */
    public void execute() {
        boolean running = true;
        while (running) {
            try {
                System.out.println("Waiting for messages ...");
                DatagramPacket packet = receivePacket();
                sendEcho(packet.getAddress(), packet.getPort(),
                        packet.getData(), packet.getLength());
            } catch (IOException e) {
                running = false;
                closeSocket();
            }
        }
    }

    /**
     * Datagram empfangen
     * @return Empfangenes Datagram
     * @throws IOException Fehler beim Empfang
     */
    protected DatagramPacket receivePacket() throws IOException {
        byte[] buffer = new byte[65535];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            System.out.println("Exception in receive");
            throw e;
        }

        String receivedMessage = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Message received: " + packet.getLength() +
                " Bytes >" + receivedMessage + "<");
        return packet;
    }

    /**
     * Zuruecksenden der empfangenen Nachricht
     * @param address Zieladresse des Echo-Clients
     * @param port Port des Echo-Clients
     * @param data Nachricht
     * @param length Nachrichtnelaenge
     * @throws IOException Fehler beim Senden
     */
    protected void sendEcho(InetAddress address, int port, byte[] data,
                            int length) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, length, address, port);

        try {
            socket.send(packet);
            String sendMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Response sent:    " + length + " Bytes >" + sendMessage + "<");
        } catch (IOException e) {
            System.out.println("Exception in send");
            throw e;
        }

    }

    /**
     * Schliessen des Datagram-Sockets
     */
    protected void closeSocket() {
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("Error in close");
        }
    }
}
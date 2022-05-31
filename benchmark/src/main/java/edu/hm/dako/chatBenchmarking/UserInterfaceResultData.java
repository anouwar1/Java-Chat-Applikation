package edu.hm.dako.chatBenchmarking;

public class UserInterfaceResultData {

    // Arithmetisches Mittel
    double mean;
    // Standardabweichung
    double standardDeviation;
    // Anzahl gesendeter Requests
    private long numberOfSentRequests;
    // Anzahl empfangener Responses
    private long numberOfResponses;
    // Anzahl verlorener (nicht empfangener) Responses
    private long numberOfLostResponses;
    // Anzahl an Wiederholungen beim Senden
    private long numberOfRetries;
    // Testende als Datum/Uhrzeit-String
    private String endTime;
    // Testdauer in Sekunden
    private long elapsedTime;
    // Anzahl gesendeter Event-Nachrichten im Server
    private long numberOfSentEventMessages;
    // Anzahl empfangener Event-Bestaetigungen im Server
    private long numberOfReceivedConfirmEvents;
    // Anzahl verlorener Bestaetigungen im Server
    private long numberOfLostConfirmEvents;
    // Anzahl von erneuten Sendungen von Event-Nachrichten im Server
    private long numberOfRetriedEvents;
    // Mittlere Serverbearbeitungszeit in ms
    private double avgServerTime;
    // Maximale Heap-Belegung waehrend des Testlaufs in MiB
    private long maxHeapSize;
    // Maximale CPU-Auslastung waehrend des Testlaufs in Prozent
    private float maxCpuUsage;
    // Minimum (Minimale RTT ueber alle Requests)
    private double minimum;
    // Maximum (Maximale RTT ueber alle Requests)
    private double maximum;
    // 10 % Percentile, alle RTT-Werte, die zu den kleinsten 10 % gehoeren
    private double percentile10;
    // 10 % Percentile = 25%-Quartil, alle RTT-Werte, die zu den kleinsten 25 %
    // gehoeren
    private double percentile25;
    // Median = 50%-Quartil
    private double percentile50;
    // 75 % percentile
    private double percentile75;
    // 90 % Percentile
    private double percentile90;
    // Stannweite (zwischen Minimum und Maximum)
    private double range;
    // Interquartilsabstand zwischen 25%- und 75%-Percentile
    private double interquartilRange;

    public long getNumberOfSentRequests() {
        return numberOfSentRequests;
    }

    public void setNumberOfSentRequests(long numberOfSentRequests) {
        this.numberOfSentRequests = numberOfSentRequests;
    }

    public long getNumberOfResponses() {
        return numberOfResponses;
    }

    public void setNumberOfResponses(long numberOfResponses) {
        this.numberOfResponses = numberOfResponses;
    }

    public long getNumberOfLostResponses() {
        return numberOfLostResponses;
    }

    public void setNumberOfLostResponses(long numberOfLostResponses) {
        this.numberOfLostResponses = numberOfLostResponses;
    }

    public long getNumberOfRetries() {
        return numberOfRetries;
    }

    public void setNumberOfRetries(long numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getNumberOfSentEventMessages() {
        return numberOfSentEventMessages;
    }

    public void setNumberOfSentEventMessages(long numberOfSentEventMessages) {
        this.numberOfSentEventMessages = numberOfSentEventMessages;
    }

    public long getNumberOfReceivedConfirmEvents() {
        return numberOfReceivedConfirmEvents;
    }

    public void setNumberOfReceivedConfirmEvents(
            long numberOfReceivedConfirmEvents) {
        this.numberOfReceivedConfirmEvents = numberOfReceivedConfirmEvents;
    }

    public long getNumberOfLostConfirmEvents() {
        return numberOfLostConfirmEvents;
    }

    public void setNumberOfLostConfirmEvents(long numberOfLostConfirmEvents) {
        this.numberOfLostConfirmEvents = numberOfLostConfirmEvents;
    }

    public long getNumberOfRetriedEvents() {
        return numberOfRetriedEvents;
    }

    public void setNumberOfRetriedEvents(long numberOfRetriedEvents) {
        this.numberOfRetriedEvents = numberOfRetriedEvents;
    }

    public double getAvgServerTime() {
        return avgServerTime;
    }

    public void setAvgServerTime(double avgServerTime) {
        this.avgServerTime = avgServerTime;
    }

    public long getMaxHeapSize() {
        return maxHeapSize;
    }

    public void setMaxHeapSize(long maxHeapSize) {
        this.maxHeapSize = maxHeapSize;
    }

    public double getMaxCpuUsage() {
        return maxCpuUsage;
    }

    public void setMaxCpuUsage(float maxCpuUsage) {
        this.maxCpuUsage = maxCpuUsage;
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    public double getPercentile10() {
        return percentile10;
    }

    public void setPercentile10(double percentile10) {
        this.percentile10 = percentile10;
    }

    public double getPercentile25() {
        return percentile25;
    }

    public void setPercentile25(double percentile25) {
        this.percentile25 = percentile25;
    }

    public double getPercentile50() {
        return percentile50;
    }

    public void setPercentile50(double percentile50) {
        this.percentile50 = percentile50;
    }

    public double getPercentile75() {
        return percentile75;
    }

    public void setPercentile75(double percentile75) {
        this.percentile75 = percentile75;
    }

    public double getPercentile90() {
        return percentile90;
    }

    public void setPercentile90(double percentile90) {
        this.percentile90 = percentile90;
    }

    public double getInterquartilRange() {
        return interquartilRange;
    }

    public void setInterquartilRange(double interquartilRange) {
        this.interquartilRange = interquartilRange;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }
}
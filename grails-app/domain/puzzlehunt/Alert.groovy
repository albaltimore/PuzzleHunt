package puzzlehunt

class Alert {
    Player player
    boolean isAcknowledged
    String title
    String message
    long targetTime
    int leadTime
    String batchId
    Hunt hunt

    static constraints = {
    }
}

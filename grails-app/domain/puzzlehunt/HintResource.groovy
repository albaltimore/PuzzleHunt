package puzzlehunt

class HintResource {

    Resource resource
    long seconds
    String description

    static constraints = {
        description maxSize: 1200, nullable: true
        resource nullable: true
    }
}

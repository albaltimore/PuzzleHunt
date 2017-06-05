package puzzlehunt

class RequiredPuzzle {
    List<Coordinate> coordinates
    Puzzle puzzle
    String color

    static hasMany = [coordinates: Coordinate]
    static constraints = {
        color nullable: true
    }
}

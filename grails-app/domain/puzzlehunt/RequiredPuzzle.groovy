package puzzlehunt

import groovy.transform.ToString

@ToString(includeNames = true)
class RequiredPuzzle {
    List<Coordinate> coordinates
    Puzzle puzzle
    String color

    Resource pathResource
    Integer pathResourceXcor, pathResourceYcor

    static hasMany = [coordinates: Coordinate]
    static constraints = {
        color nullable: true

        pathResource nullable: true
        pathResourceXcor nullable: true
        pathResourceYcor nullable: true
    }
}

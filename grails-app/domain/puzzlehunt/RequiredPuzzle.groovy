package puzzlehunt

import groovy.transform.ToString

@ToString(includeNames = true)
class RequiredPuzzle {
    List<Coordinate> coordinates
    Puzzle puzzle
    String color

    List<PathResource> pathResource

    static hasMany = [coordinates: Coordinate]
	
    static constraints = {
        color nullable: true

        pathResource nullable: true
    }
}

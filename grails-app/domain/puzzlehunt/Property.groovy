package puzzlehunt 

class Property {
    String name
    String value

    static constraints = {
        value nullable: true
        name unique: true
    }

}
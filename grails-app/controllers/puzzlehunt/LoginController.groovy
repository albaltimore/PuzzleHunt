package puzzlehunt

import grails.converters.JSON

class LoginController {

    int order = 10

    def index() {}

    def login() {
        def player = Player.findByNameAndPassword(params.username, params.password)
        if (player) {
            session.playerId = player.id
            session.playerName = player.name
            
            redirect controller: "player"
            
        } else {
            flash.message = "Invalid Credentials"
            forward action: "index"
        }
    }
}

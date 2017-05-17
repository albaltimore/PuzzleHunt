package puzzlehunt

import grails.converters.JSON

class LoginController {

    def index() { }

    def login() {
        def player = Player.findByNameAndPassword(params.username, params.password)
        if (player) {
            session.playerId = player.id
            if (player.role == "HINTER") {
                redirect controller: "hint"
            }
            else {
                redirect controller: "player"
            }
        } else {
            flash.message = "Invalid Credentials"
            redirect action: "index"
        }
    }
}

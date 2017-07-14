package puzzlehunt

import grails.converters.JSON

class LoginController {

    int order = 10

    def propertiesService

    def index() { }

    def login() {
        def player = Player.findByNameAndPassword(params.username, params.password)
        if (player) {
            session.playerId = player.id
            session.playerName = player.name
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

    def getFavicon() {
        def bootstrapPath = grailsApplication.config.getProperty("puzzlehunt.resourcePath")
        def rs = Favicon.first()?.resource
        println "favicon ${rs}"
        if (rs) {
            println "favicon data ${rs.filename}"
            def f = new File("${bootstrapPath}/${rs.filename}")
            def extension = rs.filename.substring(rs.filename.lastIndexOf(".") + 1).toLowerCase()

            render file:f, contentType: "image/png"
        } else {
            render status: 404
        }
    }
}

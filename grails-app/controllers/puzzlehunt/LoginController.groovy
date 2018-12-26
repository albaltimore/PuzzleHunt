package puzzlehunt

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.apache.ApacheHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import grails.converters.JSON

class LoginController {

    int order = 10

    def index() {}

    def login() {
        def player = Player.findByNameAndPasswordAndSourceIsNull(params.username, params.password)
        if (player) {
            session.playerId = player.id
            session.playerName = player.name

            redirect controller: "player"

        } else {
            flash.message = "Invalid Credentials"
            forward action: "index"
        }
    }

    def googleAuth() {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new JacksonFactory())
            .setAudience(['98624763155-ig63kk95v6jfs3803m7o53qpgbaqb1nm.apps.googleusercontent.com']).build()

        GoogleIdToken idToken = verifier.verify(params.idtoken)

        if (!idToken) {
            flash.message = "Invalid Credentials"
            forward action: 'index'
        }
        GoogleIdToken.Payload payload = idToken.getPayload()

        if (!payload.email && !payload.emailVerified) {
            flash.message = "Invalid Credentials"
            forward action: 'index'
        }

        println payload
        println payload.email

        Player player = Player.findBySourceAndEmail('google', payload.email)
        if (!player) {
            player = new Player(name: payload.email, email: payload.email, source: 'google')
            println player

            if(!player.save(flush: true)) {

                player.errors.allErrors.each {
                    println it
                }

                render 'failed to login'
                return
                flash.message = 'Could not register user'
                forward action: 'index'
                return
            }
        }
        session.playerId = player.id
        session.playerName = player.name
        redirect controller: 'player'
    }
}

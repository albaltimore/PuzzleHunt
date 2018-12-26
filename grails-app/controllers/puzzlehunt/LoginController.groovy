package puzzlehunt

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.apache.ApacheHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import groovy.json.JsonSlurper

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

            if (!player.save(flush: true)) {

                player.errors.allErrors.each {
                    println it
                }

                flash.message = 'Could not register user'
                forward action: 'index'
                return
            }
        }
        session.playerId = player.id
        session.playerName = player.name
        redirect controller: 'player'
    }


    def facebookAuth() {
        def payload
        try {
            def urlParams = [fields: 'name,email', access_token: params.idtoken]
            payload = new JsonSlurper().parse(new URL("https://graph.facebook.com/v3.2/me?${urlParams.collect { k, v -> "$k=${URLEncoder.encode(v, 'UTF-8')}" } join '&'}"))
        } catch (Exception ex) {
            flash.message = 'Could not register user'
            forward action: 'index'
            return
        }

        Player player = Player.findBySourceAndEmail('facebook', payload.email)
        if (!player) {
            player = new Player(name: payload.email, email: payload.email, source: 'facebook')
            println player

            if (!player.save(flush: true)) {

                player.errors.allErrors.each {
                    println it
                }

                flash.message = 'Could not register user'
                forward action: 'index'
                return
            }
        }
        session.playerId = player.id
        session.playerName = player.name
        redirect controller: 'player'
    }

    def microsoftAuth() {
        def payload
        HttpURLConnection c

        try {
            def url = new URL('https://graph.microsoft.com/v1.0/me')
            c = (HttpURLConnection) url.openConnection()
            c.setRequestProperty 'Authorization', "Bearer $params.idtoken"
            c.setRequestProperty 'Accept', 'application/json'
            payload = new JsonSlurper().parse(c.inputStream)
        } catch(Exception ex) {
            ex.printStackTrace()
            if (c && c.errorStream) System.err.println c.errorStream.text

            flash.message = 'Could not register user'
            forward action: 'index'
            return
        }

        Player player = Player.findBySourceAndEmail('microsoft', payload.userPrincipalName)
        if (!player) {
            player = new Player(name: payload.userPrincipalName, email: payload.userPrincipalName, source: 'microsoft')
            println player

            if (!player.save(flush: true)) {

                player.errors.allErrors.each {
                    println it
                }

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

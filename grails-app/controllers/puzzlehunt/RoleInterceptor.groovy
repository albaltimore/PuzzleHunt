package puzzlehunt

class RoleInterceptor {

    private static final CONTROLLERS = ['player', 'hint', 'status', 'admin']
    private static
    final ROLES = [HINTER: ['hint', 'status'], PRINTER: ['status'], ADMIN: ['admin', 'hint', 'status'], PLAYER: ['player']]

    RoleInterceptor() {
        CONTROLLERS.each {
            match controller: it
        }
    }

    int order = 100

    boolean before() {
        def player = Player.findById(session.playerId)
        def role = player.role ?: 'PLAYER'

        if (!(role in ROLES)) {
            flash.message = 'I\'m not sure who you are...'
            redirect controller: 'login'
            return false
        }

        if (role == 'PLAYER' && ((Property.findByName('START')?.value ?: 0) as Long) > System.currentTimeMillis()) {
            flash.message = "The hunt hasn't started yet. It begins at ${new Date((Property.findByName('START')?.value ?: 0) as Long)}."
            forward controller: 'login'
            return false
        }


        if (!(controllerName in ROLES[role])) {
            redirect controller: ROLES[role][0]
            return false
        }

        if (!player.firstLoginTime) {
            player.firstLoginTime = System.currentTimeMillis()
            player.save(flush: true)
        }

        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}

package puzzlehunt

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/"(redirect:[controller: "player", permanent:"true"])
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}

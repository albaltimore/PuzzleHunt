package puzzlehunt

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/"(redirect:[controller: "login", action: 'homepage', permanent:"true"])
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}

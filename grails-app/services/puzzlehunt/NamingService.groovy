package puzzlehunt

import grails.gorm.transactions.Transactional

import java.security.SecureRandom

@Transactional
class NamingService {
    private def adj = [] as Set, noun = [] as Set

    public NamingService() {
        this.getClass().getClassLoader().getResourceAsStream('adj.txt').eachLine {adj.add it}
        this.getClass().getClassLoader().getResourceAsStream('noun.txt').eachLine {noun.add it}
    }

    def getRandomName() {
        def rItem = {it[Math.random() * it.size() as int]}
        "${rItem(adj).capitalize()} ${rItem(noun).capitalize()}"
    }


}

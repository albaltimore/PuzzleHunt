environments {
    production {
        dataSource {
            println System.env.JDBC_DATABASE_URL
            url = System.env.JDBC_DATABASE_URL
            username = System.env.JDBC_DATABASE_USERNAME
            password = System.env.JDBC_DATABASE_PASSWORD
        }
    }
}
environments {
    production {
        dataSource {
            url = System.env.JDBC_DATABASE_URL
            username = System.env.JDBC_DATABASE_USERNAME
            password = System.env.JDBC_DATABASE_PASSWORD
        }
    }
}
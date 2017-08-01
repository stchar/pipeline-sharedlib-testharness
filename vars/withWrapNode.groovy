def call(String label, Closure body = null) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
        echo "hello test message: ${config.message}"
    }
}

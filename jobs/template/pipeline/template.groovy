@Library('jenkins-commons') _

withWrapNode('some_stage') {
  echo "Hello World!"
}

return this

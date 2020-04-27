def call(_stages,_name, _node, _timeout, Closure _closure) {
  stage(_name) {
    if(_stages.contains(_name)) {
      gitlabCommitStatus(name: _name) {
        timeout(time: _timeout, unit: 'MINUTES') {
          node(_node) {
            _closure()
          }
        }
      }
    } else {
      updateGitlabCommitStatus name: _name, state: 'canceled'
    }
  }
}

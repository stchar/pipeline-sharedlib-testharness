@Library('jenkins-commons')
import org.hcm.libjenkins.Gitlab

gitlab_lib = new Gitlab(this)

node() {
  echo "Hello World!"
}

return this

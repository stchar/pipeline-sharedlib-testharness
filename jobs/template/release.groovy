@Library('jenkins-commons')
import org.hcm.libjenkins.Release

release_lib = new Release(this)

node() {
  echo "Hello World!"
}

return this

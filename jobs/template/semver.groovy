jc = library('jenkins-commons')
semver_lib = jc.org.hcm.libjenkins.Semver.new()

node() {
  echo "Hello World!"
}

return this

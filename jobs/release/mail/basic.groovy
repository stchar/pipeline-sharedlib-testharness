package release.mail

@Library('jenkins-commons')
import org.hcm.libjenkins.Release
def release_lib = new Release(this)

def component = [:]
component.name          = component.name          ? component.name          : 'component'
component.namespace     = component.namespace     ? component.namespace     : 'namespace'
component.project       = component.project       ? component.project       : 'namespace/component'
component.src_ref       = component.src_ref       ? component.src_ref       : 'master'
component.target_ref    = component.target_ref    ? component.target_ref    : 'master'
component.mailto        = component.mailto        ? component.mailto        : 'jhon.doe@example.com'

node() {
  echo "Preparing email context"
  echo "Echo Run release mail step"
  echo "component.mailto= ${component.mailto}"
  echo "component.body= ${component.body}"
  release_lib.release_mail(component)

  echo "Mail Sent"
}

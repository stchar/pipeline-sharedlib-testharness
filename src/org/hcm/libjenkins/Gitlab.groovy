package org.hcm.libjenkins;

@Grab('com.github.zafarkhaja:java-semver:0.9.0')

import com.github.zafarkhaja.semver.Version;

// Class declaration
class Gitlab implements Serializable {
  def script;

  Gitlab() {

  }


  Gitlab(script) {
    this.script=script
  }

  // Helper functions
  def is_upstream(name) {
    return ( !(name.equals(''))
      && (name =~ /^master$/ || name =~ /^rel-.*$/) )
  }

  /**
  * Build os-specific file path
  * @param pathArray {ArrayList} List of filesystem nodes
  * @param escape_flag {boolean} escape path delimeter symbol (windows only)
  * @return {String} file path
  * @example
  * <code>
  *   // on unix
  *   buidFilePath(["/usr","local","bin"])
  *   // returns /usr/local/bin
  * </code>
  *
  * <code>
  *   // on windows
  *   buidFilePath(["C:","somefolder","somefile"])
  *   // returns C:\somefolder\somefile
  *   buidFilePath(["C:","somefolder","somefile"],escape_flag=true)
  *   // returns C:\\somefolder\\somefile
  * </code>
  */
  def buidFilePath(pathArray, escape_flag=false) {
    if (!script.isUnix()) {
       if(escape_flag) {return pathArray.join('\\\\')}
       return pathArray.join('\\')
    }
    return pathArray.join('/')
  }

  /**
  * Build os-specific file path
  * @example
  * <code>
  *   // on unix
  *   buidPath([buidFilePath(["/bin"]),buidFilePath(["/usr","bin"]),buidFilePath(["/usr","local","bin"])])
  *   // returns/bin:/usr/bin:/usr/local/bin
  * </code>
  *
  * <code>
  *   // on windows
  *   buidPath([ buidFilePath(["C:","somefolder","somefile"]),buidFilePath(["D:","somefolder","somefile"]) ])
  *   // returns C:\somefolder\somefile;D:\somefolder\somefile
  * </code>
  */
  /**
  * Build os-specific content of Path Variable
  * @example:
  * buidPath([buidFilePath(["/bin"]),buidFilePath(["/usr","bin"]),buidFilePath(["/usr","local","bin"])])
  * /bin:/usr/bin:/usr/local/bin
  */
  def buidPath(pathArray) {
    if (!script.isUnix()) {
       return pathArray.join(';')
    }
    return pathArray.join(':')
  }

  /**
  * Incremet semver value
  */
  def increment_version(semver) {
    Version v = Version.valueOf(semver);
    return v.incrementPatchVersion().toString();
  }

  /**
  * Generate semver value. If some arguments are missed it uses git to retrive the values
  * @param tag {String} Nearest tag
  * @param patch_count {Integer} Number of commits beyond the tag
  * @param sha1_abr {String} Commit sha1 abbraviation
  * @return {String} semver value
  */
  def extract_semver_git(tag=null, patch_count=null ,sha1_abr=null) {
    if(!tag) {
      try {
        script.sh "git describe --abbrev=0 HEAD 2>&1 |grep 'fatal: No names found, cannot describe anything.'"
        tag = "0.0.0"
      } catch (err) {
      }
    }

    if(!tag) {
      script.sh "git describe --abbrev=0 HEAD > temp"
      tag = script.readFile(file:'temp' ).trim()
      script.sh("rm temp")
    }

    if (!sha1_abr) {
      script.sh "git log -1 --format=%h HEAD > temp"
      sha1_abr = script.readFile(file:'temp' ).trim()
      script.sh("rm temp")
    }

    if (!patch_count) {
      if (tag == "0.0.0") {
        script.sh "git rev-list HEAD |wc -l > temp"
      } else {
        script.sh "git rev-list ${tag}..HEAD |wc -l > temp"
      }
      patch_count = new Integer(script.readFile(file:'temp' ).trim())
      script.sh("rm temp")
    }

    script.echo("$tag $patch_count $sha1_abr")

    Version.Builder builder = new Version.Builder(tag)
    if( patch_count && sha1_abr ){
      builder.setBuildMetadata("build.${patch_count}.ref${sha1_abr}")
    }
    return builder.build().toString();
  }

  /**
  * Generate assemblyFileVersion version for a C# component.
  * The format  of this filed is regulated by windows
  * maj    min    patch  build_version
  * ushort.ushort.ushort.ushort
  * ushort is in a range if [0..65535] of integer values
  * if one of smever value is out of this range error is emmited
  * @param tag {String} semver value
  * @return {String} semver value
  */
  def semver_to_assembly_file_version(semver) {
    Version v = Version.valueOf(semver)
    int major = v.getMajorVersion() // 1
    if ( major < 0 || major > 65535 ) {
        throw new IllegalArgumentException("$semver major value: $major is out of range")
    }

    int minor = v.getMinorVersion() // 0
    if ( minor < 0 || minor > 65535 ) {
        throw new IllegalArgumentException("$semver minor value: $minor is out of range")
    }

    int patch = v.getPatchVersion() // 0
    if ( patch < 0 || patch > 65535 ) {
        throw new IllegalArgumentException("$semver patch value: $patch is out of range")
    }

    int pre_release_count = 0
    String preRelease  = v.getPreReleaseVersion().trim() // "rc.1"

    if(preRelease) {
      preRelease = preRelease.replaceAll(/rc.(\d+)/) { fullMatch ->
        "${fullMatch[1]}"
      }
      pre_release_count = preRelease ? new Integer(preRelease) : 0
    }
    int patch_count = 0
    String buildMeta = v.getBuildMetadata()     // "build.1"
    if(buildMeta) {
      buildMeta = buildMeta.replaceAll(/build.(\d+)(.-ref.*)?/) { fullMatch ->
        "${fullMatch[1]}"
      }
      patch_count = buildMeta ? new Integer(buildMeta) : 0
    }

    int build_version = pre_release_count*1000+patch_count
    if ( build_version < 0 || build_version > 65535 ) {
      throw new IllegalArgumentException("$semver build_version value: $build_version is out of range")
    }

    return "${major}.${minor}.${patch}.${build_version}"
  }


}

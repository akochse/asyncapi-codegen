# Gradle Help

> Code Generator for java code from asyncApi specs


## Build

This project uses the [Gradle Build Tool](https://gradle.org/) to build and
release artifacts.

The project has the Gradle Wrapper installed in order to make sure the project
is always built with the same version of Gradle. So as a general rule you run
any build task in the following format:

```sh
$ ./gradlew <task>
```

If needed you can even run multiple tasks in the same execution:

```sh
$ ./gradlew <task-1> <task-2> <...>
```

Alternatively you can install a wrapper around the `gradle`/`gradlew` binaries
like [`gdub`](http://www.gdub.rocks/), which always tries the local Gradle Wrapper
first before resorting to the globally installed Gradle binary. In this case you
would run any build task in the format:

```sh
$ ./gradlew <task>
```

## Release-Handling

The gradle script has a simple versioning built in with the followng tasks: 

* createRelease - Performs first stage of release - creates tag.
* markNextVersion - Creates next version marker tag and pushes it to remote.
* pushRelease - Performs second stage of release - pushes tag to remote.
* release - Performs release - creates tag and pushes it to remote.
* verifyRelease - Verifies code is ready for release.
* currentVersion - Prints current project version extracted from SCM

If any change in the code got made an automatic SNAPSHOT version will build next. After everything is checked in an pushed one can with create next release with:

```
./gradlew createRelese
```
with the next version tag. To push the new tag onto the remote repository use:

```
git push --tags 
```

If you want to increase the major or minor version use the folloing command(Format: V<MajorVersion>.<MinorVersion>.<Revision> z.B. V1.0.0) with: 

```
./gradlew markNextVersion -Prelease.version=1.1.0
```

[Weitere Dokumentation dazu](https://axion-release-plugin.readthedocs.io/en/latest/)
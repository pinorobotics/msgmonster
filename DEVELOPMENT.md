# Build

Building  module locally and making changes to it (this is optional and not intended for users).

## With Gradle

``` bash
gradle clean build
```

## With Eclipse

- Build Eclipse projects:

``` bash
gradle eclipse
```

- Import them into Eclipse

# Release steps

- Close version in gradle.properties
- Run `gradle clean build` (separately in each environment: ROS1, ROS2)
- Open next SNAPSHOT version
- Update [CHANGELOG.md](msgmonster/release/CHANGELOG.md) with new release (for changelog generation use `git log --format=%s`)
- Commit changes
- Push
- Upload documentation to website

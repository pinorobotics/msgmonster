**msgmonster** - application for generating Message Java classes for [jrosclient](https://github.com/lambdaprime/jrosclient) from ROS msg files.

# Features

- generate Java classes for single msg file or all msg files within a package
- all comments in msg files are preserved and copied to Java classes

# Download

Download [release versions](https://github.com/pinorobotics/msgmonster/releases)

Download [latest prerelease version](https://github.com/pinorobotics/msgmonster/tree/main/msgmonster/release)

# Requirements

- Java 11
- rosmsg command

# Usage

```bash
msgmonster <ROS_VERSION> <JAVA_PACKAGE_NAME> < PACKAGE_NAME | MESSAGE_NAME > <OUTPUT_FOLDER>
```

Where: 

- `ROS_VERSION` - version of ROS for which message is generated (ros1, ros2)

- `JAVA_PACKAGE_NAME` - name of the Java package to which all generated messages would belong

- `MESSAGE_NAME` - name of the ROS message

- `PACKAGE_NAME` - name of the ROS package for which Java classes will be generated

- `OUTPUT_FOLDER` - output folder where to place all generated Java classes

# Examples

To see this help:

``` bash
msgmonster
```
Generate messages defined in actionlib_msgs package:

``` bash
msgmonster ros1 myros.actionlib_msgs actionlib_msgs /tmp/actionlib_msgs
```

# Links

[Development](DEVELOPMENT.md)

# Contributors

aeon_flux <aeon_flux@eclipso.ch>

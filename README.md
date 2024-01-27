**msgmonster** - application for generating Message Java classes for [jrosclient](https://github.com/lambdaprime/jrosclient) from ROS msg files.

# Features

- generate Java classes for single msg file or all msg files within a package
- all comments in msg files are preserved and copied to Java classes

# Download

[Release versions](msgmonster/release/CHANGELOG.md)

# Requirements

- Java 17+
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
Generate messages defined in actionlib_msgs package (ROS1):

``` bash
msgmonster ros1 myros.actionlib_msgs actionlib_msgs /tmp/actionlib_msgs
```

Generate message for sensor_msgs/Image (ROS2):

``` bash
msgmonster ros2 myros.actionlib_msgs sensor_msgs/msg/Image /tmp/actionlib_msgs
```

Notice that ROS2 requires 3 parts in MESSAGE_NAME ("sensor_msgs", "msg", "Image") instead of 2 as in ROS1.

# Links

[Development](DEVELOPMENT.md)

# Contacts

aeon_flux <aeon_flux@eclipso.ch>

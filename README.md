**msgmonster** - application for generating Java classes for [jrosclient](https://github.com/lambdaprime/jrosclient) from ROS interface definition files (msg/srv/action).

# Features

- generate Java classes for single ROS interface definition file or all files inside ROS package
- all comments in ROS interface definition files are preserved and copied to Java classes

# Download

[Release versions](msgmonster/release/CHANGELOG.md)

# Requirements

- Java 22+
- rosmsg command

# Usage

```bash
msgmonster [-d] [-exclude regexp1,...,regexpN] [-import package1,...,packageN] <ROS_VERSION> <JAVA_PACKAGE_NAME> < PACKAGE_NAME | MESSAGE_NAME > <OUTPUT_FOLDER>
```

Where: 

- `ROS_VERSION` - version of ROS for which message is generated (ros1, ros2)

- `JAVA_PACKAGE_NAME` - name of the Java package to which all generated messages would belong

- `MESSAGE_NAME` - name of the ROS message

- `PACKAGE_NAME` - name of the ROS package for which Java classes will be generated

- `OUTPUT_FOLDER` - output folder where to place all generated Java classes

Options:

- `-d` - enable debug mode when all debug logging is stored in "msgmonster-debug.log" file under system temporary directory

- `-exclude regexp1,...,regexpN` - exclude ROS interface definition files which names match any of the given regexps. This option can be used to skip any problematic ROS interface definition files.

- `-import package1,...,packageN` - add imports to all generated Java classes in the following form:
``` java
import package1;
...
import packageN;
```
This is useful when messages are generated into a separate Java packages than service or action definitions (for example messages are generated into "java.package.msg" and service definitions which rely on these messages, are generated into "java.package.srv")

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

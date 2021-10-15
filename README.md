**msgmonster** - application for generating Message Java classes for [jrosclient](https://github.com/lambdaprime/jrosclient) from ROS msg files.

# Download

You can download **msgmonster** release versions from [here](https://github.com/pinorobotics/msgmonster/releases)

Latest prerelease version can be found [here](https://github.com/pinorobotics/msgmonster/tree/main/msgmonster/release)

# Requirements

- Java 11

# Usage

```bash
msgmonster <PACKAGE_NAME> < INPUT_FILE | INPUT_FOLDER > <OUTPUT_FOLDER>
```

Where: 

- `PACKAGE_NAME` - name of the Java package to which all generated messages would belong

- `INPUT_FILE` - msg file

- `INPUT_FOLDER` - folder with msg subdirectory where msg files are stored

- `OUTPUT_FOLDER` - output folder where to place all generated Java classes

# Examples

To see this help:

``` bash
msgmonster
```
Generate messages defined in actionlib_msgs folder:

``` bash
msgmonster myros.actionlib_msgs /opt/ros/noetic/share/actionlib_msgs actionlib_msgs
```

# Contributors

aeon_flux <aeon_flux@eclipso.ch>

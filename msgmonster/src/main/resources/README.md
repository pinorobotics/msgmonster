**msgmonster** - application for generating jrosclient Message classes from ROS msg files.

# Download

You can download **msgmonster** release versions from <https://github.com/pinorobotics/msgmonster/releases>

Latest prerelease version can be found here <https://github.com/pinorobotics/msgmonster/tree/main/msgmonster/release>

# Requirements

- Java 11

# Usage

```bash
msgmonster < INPUT_FILE | INPUT_FOLDER > <OUTPUT_FOLDER>
```

Where: 

INPUT_FOLDER - msg file

INPUT_FOLDER - folder with msg subdirectory where msg files are stored

OUTPUT_FOLDER - output folder where to place all generated Java classes

# Examples

To see this help:

``` bash
msgmonster
```
Generate messages defined in actionlib_msgs folder:

``` bash
/opt/ros/noetic/share/actionlib_msgs


# Contributors

aeon_flux <aeon_flux@eclipso.ch>

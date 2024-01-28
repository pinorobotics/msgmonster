# Version 3

- Issue lambdaprime/jros2client#9 Adding support for fixed size arrays
- Fixing PreconditionException for msg files with no fields
- Fixing bug when '#' was removed only from the first line of the Java class level comment

[msgmonster-v3.0.zip](https://github.com/pinorobotics/msgmonster/raw/main/msgmonster/release/msgmonster-v3.0.zip)

# Version 2

- Issue lambdaprime/jros2client#3 Map ROS char type to Java byte
- Updating Gradle files
- Fixing NoSuchElementException when generating classes for ROS2 packages
- Updating readme
- When any of ros commands fail then throw exception with its stderr output
- Removing Streamed annotation
- Do not generate md5 f ROS and rename "type" to "name"
- Support of ROS2
- Updating xfunction and excluding dependency libs from project to take them from artifactory instead
- Adding spotless and moving to Java 17
- No need to convert arrays to collections since XJSon supports arrays now
- We should not use Arrays.toString since XJson expects collection so that it can parse it to json further
- Fixing bug with Arrays not being imported for primitive type arrays
- Integrating with rosmsg command for proper md5 sum calculation. Using it for reading message files as well.
- Adding int8
- Fixing naming of foreign types by appending "Message" to them
- Allow users to change package name
- Do not add delimiters into last statements of hashCode, equals, toString
- Fixing IndexOutOfBoundsException and do not overwrite file if it exists

[msgmonster-v2.0.zip](https://github.com/pinorobotics/msgmonster/raw/main/msgmonster/release/msgmonster-v2.0.zip)

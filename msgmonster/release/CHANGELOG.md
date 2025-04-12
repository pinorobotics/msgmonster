# Version 7

- Support generation for jros2actionlib v5
- Support generation for jros2services v8
- Replace Bash launcher with Java
- Update Gradle
- Issue #6 Add support for "-import" option
- Move all non CLI related logic out from MsgmonsterApp
- Update to Java 22
- Issue lambdaprime/jros2client#13 Adding more primitive types

[msgmonster-v7.0.zip](https://github.com/pinorobotics/msgmonster/raw/main/msgmonster/release/msgmonster-v7.0.zip)

# Version 6

- Fixing tests for ROS1
- Issue #3 Fixing app hangs on big output from external commands
- Issue #5 Fixing NAME in Service messages and including interfaceType
- Adding generation for int constants
- Issue #3 Adding "-exclude" option
- Issue #3 Adding debug mode
- Issue #4 Fix for Time primitive type

[msgmonster-v6.0.zip](https://github.com/pinorobotics/msgmonster/raw/main/msgmonster/release/msgmonster-v6.0.zip)

# Version 5

- Fixing tests for ROS1
- Initialize all fields in Action classes with default values
- Adding Duration primitive type support + move all primitive types under TestDifferentFields test
- Fixing withGoalId return type for action message GetResultRequest
- Fix ClassCastException for equals
- Issue #2 Adding generation support for action files
- Avoid generating unused imports for srv
- Issue #2 Adding generation support for srv files
- If package is not found then show user proper message and do not try to resolve it as a type name (which will fail anyway with misleading "Invalid name" error)
- Adding support for ROS version specific std_msgs types
- Enable unit test for ROS2
- Issue #2 Adding RosFile to capture ROS definition file name and type
- Issue #2 Exclude type of the generated class from the class header as it is already part of the classname itself. Additionally it helps to keep header generic for any non msg types.
- Deprecate isPackage inside RosMsgCommand and let listMsgFiles to support both packages and files
- Issue #2 Adding GeneratorUtils to keep shared logic between generation messages, services and actions
- Updating xfunction to v25
- Move all MsgmonsterAppTests samples to its separate folder
- Assert test logging output
- Integrating JUL
- Moving message generation logic into separate package
- Follow Java notation for "ID" (replacing GoalIDMessage to GoalIdMessage)
- Adding support for uint16
- Masking of HTML symbols "&", "<", ">" in Java comments
- Updating gradle files

[msgmonster-v5.0.zip](https://github.com/pinorobotics/msgmonster/raw/main/msgmonster/release/msgmonster-v5.0.zip)

# Version 4

- Ignore msg files for which generation fails
- Adding support for int64
- Adding generation support for 'fields' parameter

[msgmonster-v4.0.zip](https://github.com/pinorobotics/msgmonster/raw/main/msgmonster/release/msgmonster-v4.0.zip)

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

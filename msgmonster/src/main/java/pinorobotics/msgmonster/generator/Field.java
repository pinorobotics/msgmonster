/*
 * Copyright 2021 msgmonster project
 * 
 * Website: https://github.com/pinorobotics/msgmonster
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pinorobotics.msgmonster.generator;

import java.util.Map;
import java.util.Set;
import pinorobotics.msgmonster.ros.RosVersion;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class Field {
    /** Map from ROS type to Java type */
    private static final Map<String, String> PRIMITIVES_TYPE_MAP =
            Map.ofEntries(
                    Map.entry("bool", "boolean"),
                    Map.entry("byte", "byte"),
                    Map.entry("char", "byte"),
                    Map.entry("float32", "float"),
                    Map.entry("float64", "double"),
                    Map.entry("int8", "byte"),
                    Map.entry("uint8", "byte"),
                    Map.entry("int16", "short"),
                    Map.entry("uint16", "short"),
                    Map.entry("int32", "int"),
                    Map.entry("uint32", "int"),
                    Map.entry("int64", "long"),
                    Map.entry("uint64", "long"));

    private static final Map<String, String> BASIC_TYPE_MAP =
            Map.of(
                    "time", "Time",
                    "Time", "Time",
                    "builtin_interfaces/Time", "Time",
                    "duration", "Duration",
                    "Duration", "Duration",
                    "builtin_interfaces/Duration", "Duration");
    private static final Map<String, String> STDMSG_TYPE_MAP =
            Map.of(
                    "Header", "HeaderMessage",
                    "std_msgs/Header", "HeaderMessage",
                    "string", "StringMessage",
                    "int32", "Int32Message");
    private static final Set<String> STDMSG_VERSION_BASED_TYPES =
            Set.of("Header", "std_msgs/Header");
    private Formatter formatter = new Formatter();
    private String name, type, comment;
    private String value;
    private int arraySize;
    private boolean isArray;

    private RosVersion rosVersion;

    public Field(RosVersion rosVersion, String name, String rosType, String value, String comment) {
        this.rosVersion = rosVersion;
        this.name = name;
        this.type = rosType.replaceAll("(.*)\\[\\d*\\]", "$1");
        this.value = value;
        this.comment = comment;
        isArray = rosType.contains("[");
        if (isArray) arraySize = readArraySize(rosType);
    }

    private int readArraySize(String rosType) {
        var sizeStr = rosType.replaceAll(".*\\[(\\d*)\\]", "$1").replaceAll("\\s*", "");
        if (sizeStr.isBlank()) return 0;
        return Integer.parseInt(sizeStr);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getComment() {
        return comment;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s <%s> [%s] {%s}\n", name, type, value, comment);
    }

    public boolean hasArrayType() {
        return isArray;
    }

    public boolean hasPrimitiveType() {
        return PRIMITIVES_TYPE_MAP.containsKey(type);
    }

    public boolean hasBasicType() {
        return BASIC_TYPE_MAP.containsKey(type);
    }

    public String getJavaType() {
        if (hasBasicType()) {
            return BASIC_TYPE_MAP.get(type);
        } else if (hasPrimitiveType()) {
            return PRIMITIVES_TYPE_MAP.get(type);
        } else if (hasForeignType()) {
            return formatter.formatAsJavaClassName(type.replaceAll(".*/(.*)", "$1"));
        } else if (hasStdMsgType()) {
            return STDMSG_TYPE_MAP.get(type);
        }
        return formatter.formatAsJavaClassName(type);
    }

    /** Type which belongs to different ROS package */
    public boolean hasForeignType() {
        return type.contains("/");
    }

    public String getJavaFullType() {
        if (hasBasicType()) return "id.jrosmessages.primitives." + getJavaType();
        if (hasStdMsgType()) {
            var packageName = "id.jrosmessages.std_msgs.";
            if (STDMSG_VERSION_BASED_TYPES.contains(type))
                packageName = "id.j%smessages.std_msgs.".formatted(rosVersion);
            return packageName + getJavaType();
        }
        if (hasPrimitiveType()) return getJavaType();
        return "id.jrosmessages." + type.replace("/", ".") + "Message";
    }

    public boolean hasStdMsgType() {
        return STDMSG_TYPE_MAP.containsKey(type);
    }

    public int getArraySize() {
        return arraySize;
    }
}

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

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class Field {
    /** Map from ROS type to Java type */
    private static final Map<String, String> PRIMITIVES_TYPE_MAP =
            Map.of(
                    "float64", "double",
                    "float32", "float",
                    "uint32", "int",
                    "uint16", "short",
                    "uint8", "byte",
                    "byte", "byte",
                    "int64", "long",
                    "char", "byte",
                    "bool", "boolean",
                    "int8", "byte");

    private static final Map<String, String> BASIC_TYPE_MAP =
            Map.of(
                    "time", "Time",
                    "duration", "Duration");
    private static final Map<String, String> STDMSG_TYPE_MAP =
            Map.of(
                    "Header", "HeaderMessage",
                    "string", "StringMessage",
                    "int32", "Int32Message");
    private Formatter formatter = new Formatter();
    private String name, type, comment;
    private String value;
    private int arraySize;
    private boolean isArray;

    public Field(String name, String rosType, String value, String comment) {
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
        return getJavaType(type);
    }

    private String getJavaType(String type) {
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
        return getJavaFullType(type);
    }

    private String getJavaFullType(String type) {
        if (hasBasicType()) return "id.jrosmessages.primitives." + getJavaType();
        if (hasStdMsgType()) return "id.jrosmessages.std_msgs." + getJavaType();
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

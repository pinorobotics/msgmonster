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
/*
 * Authors:
 * - aeon_flux <aeon_flux@eclipso.ch>
 */
package pinorobotics.msgmonster.app;

import java.util.Map;
import java.util.Set;

public class Field {
    private static final Map<String, String> PRIMITIVES_TYPE_MAP = Map.of(
            "float64", "double",
            "float32", "float",
            "uint32", "int",
            "uint8", "byte",
            "bool", "boolean");
    private static final Map<String, String> BASIC_TYPE_MAP = Map.of(
            "time", "Time",
            "duration", "Duration");
    private static final Map<String, String> STDMSG_TYPE_MAP = Map.of(
            "Header", "HeaderMessage",
            "string", "StringMessage",
            "int32", "Int32Message");
    private String name, type, comment;
    private String value;

    public Field(String name, String type, String value, String comment) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.comment = comment;
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
        return String.format("%s <%s> [%s] {%s}\n", name, type,
                value, comment);
    }

    public boolean hasArrayType() {
        return type.contains("[");
    }

    public boolean hasPrimitiveType() {
        return PRIMITIVES_TYPE_MAP.containsKey(type);
    }

    public boolean hasBasicType() {
        return BASIC_TYPE_MAP.containsKey(type);
    }

    public String getJavaType() {
        if (hasArrayType()) {
            return getJavaType(type.replaceAll("(.*)\\[\\d*\\]", "$1"));
        }
        return getJavaType(type);
    }
    
    private String getJavaType(String type) {
        if (hasBasicType()) {
            return BASIC_TYPE_MAP.get(type);
        } else if (hasPrimitiveType()) {
            return PRIMITIVES_TYPE_MAP.get(type);
        } else if (hasForeignType()) {
            return type.replaceAll(".*/(.*)", "$1");
        } else if (hasStdMsgType()) {
            return STDMSG_TYPE_MAP.get(type);
        }
        return type;
    }

    /**
     * Type which belongs to different ROS package
     */
    public boolean hasForeignType() {
        return type.contains("/");
    }

    public String getJavaFullType() {
        if (hasArrayType()) {
            return getJavaFullType(type.replaceAll("(.*)\\[\\d*\\]", "$1"));
        }
        return getJavaFullType(type);
    }

    private String getJavaFullType(String type) {
        if (hasBasicType())
            return "id.jrosmessages.primitives." + getJavaType();
        if (hasStdMsgType())
            return "id.jrosmessages.std_msgs." + getJavaType();
        if (hasPrimitiveType()) return getJavaType();
        return "id.jrosmessages." + type.replace("/", ".") + "Message";
    }
    
    public boolean hasStdMsgType() {
        return STDMSG_TYPE_MAP.containsKey(type);
    }
}

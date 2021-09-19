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
            "uint8", "byte");
    private static final Map<String, String> BASIC_TYPE_MAP = Map.of(
            "time", "Time",
            "duration", "Duration");
    private String name, type, comment;

    public Field(String name, String type, String comment) {
        this.name = name;
        this.type = type;
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
    
    @Override
    public String toString() {
        return String.format("%s <%s> %s\n", name, type,
                comment);
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
        if (hasBasicType()) {
            return BASIC_TYPE_MAP.get(type);
        } else if (hasPrimitiveType()) {
            return PRIMITIVES_TYPE_MAP.get(type);
        } else if (hasForeignType()) {
            return type.replaceAll(".*/(.*)", "$1");
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
        if (hasBasicType())
            return "id.jrosmessages.primitives" + getJavaType();
        if (hasPrimitiveType()) return getJavaType();
        return "id.jrosmessages." + type.replace("/", ".");
    }
}

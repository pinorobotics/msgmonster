/*
 * Copyright 2022 msgmonster project
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
package pinorobotics.msgmonster.app;

import java.nio.file.Path;

public class Formatter {

    /** Formats message file name actionlib_msgs/GoalID to its Java class */
    public String format(Path msgFile) {
        return String.format(
                "%sMessage", camelCase(msgFile.getFileName().toString().replaceAll(".msg", "")));
    }

    private String camelCase(String text) {
        var words = text.split("_");
        var buf = new StringBuilder();
        for (var w : words) {
            if (w.isEmpty()) continue;
            buf.append(Character.toTitleCase(w.charAt(0)));
            if (w.length() > 1) {
                buf.append(w.substring(1));
            }
        }
        return buf.toString();
    }

    /** Formats field type name as defined in ROS message file into its Java class name */
    public String formatAsJavaClassName(String fieldType) {
        return camelCase(fieldType) + "Message";
    }

    public String formatAsMethodName(String fieldType) {
        return camelCase(fieldType);
    }
}

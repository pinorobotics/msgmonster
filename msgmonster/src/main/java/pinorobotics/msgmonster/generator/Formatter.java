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
package pinorobotics.msgmonster.generator;

import java.nio.file.Path;
import pinorobotics.msgmonster.ros.RosFile;
import pinorobotics.msgmonster.ros.RosVersion;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class Formatter {

    /**
     * Formats ROS file to its Java class name.
     *
     * <p>For example: "test_msgs/CollisionObject" to "CollisionObjectMessage"
     */
    public String formatAsJavaClassName(RosFile rosFile) {
        return switch (rosFile.type()) {
            case SERVICE ->
                    String.format(
                            "%sServiceDefinition",
                            camelCase(
                                    rosFile.name()
                                            .getFileName()
                                            .toString()
                                            .replaceAll(".srv", "")));
            case ACTION ->
                    String.format(
                            "%sActionDefinition",
                            camelCase(
                                    rosFile.name()
                                            .getFileName()
                                            .toString()
                                            .replaceAll(".action", "")));
            default ->
                    String.format("%sMessage", camelCase(rosFile.name().getFileName().toString()));
        };
    }

    /** Formats field type name (as defined inside ROS message file) into its Java class name */
    public String formatAsJavaClassName(String fieldType) {
        return camelCase(fieldType) + "Message";
    }

    public String formatAsMethodName(String fieldType) {
        return camelCase(fieldType);
    }

    public String formatAsMessageName(RosVersion rosVersion, Path msgFile) {
        var relativePath =
                switch (rosVersion) {
                    case ros2 ->
                            msgFile.getParent()
                                    .getParent()
                                    .getFileName()
                                    .resolve(msgFile.getFileName());
                    default -> msgFile.getParent().getFileName().resolve(msgFile.getFileName());
                };
        return relativePath.toString().replace(".msg", "");
    }

    private String camelCase(String text) {
        var words = text.split("_");
        var buf = new StringBuilder();
        for (var w : words) {
            if (w.isEmpty()) continue;
            buf.append(Character.toTitleCase(w.charAt(0)));
            if (w.length() > 1) {
                var rest = w.substring(1);
                rest = rest.replaceAll("ID$", "Id");
                buf.append(rest);
            }
        }
        return buf.toString();
    }
}

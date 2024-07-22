/*
 * Copyright 2024 msgmonster project
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
package pinorobotics.msgmonster.ros;

import id.xfunction.Preconditions;
import id.xfunction.logging.XLogger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Represents ROS definition files (ex. example_interfaces/msg/Int64,
 * std_msgs/msg/MultiArrayDimension, ...)
 *
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public record RosFile(Path name, RosInterfaceType type) {
    private static final XLogger LOGGER = XLogger.getLogger(RosFile.class);

    public RosFile {
        Preconditions.isTrue(
                name.getNameCount() < 4, "ROS file name cannot have more than 3 parts");
        Preconditions.isTrue(
                !name.getFileName().toString().contains("."),
                "ROS file name cannot have extension");
    }

    public RosFile(String name, RosInterfaceType type) {
        this(Paths.get(name), type);
    }

    public static Optional<RosFile> create(RosVersion rosVersion, Path rosFileName) {
        var rosFile =
                (switch (rosVersion) {
                            case ros1 -> findRos1Type(rosFileName);
                            case ros2 -> findRos2Type(rosFileName);
                        })
                        .map(type -> new RosFile(rosFileName, type));
        if (rosFile.isEmpty()) {
            LOGGER.severe("Could not find type of ROS file: {0}", rosFileName);
        }
        return rosFile;
    }

    private static Optional<RosInterfaceType> findRos2Type(Path msgFile) {
        Preconditions.equals(
                3,
                msgFile.getNameCount(),
                "Invalid name '%s'. Expected three parts separated by '/'",
                msgFile);
        return Optional.ofNullable(
                switch (msgFile.getName(1).toString()) {
                    case "msg" -> RosInterfaceType.MESSAGE;
                    case "srv" -> RosInterfaceType.SERVICE;
                    case "action" -> RosInterfaceType.ACTION;
                    default -> null;
                });
    }

    private static Optional<RosInterfaceType> findRos1Type(Path msgFile) {
        Preconditions.equals(
                2,
                msgFile.getNameCount(),
                "Invalid name '%s'. Expected two parts separated by '/'",
                msgFile);
        var name = msgFile.getName(1).toString();
        return Optional.ofNullable(
                name.endsWith("Action") ? RosInterfaceType.ACTION : RosInterfaceType.MESSAGE);
    }

    public String flatName() {
        return name.toString().replace("/", "_");
    }
}

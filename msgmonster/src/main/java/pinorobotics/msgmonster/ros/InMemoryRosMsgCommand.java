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

import static java.util.stream.Collectors.joining;

import id.xfunction.logging.XLogger;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Instead of calling ROS CLI command it process all data from memory.
 *
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class InMemoryRosMsgCommand implements RosMsgCommand {
    private static final XLogger LOGGER = XLogger.getLogger(InMemoryRosMsgCommand.class);
    private Map<RosFile, List<String>> rosFiles;
    private RosVersion rosVersion;

    public InMemoryRosMsgCommand(Map<RosFile, List<String>> rosFiles, RosVersion rosVersion) {
        this.rosFiles = rosFiles;
        this.rosVersion = rosVersion;
    }

    @Override
    public Stream<RosFile> listFiles(Path rosPath) {
        return rosFiles.keySet().stream();
    }

    @Override
    public Stream<String> lines(RosFile msgFile) {
        var lines = rosFiles.get(msgFile);
        if (lines == null) {
            LOGGER.warning("ROS file not found in-memory: {0}", rosFiles);
            return Stream.empty();
        }
        LOGGER.fine("Lines for {0}\n{1}", msgFile, lines.stream().collect(joining("\n")));
        return lines.stream();
    }

    @Override
    public RosVersion getRosVersion() {
        return rosVersion;
    }
}

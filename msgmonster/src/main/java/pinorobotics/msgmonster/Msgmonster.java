/*
 * Copyright 2025 msgmonster project
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
package pinorobotics.msgmonster;

import id.xfunction.logging.XLogger;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import pinorobotics.msgmonster.app.MsgmonsterApp;
import pinorobotics.msgmonster.generator.JRosActionGenerator;
import pinorobotics.msgmonster.generator.JRosMessageGenerator;
import pinorobotics.msgmonster.generator.JRosServiceGenerator;
import pinorobotics.msgmonster.ros.Ros1MsgCommand;
import pinorobotics.msgmonster.ros.Ros2MsgCommand;
import pinorobotics.msgmonster.ros.RosFile;
import pinorobotics.msgmonster.ros.RosMsgCommandFactory;
import pinorobotics.msgmonster.ros.RosVersion;

public class Msgmonster {
    private static final XLogger LOGGER = XLogger.getLogger(MsgmonsterApp.class);
    private RosMsgCommandFactory rosCommandFactory;
    private List<Predicate<String>> excludePredicates = List.of();
    private List<String> imports = List.of();

    public Msgmonster(RosMsgCommandFactory rosCommandFactory) {
        this.rosCommandFactory = rosCommandFactory;
    }

    public Msgmonster() {
        this(
                rosVersion ->
                        switch (rosVersion) {
                            case ros1 -> new Ros1MsgCommand();
                            case ros2 -> new Ros2MsgCommand();
                        });
    }

    public void setExcludePatterns(List<Pattern> patterns) {
        this.excludePredicates = patterns.stream().map(Pattern::asMatchPredicate).toList();
    }

    public void setUserImports(List<String> imports) {
        this.imports = imports;
    }

    private boolean isExcluded(RosFile rosFile) {
        var rosFileName = rosFile.name().toString();
        return excludePredicates.stream().filter(p -> p.test(rosFileName)).findFirst().isPresent();
    }

    public void run(RosVersion rosVersion, Path packageName, Path input, Path outputFolder)
            throws Exception {
        var rosmsg = rosCommandFactory.create(rosVersion);
        var messageGenerator = new JRosMessageGenerator(rosmsg, outputFolder, packageName);
        var serviceGenerator = new JRosServiceGenerator(rosmsg, outputFolder, packageName);
        var actionGenerator = new JRosActionGenerator(rosmsg, outputFolder, packageName);
        var rosFiles = rosmsg.listFiles(input);
        rosFiles.forEach(
                rosFile -> {
                    LOGGER.info("Processing file {0}", rosFile);
                    if (isExcluded(rosFile)) {
                        LOGGER.info("File marked as excluded, ignoring...");
                        return;
                    }
                    switch (rosFile.type()) {
                        case MESSAGE -> messageGenerator.generateJavaClass(rosFile, imports);
                        case SERVICE -> serviceGenerator.generateJavaClass(rosFile, imports);
                        case ACTION -> actionGenerator.generateJavaClass(rosFile, imports);
                        default -> LOGGER.warning("ROS file type not supported: {0}", rosFile);
                    }
                });
    }
}

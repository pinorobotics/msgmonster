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
package pinorobotics.msgmonster.app;

import id.xfunction.ResourceUtils;
import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.cli.SmartArgs;
import id.xfunction.logging.XLogger;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import pinorobotics.msgmonster.generator.JRosActionGenerator;
import pinorobotics.msgmonster.generator.JRosMessageGenerator;
import pinorobotics.msgmonster.generator.JRosServiceGenerator;
import pinorobotics.msgmonster.ros.Ros1MsgCommand;
import pinorobotics.msgmonster.ros.Ros2MsgCommand;
import pinorobotics.msgmonster.ros.RosFile;
import pinorobotics.msgmonster.ros.RosMsgCommandFactory;
import pinorobotics.msgmonster.ros.RosVersion;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class MsgmonsterApp {
    private static final XLogger LOGGER = XLogger.getLogger(MsgmonsterApp.class);
    private static final ResourceUtils resourceUtils = new ResourceUtils();
    private RosMsgCommandFactory rosCommandFactory;
    private List<Predicate<String>> excludePredicates = List.of();

    private static void usage() {
        resourceUtils.readResourceAsStream("README-msgmonster.md").forEach(System.out::println);
    }

    public MsgmonsterApp(RosMsgCommandFactory rosCommandFactory) {
        this.rosCommandFactory = rosCommandFactory;
    }

    public MsgmonsterApp() {
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

    private boolean isExcluded(RosFile rosFile) {
        var rosFileName = rosFile.name().toString();
        return excludePredicates.stream().filter(p -> p.test(rosFileName)).findFirst().isPresent();
    }

    public void run(List<String> args) throws Exception {
        if (args.size() < 4) {
            usage();
            return;
        }
        var rosVersion = RosVersion.valueOf(args.get(0));
        var rosmsg = rosCommandFactory.create(rosVersion);
        var packageName = Paths.get(args.get(1));
        var outputFolder = Paths.get(args.get(3));
        outputFolder.toFile().mkdirs();
        LOGGER.info("Output folder {0}", outputFolder);
        var input = Paths.get(args.get(2));
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
                        case MESSAGE -> messageGenerator.generateJavaClass(rosFile);
                        case SERVICE -> serviceGenerator.generateJavaClass(rosFile);
                        case ACTION -> actionGenerator.generateJavaClass(rosFile);
                        default -> LOGGER.warning("ROS file type not supported: {0}", rosFile);
                    }
                });
    }

    public static void main(String[] args) throws Exception {
        try {
            XLogger.load("logging-msgmonster.properties");
            var app = new MsgmonsterApp();
            Map<String, Consumer<String>> handlers =
                    Map.of(
                            "-exclude",
                            val -> {
                                app.setExcludePatterns(
                                        Arrays.stream(val.split(","))
                                                .map(Pattern::compile)
                                                .toList());
                            });
            var positionalArgs = new ArrayList<String>();
            Function<String, Boolean> defaultHandler =
                    arg -> {
                        switch (arg) {
                            case "-d":
                                {
                                    XLogger.load("logging-debug-msgmonster.properties");
                                    return true;
                                }
                            default:
                                {
                                    positionalArgs.add(arg);
                                    return true;
                                }
                        }
                    };
            new SmartArgs(handlers, defaultHandler).parse(args);
            app.run(positionalArgs);
        } catch (ArgumentParsingException e) {
            usage();
        }
    }
}

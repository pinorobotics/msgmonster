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
import id.xfunction.logging.XLogger;
import java.nio.file.Paths;
import pinorobotics.msgmonster.generator.JRosMessageGenerator;
import pinorobotics.msgmonster.generator.JRosServiceGenerator;
import pinorobotics.msgmonster.ros.Ros1MsgCommand;
import pinorobotics.msgmonster.ros.Ros2MsgCommand;
import pinorobotics.msgmonster.ros.RosMsgCommandFactory;
import pinorobotics.msgmonster.ros.RosVersion;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class MsgmonsterApp {
    private static final XLogger LOGGER = XLogger.getLogger(MsgmonsterApp.class);
    private static final ResourceUtils resourceUtils = new ResourceUtils();
    private RosMsgCommandFactory rosCommandFactory;

    private static void usage() {
        resourceUtils.readResourceAsStream("README-msgmonster.md").forEach(System.out::println);
    }

    public MsgmonsterApp(RosMsgCommandFactory rosCommandFactory) {
        this.rosCommandFactory = rosCommandFactory;
    }

    public void run(String[] args) throws Exception {
        if (args.length < 4) {
            usage();
            return;
        }
        var rosVersion = RosVersion.valueOf(args[0]);
        var rosmsg = rosCommandFactory.create(rosVersion);
        var packageName = Paths.get(args[1]);
        var outputFolder = Paths.get(args[3]);
        outputFolder.toFile().mkdirs();
        LOGGER.info("Output folder {0}", outputFolder);
        var input = Paths.get(args[2]);
        var messageGenerator = new JRosMessageGenerator(rosmsg, outputFolder, packageName);
        var serviceGenerator = new JRosServiceGenerator(rosmsg, outputFolder, packageName);
        var rosFiles = rosmsg.listFiles(input);
        rosFiles.forEach(
                rosFile -> {
                    LOGGER.info("Processing file {0}", rosFile);
                    switch (rosFile.type()) {
                        case MESSAGE -> messageGenerator.generateJavaClass(rosFile);
                        case SERVICE -> serviceGenerator.generateJavaClass(rosFile);
                        default -> LOGGER.warning("ROS file type not supported: {0}", rosFile);
                    }
                });
    }

    public static void main(String[] args) throws Exception {
        try {
            XLogger.load("logging-msgmonster.properties");
            new MsgmonsterApp(
                            rosVersion ->
                                    switch (rosVersion) {
                                        case ros1 -> new Ros1MsgCommand();
                                        case ros2 -> new Ros2MsgCommand();
                                    })
                    .run(args);
        } catch (ArgumentParsingException e) {
            usage();
        }
    }
}

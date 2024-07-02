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
import id.xfunction.cli.CommandLineInterface;
import id.xfunction.function.Unchecked;
import java.nio.file.Path;
import java.nio.file.Paths;
import pinorobotics.msgmonster.generator.JRosMessageGenerator;
import pinorobotics.msgmonster.ros.Ros1MsgCommand;
import pinorobotics.msgmonster.ros.Ros2MsgCommand;
import pinorobotics.msgmonster.ros.RosMsgCommandFactory;
import pinorobotics.msgmonster.ros.RosVersion;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class MsgmonsterApp {

    private static final ResourceUtils resourceUtils = new ResourceUtils();
    private CommandLineInterface cli;
    private RosMsgCommandFactory rosCommandFactory;

    private static void usage() {
        resourceUtils.readResourceAsStream("README-msgmonster.md").forEach(System.out::println);
    }

    public MsgmonsterApp(CommandLineInterface cli, RosMsgCommandFactory rosCommandFactory) {
        this.cli = cli;
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
        cli.print("Output folder " + outputFolder);
        Path input = Paths.get(args[2]);
        var messageGenerator = new JRosMessageGenerator(cli, rosmsg, outputFolder, packageName);
        if (!rosmsg.isPackage(input)) {
            messageGenerator.generateJavaClass(input);
        } else {
            rosmsg.listMsgFiles(input)
                    // .limit(1)
                    .forEach(Unchecked.wrapAccept(messageGenerator::generateJavaClass));
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            new MsgmonsterApp(
                            new CommandLineInterface(),
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

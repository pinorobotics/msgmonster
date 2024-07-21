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
package pinorobotics.msgmonster.generator;

import id.xfunction.logging.XLogger;
import id.xfunction.text.Substitutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.ainslec.picocog.PicoWriter;
import pinorobotics.msgmonster.ros.InMemoryRosMsgCommand;
import pinorobotics.msgmonster.ros.RosFile;
import pinorobotics.msgmonster.ros.RosInterfaceType;
import pinorobotics.msgmonster.ros.RosMsgCommand;
import pinorobotics.msgmonster.ros.RosVersion;

/**
 * "The first section of this message, above the ---, is the structure (data type and name) of the
 * goal request. The next section is the structure of the result. The last section is the structure
 * of the feedback." (<a
 * href="https://docs.ros.org/en/galactic/Tutorials/Beginner-CLI-Tools/Understanding-ROS2-Actions/Understanding-ROS2-Actions.html">Understanding
 * actions</a>)
 *
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRosActionGenerator {
    private static final XLogger LOGGER = XLogger.getLogger(JRosMessageGenerator.class);
    private Formatter formatter = new Formatter();
    private Map<String, String> substitution = new HashMap<>();
    private Substitutor substitutor = new Substitutor();
    private GeneratorUtils utils = new GeneratorUtils();
    private RosMsgCommand rosmsg;
    private Path outputFolder;
    private Path packageName;

    public JRosActionGenerator(RosMsgCommand rosmsg, Path outputFolder, Path packageName) {
        this.rosmsg = rosmsg;
        this.outputFolder = outputFolder;
        this.packageName = packageName;
    }

    public void generateJavaClass(RosFile rosFile) {
        try {
            generateJavaInternal(rosFile);
        } catch (Exception e) {
            LOGGER.severe("Error generating service class for " + rosFile, e);
            e.printStackTrace();
        }
    }

    private void generateJavaInternal(RosFile rosFile) throws IOException {
        generateActionDefinition(rosFile);
        if (rosmsg.getRosVersion() == RosVersion.ros2) generateAction2Classes(rosFile);
        generateActionMessages(rosFile);
    }

    private void generateAction2Classes(RosFile rosFile) throws IOException {
        substitution.clear();
        var actionName = formatAsActionName(rosFile);
        String[][] params = {
            {"ActionGoalMessage.java", "action2_goal"},
            {"ActionResultMessage.java", "action2_result"},
            {"ActionGetResultRequestMessage.java", "action2_get_result_request"}
        };
        for (int i = 0; i < params.length; i++) {
            Path outFile = outputFolder.resolve(actionName + params[i][0]);
            if (outFile.toFile().exists()) {
                LOGGER.warning("Message file already exist - ignoring");
                return;
            }
            var classOutput = utils.readResource(params[i][1]);
            substitution.put("${actionName}", formatAsActionName(rosFile));
            var msgName = formatter.formatAsMessageName(rosmsg.getRosVersion(), rosFile.name());
            substitution.put("${msgName}", msgName);
            substitution.put("${fullJavaPackageName}", packageName.toString());
            classOutput = substitutor.substitute(classOutput, substitution);
            Files.writeString(outFile, classOutput, StandardOpenOption.CREATE_NEW);
        }
    }

    private String formatAsActionName(RosFile rosFile) {
        return formatter.formatAsJavaClassName(rosFile).replaceAll("ActionDefinition", "");
    }

    private void generateActionDefinition(RosFile rosFile) throws IOException {
        substitution.clear();
        String className = formatter.formatAsJavaClassName(rosFile);
        Path outFile = outputFolder.resolve(className + ".java");
        if (outFile.toFile().exists()) {
            LOGGER.warning("Message file already exist - ignoring");
            return;
        }
        PicoWriter topWriter = new PicoWriter();
        utils.generateHeader(
                topWriter, formatter.formatAsMessageName(rosmsg.getRosVersion(), rosFile.name()));
        topWriter.writeln(String.format("package %s;", packageName));
        topWriter.write(utils.readResource("action_imports"));
        topWriter.writeln();
        generateJavadocComment(topWriter, rosFile);
        topWriter.write(utils.readResource("action_definition"));
        var classOutput = topWriter.toString();
        substitution.put("${actionName}", formatAsActionName(rosFile));
        substitution.put(
                "${rosVersion}",
                ""
                        + switch (rosmsg.getRosVersion()) {
                            case ros1 -> "1";
                            case ros2 -> "2";
                        });
        classOutput = substitutor.substitute(classOutput, substitution);
        Files.writeString(outFile, classOutput, StandardOpenOption.CREATE_NEW);
    }

    private void generateActionMessages(RosFile rosFile) {
        var lines =
                rosmsg.lines(rosFile)
                        .map(String::trim)
                        .collect(Collectors.toCollection(ArrayList<String>::new));
        var posA = lines.indexOf("---");
        if (posA < 0) {
            LOGGER.severe(
                    "Action definition is invalid and will be ignored, separator '---' is not"
                            + " found: {0}",
                    rosFile);
            return;
        }
        var posB = lines.lastIndexOf("---");
        if (posB <= posA) {
            LOGGER.severe(
                    "Action definition is invalid and will be ignored, separator '---' ordering is"
                            + " invalid: {0}",
                    rosFile);
            return;
        }

        try {
            var goal = new RosFile(rosFile.name() + "Goal", RosInterfaceType.MESSAGE);
            var result = new RosFile(rosFile.name() + "Result", RosInterfaceType.MESSAGE);
            var messageGenerator =
                    new JRosMessageGenerator(
                            new InMemoryRosMsgCommand(
                                    Map.of(
                                            goal, lines.subList(0, posA),
                                            result, lines.subList(posA + 1, posB)),
                                    rosmsg.getRosVersion()),
                            outputFolder,
                            packageName);
            messageGenerator.generateJavaClass(goal);
            messageGenerator.generateJavaClass(result);
        } catch (Exception e) {
            LOGGER.severe("Error generating goal/result messages", e);
        }
    }

    private void generateJavadocComment(PicoWriter writer, RosFile rosFile) {
        var comment = "Definition for " + rosFile.name();
        utils.generateJavadocComment(writer, comment);
    }
}

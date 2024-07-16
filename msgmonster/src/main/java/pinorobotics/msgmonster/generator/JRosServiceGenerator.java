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

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRosServiceGenerator {
    private static final XLogger LOGGER = XLogger.getLogger(JRosMessageGenerator.class);
    private Formatter formatter = new Formatter();
    private Map<String, String> substitution = new HashMap<>();
    private Substitutor substitutor = new Substitutor();
    private GeneratorUtils utils = new GeneratorUtils();
    private RosMsgCommand rosmsg;
    private Path outputFolder;
    private Path packageName;

    public JRosServiceGenerator(RosMsgCommand rosmsg, Path outputFolder, Path packageName) {
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
        topWriter.write(utils.readResource("imports"));
        topWriter.writeln();
        topWriter.write(utils.readResource("service_imports"));
        topWriter.writeln();
        generateJavadocComment(topWriter, rosFile);
        topWriter.write(utils.readResource("service_definition"));
        var classOutput = topWriter.toString();
        substitution.put("${serviceName}", className.replaceAll("ServiceDefinition", ""));
        classOutput = substitutor.substitute(classOutput, substitution);
        Files.writeString(outFile, classOutput, StandardOpenOption.CREATE_NEW);
        generateRequestResponse(rosFile);
    }

    private void generateRequestResponse(RosFile rosFile) {
        var lines =
                rosmsg.lines(rosFile)
                        .map(String::trim)
                        .collect(Collectors.toCollection(ArrayList<String>::new));
        var pos = lines.indexOf("---");
        if (pos < 0) {
            LOGGER.severe(
                    "Service definition is invalid and will be ignored, separator '---' is not"
                            + " found: {0}",
                    rosFile);
            return;
        }
        try {
            var request = new RosFile(rosFile.name() + "Request", RosInterfaceType.MESSAGE);
            var response = new RosFile(rosFile.name() + "Response", RosInterfaceType.MESSAGE);
            var messageGenerator =
                    new JRosMessageGenerator(
                            new InMemoryRosMsgCommand(
                                    Map.of(
                                            request, lines.subList(0, pos),
                                            response, lines.subList(pos + 1, lines.size())),
                                    rosmsg.getRosVersion()),
                            outputFolder,
                            packageName);
            messageGenerator.generateJavaClass(request);
            messageGenerator.generateJavaClass(response);
        } catch (Exception e) {
            LOGGER.severe("Error generating request/response messages", e);
        }
    }

    private void generateJavadocComment(PicoWriter writer, RosFile rosFile) {
        var comment = "Definition for " + rosFile.name();
        utils.generateJavadocComment(writer, comment);
    }
}

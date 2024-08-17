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
package pinorobotics.msgmonster.tests.integration;

import id.xfunction.lang.XExec;
import id.xfunction.nio.file.XFiles;
import id.xfunctiontests.AssertRunCommand;
import id.xfunctiontests.XAsserts;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pinorobotics.msgmonster.ros.RosVersion;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class MsgmonsterAppIT {

    private static final String COMMAND_PATH =
            Paths.get("").toAbsolutePath().resolve("build/msgmonster/msgmonster").toString();
    private static Path outputFolder;
    private static final RosVersion ROS_VERSION = findRosVersion();
    private static final Path SAMPLES_PATH = Paths.get("samples", ROS_VERSION.toString());

    @BeforeAll
    public static void setup() throws IOException {
        outputFolder = Files.createTempDirectory("msgmonster");
    }

    private record TestCase(String msgName, Path expectedPath) {}

    static Stream<TestCase> dataProvider() {
        var testCases = new ArrayList<TestCase>();
        testCases.addAll(
                List.of(
                        new TestCase(
                                generateMsgFilePath("std_msgs", Optional.of("String")),
                                SAMPLES_PATH.resolve("StringMessage.java")),
                        new TestCase(
                                generateMsgFilePath("tf2_msgs", Optional.empty()),
                                SAMPLES_PATH.resolve("tf2_msgs"))));
        testCases.add(
                new TestCase(
                        generateMsgFilePath(
                                ROS_VERSION == RosVersion.ros2 ? "example_interfaces" : "std_msgs",
                                Optional.of("Char")),
                        SAMPLES_PATH.resolve("CharMessage.java")));
        return testCases.stream();
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test_happy(TestCase testCase) throws Exception {
        new AssertRunCommand(
                        COMMAND_PATH,
                        ROS_VERSION.toString(),
                        "id.jrosmessages.test_msgs",
                        testCase.msgName,
                        outputFolder.toString())
                .assertReturnCode(0)
                .withOutputConsumer(System.out::println)
                .run();
        var outputSource = outputFolder;
        if (testCase.expectedPath.toFile().isFile())
            outputSource = outputFolder.resolve(testCase.expectedPath.getFileName());
        XAsserts.assertContentEquals(testCase.expectedPath, outputSource);
    }

    @Test
    public void test_invalid_msg_name() throws Exception {
        new AssertRunCommand(
                        COMMAND_PATH,
                        ROS_VERSION.toString(),
                        "id.jrosmessages.test_msgs",
                        ROS_VERSION == RosVersion.ros2
                                ? "std_msgs/HttpClient"
                                : "std_msgs/foo/HttpClient",
                        "/tmp")
                .withWildcardMatching()
                .assertOutputFromResource("test_invalid_msg_name." + ROS_VERSION)
                .run();
    }

    @Test
    public void test_package_not_found() throws Exception {
        new AssertRunCommand(
                        COMMAND_PATH,
                        "-d",
                        ROS_VERSION.toString(),
                        "id.jrosmessages.test_msgs",
                        "stdwqwqw_msgs",
                        "/tmp")
                .withWildcardMatching()
                .assertOutputFromResource("test_pkg_not_found." + ROS_VERSION)
                .run();
        XAsserts.assertMatches(
                getClass(),
                "debug_output." + ROS_VERSION,
                Files.readString(XFiles.TEMP_FOLDER.get().resolve("msgmonster-debug.log")));
    }

    @Test
    public void test_exclude() throws Exception {
        var excludeRegexp =
                ROS_VERSION == RosVersion.ros2
                        ? "tf2_msgs/action/LookupTransform,tf2_msgs/.*/FrameGraph,.*TFMessage"
                        : "tf2_msgs/LookupTransform.*,tf2_msgs/FrameGraph,.*TFMessage";
        new AssertRunCommand(
                        COMMAND_PATH,
                        "-exclude",
                        excludeRegexp,
                        ROS_VERSION.toString(),
                        "id.jrosmessages.test_msgs",
                        generateMsgFilePath("tf2_msgs", Optional.empty()),
                        outputFolder.toString())
                .assertReturnCode(0)
                .withOutputConsumer(System.out::println)
                .run();
        var generatedFiles = Files.list(outputFolder).map(Path::getFileName).toList();
        Assertions.assertEquals("[TF2ErrorMessage.java]", generatedFiles.toString());
    }

    private static String generateMsgFilePath(String rosPackage, Optional<String> name) {
        var res = rosPackage;
        if (name.isPresent()) res += (ROS_VERSION == RosVersion.ros2 ? "/msg/" : "/") + name.get();
        return res;
    }

    private static RosVersion findRosVersion() {
        try {
            return new XExec("ros2").start().await() != 0 ? RosVersion.ros1 : RosVersion.ros2;
        } catch (Exception e) {
            if (e.getCause() instanceof IOException) return RosVersion.ros1;
            throw e;
        }
    }
}

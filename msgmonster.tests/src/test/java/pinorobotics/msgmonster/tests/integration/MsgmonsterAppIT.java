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

import id.xfunction.AssertRunCommand;
import id.xfunction.lang.XExec;
import id.xfunctiontests.XAsserts;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pinorobotics.msgmonster.app.RosVersion;

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
        return Stream.of(
                new TestCase(
                        generateMsgFile("std_msgs", Optional.of("String")),
                        SAMPLES_PATH.resolve("StringMessage.java")),
                new TestCase(
                        generateMsgFile("tf2_msgs", Optional.empty()),
                        SAMPLES_PATH.resolve("tf2_msgs")));
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
    public void test_msg_not_found() throws Exception {
        new AssertRunCommand(
                        COMMAND_PATH,
                        ROS_VERSION.toString(),
                        "id.jrosmessages.test_msgs",
                        "std_msgs/HttpClient",
                        "/tmp")
                .withWildcardMatching()
                .assertOutputFromResource("test_msg_not_found." + ROS_VERSION)
                .run();
    }

    private static String generateMsgFile(String rosPackage, Optional<String> name) {
        var res = rosPackage;
        if (name.isPresent()) res += (ROS_VERSION == RosVersion.ros2 ? "/msg/" : "/") + name.get();
        return res;
    }

    private static RosVersion findRosVersion() {
        try {
            return new XExec("ros2").run().await() != 0 ? RosVersion.ros1 : RosVersion.ros2;
        } catch (Exception e) {
            if (e.getCause() instanceof IOException) return RosVersion.ros1;
            throw e;
        }
    }
}

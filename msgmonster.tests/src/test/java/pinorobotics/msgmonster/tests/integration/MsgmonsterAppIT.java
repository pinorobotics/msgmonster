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

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.AssertRunCommand;
import id.xfunction.lang.XExec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    public static void setup() throws IOException {
        outputFolder = Files.createTempDirectory("msgmonster");
    }

    private record TestCase(RosVersion rosVersion, String msgFile, String expectedFile) {}

    static Stream<TestCase> dataProvider() {
        var rosVersion = findRosVersion();
        return Stream.of(
                new TestCase(
                        rosVersion,
                        generateMsgFile(rosVersion, "std_msgs", "String"),
                        "StringMessage.java"));
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test_happy(TestCase testCase) throws Exception {
        new AssertRunCommand(
                        COMMAND_PATH,
                        testCase.rosVersion.toString(),
                        "id.jrosmessages.test_msgs",
                        testCase.msgFile,
                        outputFolder.toString())
                .assertReturnCode(0)
                .withOutputConsumer(System.out::println)
                .run();
        var expected =
                Files.readString(
                        Paths.get("")
                                .resolve(
                                        Paths.get(
                                                "samples",
                                                testCase.rosVersion.toString(),
                                                testCase.expectedFile)));
        var actual = Files.readString(outputFolder.resolve(testCase.expectedFile));
        assertEquals(expected, actual);
    }

    private static String generateMsgFile(RosVersion rosVersion, String rosPackage, String name) {
        return rosPackage + (rosVersion == RosVersion.ros2 ? "/msg/" : "/") + name;
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

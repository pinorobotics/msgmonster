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
package pinorobotics.msgmonster.tests;

import id.xfunction.logging.XLogger;
import id.xfunction.nio.file.XFiles;
import id.xfunctiontests.XAsserts;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pinorobotics.msgmonster.app.MsgmonsterApp;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class MsgmonsterAppTests {

    private static final Path SAMPLES =
            Paths.get("samples").resolve(MsgmonsterAppTests.class.getSimpleName());
    private static Path outputFolder;
    private static MsgmonsterApp msgmonsterApp;

    @BeforeEach
    public void setup() throws IOException {
        XLogger.load("msgmonster-test.properties");
        outputFolder = Files.createTempDirectory("msgmonster");
        msgmonsterApp = new MsgmonsterApp(rosVersion -> new RosMsgCommandMock(rosVersion, SAMPLES));
    }

    /**
     * Not an integration test. Instead of executing ROS commands it relies on {@link
     * RosMsgCommandMock}
     */
    @ParameterizedTest
    @CsvSource({"ros1", "ros2"})
    public void test_happy(String rosVersion) throws Exception {
        var expectedPath = SAMPLES.resolve("expected").resolve(rosVersion);
        msgmonsterApp.run(
                new String[] {
                    rosVersion, "id.jrosmessages.test_msgs", "test_msgs", outputFolder.toString()
                });
        XAsserts.assertContentEquals(expectedPath.resolve("gen"), outputFolder);
        XAsserts.assertMatches(
                Files.readString(expectedPath.resolve("test_happy")),
                Files.readString(XFiles.TEMP_FOLDER.orElseThrow().resolve("msgmonster-test.log")));
    }
}

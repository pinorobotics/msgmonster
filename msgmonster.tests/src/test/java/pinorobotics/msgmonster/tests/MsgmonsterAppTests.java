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

import id.xfunction.cli.CommandLineInterface;
import id.xfunctiontests.XAsserts;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pinorobotics.msgmonster.app.MsgmonsterApp;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class MsgmonsterAppTests {

    private static Path outputFolder;
    private static ByteArrayOutputStream baos;
    private static MsgmonsterApp msgmonsterApp;

    @BeforeAll
    public static void setupAll() throws IOException {
        outputFolder = Files.createTempDirectory("msgmonster");
    }

    @BeforeEach
    public void setup() {
        baos = new ByteArrayOutputStream();
        msgmonsterApp =
                new MsgmonsterApp(
                        new CommandLineInterface(System.in, baos, baos),
                        rosVersion -> new RosMsgCommandMock(rosVersion, Paths.get("samples")));
    }

    /**
     * ROS version agnostic test. Instead of executing ROS commands it relies on {@link
     * RosMsgCommandMock}
     */
    @Test
    public void test_happy() throws Exception {
        msgmonsterApp.run(
                new String[] {
                    "ros1", "id.jrosmessages.test_msgs", "test_msgs", outputFolder.toString()
                });
        XAsserts.assertContentEquals(Paths.get("samples/expected"), outputFolder);
    }
}

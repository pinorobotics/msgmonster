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
/*
 * Authors:
 * - aeon_flux <aeon_flux@eclipso.ch>
 */
package pinorobotics.msgmonster.tests.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import id.xfunction.AssertRunCommand;

public class MsgmonsterAppTests {

    private static final String COMMAND_PATH = Paths.get("")
            .toAbsolutePath()
            .resolve("build/msgmonster/msgmonster")
            .toString();
    private static Path inputFolder;
    private static Path outputFolder;

    @BeforeAll
    public static void setup() throws IOException {
        inputFolder = Paths.get("").resolve("samples/visualization_msgs");
        outputFolder = Files.createTempDirectory("msgmonster");
    }
    
    @Test
    public void test_happy() throws Exception {
        new AssertRunCommand(COMMAND_PATH, inputFolder.toAbsolutePath().toString(), outputFolder.toString())
                .withReturnCode(0)
                .withOutputConsumer(System.out::println)
                .run();
    }

}

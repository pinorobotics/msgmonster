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

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.XAsserts;
import id.xfunction.cli.CommandLineInterface;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
                        new RosMsgCommandMock(Paths.get("").resolve("samples")));
    }

    @Test
    public void test_happy() throws Exception {
        msgmonsterApp.run(
                new String[] {"id.jrosmessages.test_msgs", "test_msgs", outputFolder.toString()});
        assertFolderEquals(Paths.get("").resolve("samples/expected"), outputFolder);
    }

    public void assertFolderEquals(Path expectedFolder, Path actualFolder) throws IOException {
        List<Path> diff = difference(expectedFolder, actualFolder).collect(Collectors.toList());
        for (var pathA : diff) {
            var relativePath = expectedFolder.relativize(pathA);
            var fileA = pathA.toFile();
            var pathB = actualFolder.resolve(relativePath);
            var fileB = pathB.toFile();
            var c = (fileA.isDirectory() ? 1 : 0) + (fileB.isDirectory() ? 1 : 0);
            if (c == 2) continue;
            XAsserts.assertTrue(c == 0, String.format("Folder missmatch: %s != %s", pathA, pathB));
            assertEquals(Files.readString(pathA), Files.readString(pathB));
        }
    }

    /**
     * Calculates difference between content of folder A and folder B.
     *
     * @param a folder A
     * @param b folder B
     * @return difference between A - B
     */
    public Stream<Path> difference(Path a, Path b) throws IOException {
        return Files.list(a)
                .filter(
                        pathA -> {
                            var pathB = b.resolve(pathA.getFileName());
                            if (!pathB.toFile().exists()) return true;
                            if (pathA.toFile().isDirectory()) {
                                return !pathB.toFile().isDirectory();
                            }
                            try {
                                String fileA = Files.lines(pathA).collect(Collectors.joining("\n"));
                                String fileB = Files.lines(pathB).collect(Collectors.joining("\n"));
                                return !fileA.equals(fileB);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return false;
                            }
                        });
    }
}

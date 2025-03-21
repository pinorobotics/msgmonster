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

import id.xfunction.ResourceUtils;
import id.xfunction.text.Substitutor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;
import org.ainslec.picocog.PicoWriter;

public class GeneratorUtils {
    private static final ResourceUtils resourceUtils = new ResourceUtils();
    private Substitutor substitutor = new Substitutor();

    public void generateHeader(PicoWriter writer, String msgName) {
        var header = readResource("header");
        Map<String, String> substitution = new HashMap<>();
        substitution.put("${msgName}", msgName);
        header = substitutor.substitute(header, substitution);
        writer.write(header);
    }

    public String readResource(String resourceName) {
        return resourceUtils.readResource(resourceName);
    }

    public Stream<String> readResourceAsStream(String resourceName) {
        return resourceUtils.readResourceAsStream(resourceName);
    }

    public void generateJavadocComment(PicoWriter writer, String comment) {
        writer.writeln("/**");
        try (var scanner = new Scanner(comment)) {
            while (scanner.hasNext()) {
                writer.writeln(" * " + scanner.nextLine());
            }
        }
        writer.writeln(" */");
    }

    public void removeLeadingBlankLines(ArrayList<String> lines) {
        while (!lines.isEmpty()) {
            if (!lines.get(0).isEmpty()) break;
            lines.remove(0);
        }
    }

    public void generateUserImports(PicoWriter writer, List<String> userImports) {
        if (userImports.isEmpty()) return;
        writer.writeln("// user defined imports");
        userImports.stream()
                .sorted()
                .distinct()
                .map(pkg -> "import %s;".formatted(pkg))
                .forEach(writer::writeln);
        writer.writeln();
    }
}

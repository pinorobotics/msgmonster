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
package pinorobotics.msgmonster.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.ainslec.picocog.PicoWriter;

import id.xfunction.ResourceUtils;
import id.xfunction.XAsserts;
import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.cli.CommandLineInterface;
import id.xfunction.function.Unchecked;
import id.xfunction.lang.XRE;
import id.xfunction.text.Substitutor;

public class MsgmonsterApp {

    private static final ResourceUtils resourceUtils = new ResourceUtils();
    private CommandLineInterface cli;
    private Path outputFolder;
    private Map<String, String> substitution = new HashMap<>();
    private Substitutor substitutor = new Substitutor();

    private static void usage() {
        resourceUtils.readResourceAsStream("README.md")
            .forEach(System.out::println);
    }

    public MsgmonsterApp(CommandLineInterface cli) {
        this.cli = cli;
    }
    
    private void run(String[] args) throws Exception {
        if (args.length < 2) {
            usage();
            return;
        }
        outputFolder = Paths.get(args[1]);
        Files.walk(Paths.get(args[0]), 1)
            .filter(p -> !p.toFile().isDirectory())
            .filter(p -> p.getFileName().toString().endsWith(".msg"))
            .limit(1)
            .forEach(Unchecked.wrapAccept(this::generateJavaClass));
    }

    public static void main(String[] args) throws Exception {
        try {
            new MsgmonsterApp(new CommandLineInterface()).run(args);
        } catch (ArgumentParsingException e) {
            usage();
        }
    }

    private void generateJavaClass(Path msgFile) throws IOException {
        substitution.clear();
        cli.print("Processing file " + msgFile);
        var definition = readMessageDefinition(msgFile);
        System.out.println(definition);
        PicoWriter topWriter = new PicoWriter();
        generateHeader(topWriter);
        var packagePath = msgFile.subpath(msgFile.getNameCount() - 2, msgFile.getNameCount());
        substitution.put("${msgName}", packagePath.toString());
        topWriter.writeln(String.format("package id.jrosmessages.%s;", msgFile.getParent().getFileName()));
        topWriter.writeln();
        generateImports(topWriter, definition);
        generateClassHeader(topWriter);
        String className = msgFile.getFileName().toString().replaceAll(".msg", "");
        topWriter.writeln_r(String.format("public class %s implements Message {",
                className));
        substitution.put("${className}", className);
        var memvarWriter = topWriter.createDeferredWriter();
        memvarWriter.writeln();
        generateClassFields(memvarWriter, definition);
        topWriter.writeln_l("}");
        var classOutput = topWriter.toString();
        classOutput = substitutor.substitute(classOutput, substitution);
        System.out.println(classOutput);
    }

    private MessageDefinition readMessageDefinition(Path msgFile) throws IOException {
        var lines = Files.lines(msgFile)
            .map(String::trim)
            .collect(Collectors.toCollection(ArrayList<String>::new));
        while (!lines.isEmpty()) {
            if (!lines.get(0).isEmpty()) break;
            lines.remove(0);
        }
        var fieldLineNums = new ArrayList<Integer>();
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (line.isEmpty()) continue;
            if (line.startsWith("#")) continue;
            fieldLineNums.add(i);
        }
        XAsserts.assertTrue(!fieldLineNums.isEmpty(), "No fields in " + msgFile);
        var pos = lines.indexOf("");
        var msgComment = "";
        if (pos < 0) pos = 0;
        if (pos < fieldLineNums.get(0)) {
            // looks like there are comments on the top of the file which does
            // not belong to the field so we use them as message definition comments
            msgComment = lines.subList(0, pos).stream()
                .collect(Collectors.joining("\n"));
        }
        if (fieldLineNums.size() > 1) {
            // if there are many fields and only one comment on the top
            // then
            var fields = lines.subList(fieldLineNums.get(0), lines.size()).stream()
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            if (fields.size() == fieldLineNums.size()) {
                msgComment = lines.subList(0, fieldLineNums.get(0)).stream()
                        .collect(Collectors.joining("\n"));
                pos = fieldLineNums.get(0);
            }
        }
        msgComment = msgComment.replaceAll("^#\\s", "");
        substitution.put("${msgComment}", msgComment);
        var curFieldNum = 0;
        var comment = new StringBuilder();
        var def = new MessageDefinition(msgComment);
        for (int i = pos; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) continue;
            if (i < fieldLineNums.get(curFieldNum)) {
                comment.append(line);
                continue;
            }
            var scanner = new Scanner(line);
            def.addField(scanner.next(), scanner.next(), comment.toString());
            comment.setLength(0);
        }
        return def;
    }

    private void generateClassFields(PicoWriter writer, MessageDefinition definition) {
        writer.writeln(resourceUtils.readResource("class_fields_header"));
        for (var field: definition.getFields()) {
            var body = "";
            if (field.hasArrayType()) {
                body = resourceUtils.readResource("class_field_array");
            } else if (field.hasPrimitiveType()){
                body = resourceUtils.readResource("class_field_primitive");
            } else {
                body = resourceUtils.readResource("class_field");
            }
            Map<String, String> substitution = new HashMap<>();
            substitution.put("${fieldType}", field.getJavaType());
            substitution.put("${fieldName}", field.getName());
            substitution.put("${fieldComment}", field.getComment());
            body = substitutor.substitute(body, substitution);
            writeWithIdent(writer, body);
        }
    }
    
    /**
     * If you send multiline text to PicoWriter with single writeln it will
     * align only first line, the rest of lines will not be aligned which result in:
     * 
     *    // first line aligned
     * // rest of lines are not
     * 
     * To fix that we need to send each line separately.
     */
    private void writeWithIdent(PicoWriter writer, String body) {
        // iterate over chars since we need to wrint empty lines too
        var buf = new StringBuilder();
        for (var ch: body.toCharArray()) {
            if (ch != '\n') {
                buf.append(ch);
                continue;
            }
            writer.writeln(buf.toString());
            buf.setLength(0);
        }
        writer.writeln();
    }

    private void generateClassHeader(PicoWriter writer) {
        var comment = resourceUtils.readResource("class_header");
        writer.write(comment);
    }

    private void generateImports(PicoWriter writer, MessageDefinition definition) {
        writer.write(resourceUtils.readResource("imports"));
        for (var field: definition.getFields()) {
            if (field.hasPrimitiveType()) continue;
            else if (field.hasBasicType() || field.hasForeignType()) {
                writer.writeln(String.format(
                        "import %s;", field.getJavaFullType()));
            } else {
                throw new XRE("Type %s is unknown", field.getType());
            }
        }
    }

    private String asPackageName(Path path) {
        var name = path.toString().replaceAll(".msg", "");
        return name.replace('/', '.');
    }

    private void generateHeader(PicoWriter writer) {
        writer.write(resourceUtils.readResource("header"));
    }
    
}

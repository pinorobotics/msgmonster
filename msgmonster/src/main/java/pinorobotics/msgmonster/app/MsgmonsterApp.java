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
import java.util.Arrays;
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
import id.xfunction.lang.XExec;
import id.xfunction.lang.XRE;
import id.xfunction.text.Substitutor;

public class MsgmonsterApp {

    private static final String MESSAGES_SUBFOLDER = "msg";
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
        Path inputFolder = Paths.get(args[0]);
        verifyInputFolder(inputFolder);
        Files.walk(inputFolder.resolve(MESSAGES_SUBFOLDER), 1)
            .filter(p -> !p.toFile().isDirectory())
            .filter(p -> p.getFileName().toString().endsWith(".msg"))
            .limit(1)
            .forEach(Unchecked.wrapAccept(this::generateJavaClass));
    }

    private void verifyInputFolder(Path inputFolder) {
        XAsserts.assertTrue(inputFolder.resolve(MESSAGES_SUBFOLDER).toFile().isDirectory(),
                "There is no msg/ folder inside of input folder " + inputFolder);
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
        substitution.put("${msgName}", definition.getName());
        substitution.put("${md5sum}", calcMd5Sum(msgFile));
        topWriter.writeln(String.format("package id.jrosmessages.%s;", msgFile.getParent().getFileName()));
        topWriter.writeln();
        generateImports(topWriter, definition);
        generateClassHeader(topWriter, definition);
        String className = msgFile.getFileName().toString().replaceAll(".msg", "");
        topWriter.writeln_r(String.format("public class %s implements Message {",
                className));
        substitution.put("${className}", className);
        var memvarWriter = topWriter.createDeferredWriter();
        memvarWriter.writeln();
        memvarWriter.writeln(resourceUtils.readResource("class_fields_header"));
        generateEnums(memvarWriter, definition);
        generateClassFields(memvarWriter, definition);
        generateWithMethods(memvarWriter, definition);
        topWriter.writeln_l("}");
        var classOutput = topWriter.toString();
        classOutput = substitutor.substitute(classOutput, substitution);
        System.out.println(classOutput);
    }

    private void generateWithMethods(PicoWriter writer, MessageDefinition definition) {
        for (var field: definition.getFields()) {
            var body = "";
            if (field.hasArrayType()) {
                body = resourceUtils.readResource("with_method");
            } else {
                body = resourceUtils.readResource("with_method");
            }
            Map<String, String> substitution = new HashMap<>(this.substitution);
            substitution.put("${fieldType}", field.getJavaType());
            substitution.put("${fieldName}", field.getName());
            substitution.put("${methodName}", "with" + camelCase("_" + field.getName()));
            body = substitutor.substitute(body, substitution);
            writeWithIdent(writer, body);
        }
    }

    private String camelCase(String text) {
        var words = text.split("_");
        var buf = new StringBuilder();
        for (var w: words) {
            if (w.isEmpty()) continue;
            buf.append(Character.toTitleCase(w.charAt(0)));
            if (w.length() > 1) {
                buf.append(w.substring(1));
            }
        }
        return buf.toString();
    }

    private String calcMd5Sum(Path msgFile) {
        var proc = new XExec("md5sum", msgFile.toAbsolutePath().toString())
                .run();
        if (proc.await() != 0) throw new XRE("md5sum calc error: " + proc.stderrAsString());
        return proc.stdoutAsString().split("\\s+")[0];
    }

    private void generateEnums(PicoWriter writer, MessageDefinition definition) {
        var body = resourceUtils.readResource("enum_field");
        for (var enumDef: definition.getEnums()) {
            writer.writeln_r("public enum UnknownType {");
            var memvarWriter = writer.createDeferredWriter();
            for (var field: enumDef.getFields()) {
                writeField(memvarWriter, body, field);
            }
            writer.writeln_l("}");
            writer.writeln();
        }
    }

    private void generateJavadocComment(PicoWriter writer, String comment) {
        writer.writeln("/**");
        var scanner = new Scanner(comment);
        while (scanner.hasNext()) {
            writer.writeln(" * " + scanner.nextLine());
        }
        writer.writeln(" */");
    }
    
    private void writeField(PicoWriter writer, String fieldTemplate, Field field) {
        Map<String, String> substitution = new HashMap<>();
        substitution.put("${fieldType}", field.getJavaType());
        substitution.put("${fieldName}", field.getName());
        fieldTemplate = substitutor.substitute(fieldTemplate, substitution);
        if (!field.getComment().isEmpty()) generateJavadocComment(writer, field.getComment());
        writeWithIdent(writer, fieldTemplate);
    }

    private String readMessageName(Path msgFile) {
        return msgFile.getParent().getParent().getFileName() + "/" + msgFile.getFileName();
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
            if (line.trim().startsWith("#")) continue;
            fieldLineNums.add(i);
        }
        XAsserts.assertTrue(!fieldLineNums.isEmpty(), "No fields in " + msgFile);
        var pos = lines.indexOf("");
        var msgComment = "";
        if (pos < 0) pos = 0;
        if (pos < fieldLineNums.get(0)) {
            // looks like there are comments on the top of the file which are
            // separated from the rest of text with empty line
            // We decide that they does not belong to the field so we use them
            // as message definition comments
            msgComment = lines.subList(0, pos).stream()
                .collect(Collectors.joining("\n"));
        } else {
            pos = 0;
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
        msgComment = cleanComment(msgComment);
        var curFieldNum = 0;
        var commentBuf = new StringBuilder();
        var def = new MessageDefinition(readMessageName(msgFile), msgComment);
        EnumDefinition curEnum = null;
        for (int i = pos; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) continue;
            if (i < fieldLineNums.get(curFieldNum)) {
                addComment(commentBuf, line);
                continue;
            }
            curFieldNum++;
            var buf = line.split("#");
            if (buf.length == 2) {
                addComment(commentBuf, buf[1]);
            }
            System.out.println(Arrays.toString(buf));
            var scanner = new Scanner(buf[0].trim());
            scanner.useDelimiter("[\\s+=]+");
//            while (scanner.hasNext())
//                System.out.println(scanner.next());
            var type = scanner.next();
            var name = scanner.next();
            var value = scanner.hasNext()? scanner.next(): "";
            var comment = commentBuf.toString();
            commentBuf.setLength(0);
            try {
                var id = Integer.parseInt(value);
                if (id == 0) {
                    if (curEnum != null)
                        def.addEnum(curEnum);
                    curEnum = new EnumDefinition();
                }
                if (id == curEnum.getFields().size()) {
                    curEnum.addField(type, name, value, comment);
                    continue;
                }
            } catch (Exception e) {
                // not an integer, ignoring
            }
            def.addField(type, name, value, comment);
        }
        if (curEnum != null && !curEnum.getFields().isEmpty()) def.addEnum(curEnum);
        return def;
    }

    private void addComment(StringBuilder commentBuf, String line) {
        commentBuf.append(cleanComment(line) + "\n");
    }

    private String cleanComment(String comment) {
        return comment.replaceAll("^#\\s*", "").trim();
    }

    private void generateClassFields(PicoWriter writer, MessageDefinition definition) {
        for (var field: definition.getFields()) {
            var body = "";
            if (field.hasArrayType()) {
                body = resourceUtils.readResource("class_field_array");
            } else if (field.hasPrimitiveType()){
                body = resourceUtils.readResource("class_field_primitive");
            } else {
                body = resourceUtils.readResource("class_field");
            }
            writeField(writer, body, field);
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
        if (buf.length() != 0) writer.writeln(buf.toString());
        else writer.writeln();
    }

    private void generateClassHeader(PicoWriter writer, MessageDefinition definition) {
        var comment = "Definition for " + definition.getName();
        comment += "\n" + definition.getComment();
        generateJavadocComment(writer, comment);
        var header = resourceUtils.readResource("class_header");
        writer.write(header);
    }

    private void generateImports(PicoWriter writer, MessageDefinition definition) {
        writer.write(resourceUtils.readResource("imports"));
        var imports = new ArrayList<String>();
        for (var field: definition.getFields()) {
            if (field.hasPrimitiveType()) continue;
            else if (field.hasBasicType() || field.hasForeignType() || field.hasStdMsgType()) {
                imports.add(String.format(
                        "import %s;", field.getJavaFullType()));
            } else {
                //throw new XRE("Type %s is unknown", field.getType());
            }
        }
        imports.stream()
            .sorted()
            .distinct()
            .forEach(writer::writeln);
        writer.writeln();
    }

    private String asPackageName(Path path) {
        var name = path.toString().replaceAll(".msg", "");
        return name.replace('/', '.');
    }

    private void generateHeader(PicoWriter writer) {
        writer.write(resourceUtils.readResource("header"));
    }
    
}

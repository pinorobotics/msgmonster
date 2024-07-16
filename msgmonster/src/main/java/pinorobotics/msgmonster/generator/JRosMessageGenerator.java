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
package pinorobotics.msgmonster.generator;

import id.xfunction.XUtils;
import id.xfunction.logging.XLogger;
import id.xfunction.text.Substitutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.ainslec.picocog.PicoWriter;
import pinorobotics.msgmonster.ros.RosFile;
import pinorobotics.msgmonster.ros.RosMsgCommand;
import pinorobotics.msgmonster.ros.RosVersion;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class JRosMessageGenerator {
    private static final XLogger LOGGER = XLogger.getLogger(JRosMessageGenerator.class);
    private Formatter formatter = new Formatter();
    private Map<String, String> substitution = new HashMap<>();
    private Substitutor substitutor = new Substitutor();
    private GeneratorUtils utils = new GeneratorUtils();
    private RosMsgCommand rosmsg;
    private Path outputFolder;
    private Path packageName;

    public JRosMessageGenerator(RosMsgCommand rosmsg, Path outputFolder, Path packageName) {
        this.rosmsg = rosmsg;
        this.outputFolder = outputFolder;
        this.packageName = packageName;
    }

    public void generateJavaClass(RosFile rosFile) {
        try {
            generateJavaInternal(rosFile);
        } catch (Exception e) {
            LOGGER.severe("Error generating class for " + rosFile, e);
        }
    }

    private void generateJavaInternal(RosFile rosFile) throws IOException {
        substitution.clear();
        String className = formatter.formatAsJavaClassName(rosFile);
        Path outFile = outputFolder.resolve(className + ".java");
        if (outFile.toFile().exists()) {
            LOGGER.warning("Message file already exist - ignoring");
            return;
        }
        var msgFile = rosFile.name();
        var definition = readMessageDefinition(rosFile);
        PicoWriter topWriter = new PicoWriter();
        utils.generateHeader(topWriter, definition.getName());
        substitution.put("${msgName}", definition.getName());
        rosmsg.calcMd5Sum(msgFile).ifPresent(md5 -> substitution.put("${md5sum}", md5));
        topWriter.writeln(String.format("package %s;", packageName));
        topWriter.writeln();
        generateImports(topWriter, definition);
        generateJavadocComment(topWriter, definition);
        generateMessageMetadata(topWriter, definition);
        topWriter.writeln_r(String.format("public class %s implements Message {", className));
        substitution.put("${className}", className);
        var memvarWriter = topWriter.createDeferredWriter();
        memvarWriter.writeln();
        memvarWriter.writeln(utils.readResource("class_fields_header"));
        generateEnums(memvarWriter, definition);
        generateClassFields(memvarWriter, definition);
        generateWithMethods(memvarWriter, definition);
        generateHashCode(memvarWriter, definition);
        generateEquals(memvarWriter, definition);
        generateToString(memvarWriter, definition);
        topWriter.writeln_l("}");
        var classOutput = topWriter.toString();
        classOutput = substitutor.substitute(classOutput, substitution);
        Files.writeString(outFile, classOutput, StandardOpenOption.CREATE_NEW);
    }

    private void generateToString(PicoWriter writer, MessageDefinition definition) {
        if (definition.getFields().isEmpty()) return;
        utils.readResourceAsStream("toString")
                .forEach(
                        line -> {
                            if (!line.contains("${...}")) {
                                writer.writeln(line);
                                return;
                            }
                            var ident =
                                    line.substring(0, line.length() - line.stripLeading().length());
                            var fields = definition.getFields();
                            for (int i = 0; i < fields.size(); i++) {
                                var field = fields.get(i);
                                if (field.hasArrayType()) {
                                    writer.write(
                                            String.format(
                                                    "%s\"%2$s\", %2$s", ident, field.getName()));
                                } else {
                                    writer.write(
                                            String.format(
                                                    "%s\"%2$s\", %2$s", ident, field.getName()));
                                }
                                if (i == fields.size() - 1) writer.writeln("");
                                else writer.writeln(",");
                            }
                        });
    }

    private void generateEquals(PicoWriter writer, MessageDefinition definition) {
        if (definition.getFields().isEmpty()) return;
        utils.readResourceAsStream("equals")
                .forEach(
                        line -> {
                            if (!line.contains("${...}")) {
                                writer.writeln(line);
                                return;
                            }
                            var ident =
                                    line.substring(0, line.length() - line.stripLeading().length());
                            var fields = definition.getFields();
                            for (int i = 0; i < fields.size(); i++) {
                                var field = fields.get(i);
                                if (field.hasArrayType()) {
                                    writer.write(
                                            String.format(
                                                    "%sArrays.equals(%2$s, other.%2$s)",
                                                    ident, field.getName()));
                                } else if (field.hasPrimitiveType()) {
                                    writer.write(
                                            String.format(
                                                    "%s%2$s == other.%2$s",
                                                    ident, field.getName()));
                                } else {
                                    writer.write(
                                            String.format(
                                                    "%sObjects.equals(%2$s, other.%2$s)",
                                                    ident, field.getName()));
                                }
                                if (i == fields.size() - 1) writer.writeln("");
                                else writer.writeln(" &&");
                            }
                        });
    }

    private void generateHashCode(PicoWriter writer, MessageDefinition definition) {
        if (definition.getFields().isEmpty()) return;
        utils.readResourceAsStream("hash_code")
                .forEach(
                        line -> {
                            if (!line.contains("${...}")) {
                                writer.writeln(line);
                                return;
                            }
                            var ident =
                                    line.substring(0, line.length() - line.stripLeading().length());
                            var fields = definition.getFields();
                            for (int i = 0; i < fields.size(); i++) {
                                var field = fields.get(i);
                                if (field.hasArrayType()) {
                                    writer.write(
                                            String.format(
                                                    "%sArrays.hashCode(%s)",
                                                    ident, field.getName()));
                                } else {
                                    writer.write(String.format("%s%s", ident, field.getName()));
                                }
                                if (i == fields.size() - 1) writer.writeln("");
                                else writer.writeln(",");
                            }
                        });
    }

    private void generateMessageMetadata(PicoWriter writer, MessageDefinition definition) {
        var metadataMap = new LinkedHashMap<String, String>();
        metadataMap.put("name", "${className}.NAME");
        if (definition.getFields().size() > 1) {
            metadataMap.put(
                    "fields",
                    "{ %s }"
                            .formatted(
                                    definition.getFields().stream()
                                            .map(Field::getName)
                                            .map(XUtils::quote)
                                            .collect(Collectors.joining(", "))));
        }
        if (rosmsg.getRosVersion() == RosVersion.ros1) {
            metadataMap.put("md5sum", XUtils.quote("${md5sum}"));
        }
        utils.readResourceAsStream("class_message_metadata")
                .forEach(
                        line -> {
                            if (!line.contains("${...}")) {
                                writer.writeln(line);
                                return;
                            }
                            var ident =
                                    line.substring(0, line.length() - line.stripLeading().length());
                            writer.writeln(
                                    metadataMap.entrySet().stream()
                                            .map(
                                                    e ->
                                                            String.format(
                                                                    "%s%s = %s",
                                                                    ident,
                                                                    e.getKey(),
                                                                    e.getValue()))
                                            .collect(Collectors.joining(",\n")));
                        });
    }

    private void generateWithMethods(PicoWriter writer, MessageDefinition definition) {
        for (var field : definition.getFields()) {
            var body = utils.readResource("with_method");
            Map<String, String> substitution = new HashMap<>(this.substitution);
            if (field.hasArrayType()) {
                substitution.put("${fieldType}", field.getJavaType() + "...");
                if (field.getArraySize() > 0) {
                    substitution.put("${arraySize}", "" + field.getArraySize());
                    body = utils.readResource("with_method_for_fixed_size_array");
                }
            } else {
                substitution.put("${fieldType}", field.getJavaType());
            }
            substitution.put("${fieldName}", field.getName());
            substitution.put(
                    "${methodName}", "with" + formatter.formatAsMethodName("_" + field.getName()));
            body = substitutor.substitute(body, substitution);
            writeWithIdent(writer, body);
        }
    }

    private void generateEnums(PicoWriter writer, MessageDefinition definition) {
        var body = utils.readResource("enum_field");
        for (var enumDef : definition.getEnums()) {
            writer.writeln_r("public enum UnknownType {");
            var memvarWriter = writer.createDeferredWriter();
            for (var field : enumDef.getFields()) {
                writeField(memvarWriter, body, field);
            }
            writer.writeln_l("}");
            writer.writeln();
        }
    }

    private void writeField(PicoWriter writer, String fieldTemplate, Field field) {
        Map<String, String> substitution = new HashMap<>();
        substitution.put("${fieldType}", field.getJavaType());
        substitution.put("${fieldName}", field.getName());
        substitution.put("${arraySize}", "" + field.getArraySize());
        fieldTemplate = substitutor.substitute(fieldTemplate, substitution);
        if (!field.getComment().isEmpty()) utils.generateJavadocComment(writer, field.getComment());
        writeWithIdent(writer, fieldTemplate);
    }

    private MessageDefinition readMessageDefinition(RosFile msgFile) throws IOException {
        var lines =
                rosmsg.lines(msgFile)
                        .map(String::trim)
                        .collect(Collectors.toCollection(ArrayList<String>::new));
        utils.removeLeadingBlankLines(lines);
        var fieldLineNums = new ArrayList<Integer>();
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (line.isEmpty()) continue;
            if (line.trim().startsWith("#")) continue;
            fieldLineNums.add(i);
        }
        var msgName = formatter.formatAsMessageName(rosmsg.getRosVersion(), msgFile.name());
        if (fieldLineNums.isEmpty()) {
            return new MessageDefinition(rosmsg.getRosVersion(), msgName);
        }
        var pos = lines.indexOf("");
        var msgCommentLines = new ArrayList<String>();
        if (pos < 0) pos = 0;
        if (pos < fieldLineNums.get(0)) {
            // looks like there are comments on the top of the file which are
            // separated from the rest of text with empty line
            // We decide that they does not belong to the field so we use them
            // as message definition comments
            lines.subList(0, pos).stream().forEach(msgCommentLines::add);
        } else {
            pos = 0;
        }
        if (fieldLineNums.size() > 1) {
            // if there are many fields and only one comment on the top
            // then
            var fields =
                    lines.subList(fieldLineNums.get(0), lines.size()).stream()
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
            if (fields.size() == fieldLineNums.size()) {
                lines.subList(0, fieldLineNums.get(0)).stream().forEach(msgCommentLines::add);
                pos = fieldLineNums.get(0);
            }
        }
        var curFieldNum = 0;
        var commentBuf = new StringBuilder();
        var def =
                new MessageDefinition(
                        rosmsg.getRosVersion(),
                        msgName,
                        msgCommentLines.stream()
                                .map(this::cleanCommentLine)
                                .collect(Collectors.joining("\n")));
        EnumDefinition curEnum = null;
        for (int i = pos; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) continue;
            if (curFieldNum == fieldLineNums.size()) {
                addCommentLine(commentBuf, line);
                continue;
            }
            if (i < fieldLineNums.get(curFieldNum)) {
                addCommentLine(commentBuf, line);
                continue;
            }
            curFieldNum++;
            var buf = line.split("#");
            if (buf.length == 2) {
                addCommentLine(commentBuf, buf[1]);
            }
            var scanner = new Scanner(buf[0].trim());
            scanner.useDelimiter("[\\s+=]+");
            var type = scanner.next();
            var name = scanner.next();
            var value = scanner.hasNext() ? scanner.next() : "";
            var comment = commentBuf.toString();
            commentBuf.setLength(0);
            try {
                var id = Integer.parseInt(value);
                if (id == 0) {
                    if (curEnum != null) def.addEnum(curEnum);
                    curEnum = new EnumDefinition(rosmsg.getRosVersion());
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

    private void addCommentLine(StringBuilder commentBuf, String line) {
        commentBuf.append(cleanCommentLine(line) + "\n");
    }

    private String cleanCommentLine(String comment) {
        return comment.replaceAll("^#\\s*", "")
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .trim();
    }

    private void generateClassFields(PicoWriter writer, MessageDefinition definition) {
        for (var field : definition.getFields()) {
            var body = "";
            if (field.hasArrayType()) {
                body =
                        utils.readResource(
                                field.getArraySize() > 0
                                        ? "class_field_fixed_size_array"
                                        : "class_field_array");
            } else if (field.hasPrimitiveType()) {
                body = utils.readResource("class_field_primitive");
            } else {
                body = utils.readResource("class_field");
            }
            writeField(writer, body, field);
        }
    }

    /**
     * If you send multiline text to PicoWriter with single writeln it will align only first line,
     * the rest of lines will not be aligned which result in:
     *
     * <p>// first line aligned // rest of lines are not
     *
     * <p>To fix that we need to send each line separately.
     */
    private void writeWithIdent(PicoWriter writer, String body) {
        // iterate over chars since we need to wrint empty lines too
        var buf = new StringBuilder();
        for (var ch : body.toCharArray()) {
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

    private void generateJavadocComment(PicoWriter writer, MessageDefinition definition) {
        var comment = "Definition for " + definition.getName();
        if (!definition.getComment().isBlank()) comment += "\n\n<p>" + definition.getComment();
        utils.generateJavadocComment(writer, comment);
    }

    private void generateImports(PicoWriter writer, MessageDefinition definition) {
        writer.write(utils.readResource("imports"));
        var imports = new ArrayList<String>();
        for (var field : definition.getFields()) {
            if (field.hasArrayType()) imports.add("import java.util.Arrays;");
            if (field.hasPrimitiveType()) continue;
            if (field.hasBasicType() || field.hasForeignType() || field.hasStdMsgType()) {
                imports.add(String.format("import %s;", field.getJavaFullType()));
            } else {
                // throw new XRE("Type %s is unknown", field.getType());
            }
        }
        imports.stream().sorted().distinct().forEach(writer::writeln);
        writer.writeln();
    }
}

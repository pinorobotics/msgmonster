package pinorobotics.msgmonster.app;

import java.nio.file.Path;

public class Formatter {

    /**
     * Formats message file name actionlib_msgs/GoalID to its Java class
     */
    public String format(Path msgFile) {
        return String.format("%sMessage",
                camelCase(msgFile.getFileName().toString().replaceAll(".msg", "")));
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

    /**
     * Formats field type name as defined in ROS message file into its Java class name
     */
    public String formatAsJavaClassName(String fieldType) {
        return camelCase(fieldType) + "Message";
    }

    public String formatAsMethodName(String fieldType) {
        return camelCase(fieldType);
    }

}

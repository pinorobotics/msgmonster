package pinorobotics.msgmonster.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.stream.Stream;

import id.xfunction.lang.XExec;
import id.xfunction.lang.XRE;

public class RosMsgCommand {

    public boolean isPackage(Path input) {
        return new XExec("rosmsg packages")
            .run()
            .stdout()
            .filter(Predicate.isEqual(input.toString()))
            .findFirst()
            .isPresent();
    }

    public Stream<Path> listMessageFiles(Path rosPackage) {
        return new XExec("rosmsg package " + rosPackage)
                .run()
                .stdout()
                .map(msg -> Paths.get(msg));
    }

    public String calcMd5Sum(Path msgFile) {
        var cmd = "rosmsg md5 " +  msgFile;
        var proc = new XExec(cmd)
                .run();
        if (proc.await() != 0) throw new XRE("md5sum calc error: " + proc.stderrAsString());
        String md5sum = proc.stdoutAsString();
        if (md5sum.isEmpty()) throw new XRE("Command `%s` returned empty MD5", cmd);
        return md5sum;
    }

    public Stream<String> lines(Path msgFile) {
        return new XExec("rosmsg show -r " + msgFile)
                .run()
                .stdout();
    }

}

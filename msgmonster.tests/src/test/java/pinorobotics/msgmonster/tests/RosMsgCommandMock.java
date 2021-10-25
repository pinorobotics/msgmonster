package pinorobotics.msgmonster.tests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import id.xfunction.XUtils;
import id.xfunction.function.Unchecked;
import id.xfunction.nio.file.XPaths;
import pinorobotics.msgmonster.app.RosMsgCommand;

public class RosMsgCommandMock extends RosMsgCommand {

    private Path folder;
    
    public RosMsgCommandMock(Path folder) {
        this.folder = folder;
    }
    
    @Override
    public Stream<Path> listMessageFiles(Path rosPackage) {
        return Unchecked.get(() -> Files.list(folder.resolve(rosPackage))
                .map(p -> XPaths.splitFileName(p.getFileName().toString())[0]))
                .map(msgName -> rosPackage.resolve(msgName))
                .peek(System.out::println);
    }
    
    @Override
    public String calcMd5Sum(Path msgFile) {
        return Unchecked.get(() -> XUtils.md5Sum(msgFile.toString()));
    }
    
    @Override
    public Stream<String> lines(Path msgFile) {
        return Unchecked.get(() -> Files.lines(folder.resolve(msgFile.toString() + ".msg")));
    }
    
    public boolean isPackage(Path input) {
        return true;
    }
}

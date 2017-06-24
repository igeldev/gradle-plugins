package igel.example_build_src

import groovy.io.FileType
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.zip.CRC32

class BuildScriptHelper {

    static String checksum(File... dirs) {
        checksum(Arrays.asList(dirs))
    }

    static String checksum(Collection<File> dirs) {
        CRC32 crc = new CRC32();

        dirs.each { File dir ->
            if (dir.exists()) {
                dir.eachFileRecurse(FileType.FILES) {
                    FileUtils.checksum(it, crc)
                }
            }
        }

        long value = crc.value
        return String.format('%08X', (value & 0xFFFFFFFF) ^ (value >> 8))
    }

    static void doPrintChecksum(Project project, int indent) {
        project.logger.lifecycle "${' ' * indent}${checksum(project.projectDir)} - $project"
        project.subprojects { doPrintChecksum(it, indent + 2) }
    }

    static void printChecksum(Project project) {
        project.logger.lifecycle 'Project checksum:'
        doPrintChecksum(project, 0)
    }

}

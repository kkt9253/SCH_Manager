package sch_helper.sch_manager.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class FileUtil {

    public String saveFile(MultipartFile file, String folderName, String fileName) throws IOException {

        String basePath = new File("").getAbsolutePath();
        String folderPath = basePath + "/" + folderName;

        System.out.println("folderPath : " + folderPath);

        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("폴더 생성");
        }

        String filePath = folderPath + "/" + fileName;
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);

        System.out.println("filePath: " + filePath);

        return filePath;
    }
}

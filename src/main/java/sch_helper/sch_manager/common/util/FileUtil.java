package sch_helper.sch_manager.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class FileUtil {

    public String saveFile(MultipartFile file, String folderName, String fileName) throws IOException {

        String basePath = new File("").getAbsolutePath();
        String folderPath = basePath + File.separator + folderName;
        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
            System.out.println("폴더 생성");
        }

        System.out.println("folderPath : " + folderPath);

        String filePath = folderPath + File.separator + fileName;
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);

        System.out.println("filePath: " + filePath);

        return filePath;
    }

    public String getFile(String folderName, String fileName) {

        String basePath = new File("").getAbsolutePath();
        String folderPath = basePath + File.separator + folderName;
        String filePath = folderPath + File.separator + fileName;

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("파일이 존재하지 않습니다: " + filePath);
            return null;
        }

        System.out.println("filePath: " + filePath);
        return filePath;
    }
}

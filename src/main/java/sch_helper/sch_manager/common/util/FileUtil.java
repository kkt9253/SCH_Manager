package sch_helper.sch_manager.common.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Base64;

@Component
public class FileUtil {

    public byte[] imageToByte(MultipartFile file) throws IOException {

        return file.getBytes();
    }

    public byte[] encodeByteToBase64(byte[] byteFile) throws IOException {

        return Base64.getEncoder().encode(byteFile);
    }

    public byte[] encodeImageToBase64(MultipartFile file) throws IOException {

        byte[] imageBytes = file.getBytes();
        //return Base64.getEncoder().encodeToString(imageBytes);
        return Base64.getEncoder().encode(imageBytes);
    }

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

    public Resource getFileResource(String folderName, String fileName) {
        try {
            // String filePath = getFile(folderName, fileName);
            String basePath = new File("").getAbsolutePath();
            String filePath = Paths.get(basePath, folderName, fileName).toString();

            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("파일이 존재하지 않습니다: " + filePath);
                return null;
            }

            return new UrlResource(file.toURI());
        } catch (MalformedURLException e) {
            System.out.println("파일을 로드하는 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
}

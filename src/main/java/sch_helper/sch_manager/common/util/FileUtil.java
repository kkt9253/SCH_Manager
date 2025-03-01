package sch_helper.sch_manager.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import sch_helper.sch_manager.common.exception.custom.ApiException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;

import java.io.IOException;
import java.util.Base64;

@Component
public class FileUtil {

    public byte[] imageToByte(MultipartFile file) {

        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ApiException(ErrorCode.TRANSFORMATION_ERROR);
         }
        return bytes;
    }

    public byte[] encodeByteToBase64(byte[] byteFile) {

        return Base64.getEncoder().encode(byteFile);
    }
}

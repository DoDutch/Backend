package graduation.project.DoDutch_server.global.config.aws;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Component
public class S3PathManager {
    @Value("${cloud.aws.s3.path.trip-main}")
    private String tripMain;

    @Value("${cloud.aws.s3.path.expense-main}")
    private String expenseMain;

    public String generateKeyName(String prefix, MultipartFile file, String uuid) {
        String original = file.getOriginalFilename();
        String ext = original.substring(original.lastIndexOf('.'));

        return prefix + "/" + uuid + ext;
    }
}

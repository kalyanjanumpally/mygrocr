package springboot.admin.homeCompany.images;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadFileResponseHome {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public UploadFileResponseHome(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

	// Getters and Setters (Omitted for brevity)
}
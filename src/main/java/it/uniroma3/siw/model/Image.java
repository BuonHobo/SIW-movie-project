package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;

@Entity
public class Image {

    @Transient
    public static final String[] supportedTypes = {"png", "webp", "jpg", "jpeg"};

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String fileName;

    @Column
    private String format;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final ImageData imageData;

    public Image(MultipartFile file) throws Exception {
        this();
        fileName = file.getOriginalFilename();
        imageData.setData(file.getBytes());
        format = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (Arrays.stream(supportedTypes).noneMatch(str -> str.equals(format))) {
            throw new Exception("image.formatNotSupported");
        }
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getThymeleafStub() {
        return "data:image/" + getFormat() + ";base64," + getBase64();
    }

    public String getBase64() {
        return Base64.getEncoder().encodeToString(imageData.getData());
    }

    public Image() {
        this.imageData = new ImageData();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String path) {
        this.fileName = path;
    }

    public byte[] getImageData() {
        return imageData.getData();
    }

    public void setImageData(byte[] imageData) {
        this.imageData.setData(imageData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        return fileName.equals(image.fileName);
    }

    @Override
    public int hashCode() {
        return fileName.hashCode();
    }
}

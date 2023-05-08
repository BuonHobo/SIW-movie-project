package it.uniroma3.siw.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Base64;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String fileName;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final ImageData imageData;

    public Image(String name, byte[] data){
        this();
        fileName=name;
        imageData.setData(data);
    }

    public String getFormat(){
        return getFileName().substring(1+getFileName().lastIndexOf('.'));
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

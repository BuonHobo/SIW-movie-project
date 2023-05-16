package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    @Autowired
    ImageRepository imageRepository;

    public Image findById(Long imageId) throws Exception {
        Image image = imageRepository.findById(imageId).orElse(null);
        if (image == null) {
            throw new Exception("image.notFound");
        }
        return image;
    }

    public void delete(Image image) {
        imageRepository.delete(image);
    }
}
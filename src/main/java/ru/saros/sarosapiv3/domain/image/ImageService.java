package ru.saros.sarosapiv3.domain.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.saros.sarosapiv3.api.exception.ImageNotFoundException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Cannot find this image"));
    }
}

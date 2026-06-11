package org.sharmas.jwelleryshopbe.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "jewellery_shop_products")
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Image upload to Cloudinary failed", e);
        }
    }

    public String uploadFromUrl(String externalImageUrl) {
        try {
            // FIX 1: Added the folder parameter so they go to the right place!
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    externalImageUrl,
                    ObjectUtils.asMap("folder", "jewellery_shop_products")
            );
            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            // FIX 2: If a website (like Amazon) blocks Cloudinary, don't crash the seeder!
            // Just print a warning and fallback to the original raw URL for that one item.
            System.err.println("⚠️ Cloudinary was blocked from downloading: " + externalImageUrl);
            return externalImageUrl;
        }
    }
}
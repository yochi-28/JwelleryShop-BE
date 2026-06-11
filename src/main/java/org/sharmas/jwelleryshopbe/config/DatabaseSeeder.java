package org.sharmas.jwelleryshopbe.config;

import org.sharmas.jwelleryshopbe.models.ProductModel;
import org.sharmas.jwelleryshopbe.repository.ProductRepository;
import org.sharmas.jwelleryshopbe.services.CloudinaryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

 //  Terminal Code: =>   ./gradlew bootRun --args='--spring.profiles.active=seed --spring.main.web-application-type=none'

@Component
@Profile("seed")
public class DatabaseSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    public DatabaseSeeder(ProductRepository productRepository, CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public void run(String... args) throws Exception {

        // 1. Wipe the existing database to prevent duplicate key errors on restart
        productRepository.deleteAll();

        // 2. Generate 16 realistic jewelry products using our helper method
        List<ProductModel> sampleProducts = Arrays.asList(
                // --- GOLD ---
                createProduct(ProductModel.type.GOLD, "24K Pure Gold Temple Necklace", "Exquisite traditional temple jewelry perfect for weddings.", 45.5, 320000.00, "https://assets.myntassets.com/w_360,q_50,,dpr_2,fl_progressive,f_webp/assets/images/15925594/2025/3/22/72b5298b-3571-4c46-8c76-f9f54f22d5961742645220969-Rubans-24K-Gold-Plated-Goddess-Lakshmi-Motif-Handcrafted-Tra-1.jpg", 5),
                createProduct(ProductModel.type.GOLD, "22K Gold Bangle Set", "Set of 4 intricately designed daily wear bangles.", 30.0, 215000.00, "https://smarsjewelry.com/cdn/shop/files/1351.1.jpg?v=1724502379", 8),
                createProduct(ProductModel.type.GOLD, "18K Rose Gold Chain", "Delicate everyday wear rose gold chain.", 8.5, 62000.00, "https://rubans.in/cdn/shop/files/rubans-25-silver-18k-rose-gold-plated-double-layer-heart-pendant-necklace-necklaces-necklace-sets-chains-mangalsutra-34238710153390.jpg?v=1751571564", 25),
                createProduct(ProductModel.type.GOLD, "22K Antique Gold Ring", "Statement ring with traditional temple motifs.", 10.0, 75000.00, "https://m.media-amazon.com/images/I/41Z1e5exKOL._AC_UY1100_.jpg", 14),

                // --- DIAMOND ---
                createProduct(ProductModel.type.DIAMOND, "Solitaire Engagement Ring", "Brilliant cut 1-carat diamond set in 18K white gold.", 4.2, 185000.00, "https://truediamond.in/cdn/shop/files/Solitaire_98.png?v=1767876365", 12),
                createProduct(ProductModel.type.DIAMOND, "Diamond Tennis Bracelet", "Classic 18K white gold bracelet with a continuous row of diamonds.", 14.5, 275000.00, "https://attrangi.in/cdn/shop/files/WhatsApp_Image_2024-07-26_at_11.08.14_PM.jpg?v=1750781916", 7),
                createProduct(ProductModel.type.DIAMOND, "Diamond Halo Pendant", "Round diamond surrounded by a sparkling halo, includes chain.", 3.8, 85000.00, "https://sillyshiny.com/cdn/shop/files/oval_pink_sapphire_east_west_diamond_halo_necklace_yin_and_yan_floating_diamonds_tennis_yellow_gold_ca4385d1-7d1a-4b6d-bb58-b30101f4ecac.jpg?v=1752052965&width=2304", 18),
                createProduct(ProductModel.type.DIAMOND, "Diamond & Platinum Studs", "Everyday luxury 0.5 carat diamond earrings.", 2.5, 65000.00, "https://www.handtstudio.com/cdn/shop/products/il_fullxfull.3212855891_m3k8_9ea729c5-10fa-4a04-a720-0916480de895_580x.jpg?v=1647057586", 22),

                // --- SILVER ---
                createProduct(ProductModel.type.SILVER, "Oxidized Silver Jhumkas", "Handcrafted oxidized silver earrings for daily wear.", 15.0, 4500.00, "https://www.bcositssilver.com/cdn/shop/files/JOO_3180_1.jpg?v=1691751164&width=2048", 50),
                createProduct(ProductModel.type.SILVER, "Silver Anklet Pair (Payal)", "Traditional Indian anklets with delicate ghungroos.", 40.0, 8500.00, "https://m.media-amazon.com/images/I/71LvHU4-PoL._AC_UY1100_.jpg", 30),
                createProduct(ProductModel.type.SILVER, "Sterling Silver Cuff Bracelet", "Modern minimalist wide cuff bracelet.", 25.0, 6000.00, "https://www.zavya.co/cdn/shop/files/BR-80406-R_MD2.jpg?v=1736752683", 20),

                // --- PLATINUM ---
                createProduct(ProductModel.type.PLATINUM, "Platinum Couple Bands", "Matching minimalist platinum rings for him and her.", 12.0, 95000.00, "https://img.tatacliq.com/images/i13/437Wx649H/MP000000008554620_437Wx649H_202309280055591.jpeg", 15),
                createProduct(ProductModel.type.PLATINUM, "Men's Platinum Chain", "Heavy masculine platinum chain, 22 inches.", 35.0, 210000.00, "https://static.malabargoldanddiamonds.com/media/catalog/product/cache/1/image/9df78eab33525d08d6e5fb8d27136e95/c/h/chgen10970_c_1.jpg", 6),

                // --- RUBY ---
                createProduct(ProductModel.type.RUBY, "Kundan Ruby Choker", "Bridal choker with uncut diamonds and deep red Burmese rubies.", 55.0, 450000.00, "https://assets0.mirraw.com/images/13820916/image_original_zoom.jpeg?1773143219", 3),
                createProduct(ProductModel.type.RUBY, "Ruby Tear-Drop Studs", "Elegant earrings featuring vivid pear-shaped rubies.", 6.2, 115000.00, "https://m.media-amazon.com/images/I/81gET4yeClL._AC_UY1100_.jpg", 10),
                createProduct(ProductModel.type.RUBY, "Vintage Ruby Cocktail Ring", "Large oval ruby set in a vintage gold band.", 12.5, 145000.00, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSYUo0mFlqULqRbbvK89jQtGI_XTSZaLXh4_Q&s", 4)
        );

        // 3. Save all 16 items to MongoDB at once!
        productRepository.saveAll(sampleProducts);

        System.out.println("✅ Database Initialized: 16 Sample Jewelry Products Loaded!");
    }

    /**
     * Helper method to keep our seeder clean and readable.
     */
    private ProductModel createProduct(ProductModel.type category, String name, String description, double gram, double price, String externalUrl, int itemCount) {
        ProductModel product = new ProductModel();

        // 3. Upload to Cloudinary right here before creating the object!
        String realCloudinaryUrl = cloudinaryService.uploadFromUrl(externalUrl);

        product.setCategory(category);
        product.setName(name);
        product.setDescription(description);
        product.setGram(gram);
        product.setPrice(price);
        product.setImageUrl(realCloudinaryUrl); // Saving the REAL url
        product.setItemCount(itemCount);
        return product;
    }
}
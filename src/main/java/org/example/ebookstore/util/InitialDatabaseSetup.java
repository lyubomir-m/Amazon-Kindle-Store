package org.example.ebookstore.util;

import org.example.ebookstore.entities.*;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.repositories.*;
import org.example.ebookstore.services.interfaces.AuthorService;
import org.example.ebookstore.services.interfaces.BookService;
import org.example.ebookstore.services.interfaces.ExchangeRateService;
import org.example.ebookstore.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Component
@Order(1)
public class InitialDatabaseSetup implements CommandLineRunner {
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final RoleRepository roleRepository;
    private final RatingRepository ratingRepository;
    private final ReviewRepository reviewRepository;
    private final WishlistRepository wishlistRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();
    private List<Picture> authorPicturesList = new ArrayList<>();
    private List<Picture> userPicturesList = new ArrayList<>();
    private final UserService userService;
    private final AuthorService authorService;
    private final PictureRepository pictureRepository;
    private Map<String, List<Category>> level2NameToLeafCategories = new HashMap<>();
    private final ExchangeRateService exchangeRateService;
    private final BookService bookService;
    private ResourceLoader resourceLoader;
    private final ScheduledTaskAuditRepository scheduledTaskAuditRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public InitialDatabaseSetup(ResourceLoader resourceLoader, PublisherRepository publisherRepository, AuthorRepository authorRepository, CategoryRepository categoryRepository, BookRepository bookRepository, CurrencyRepository currencyRepository, ExchangeRateRepository exchangeRateRepository, RoleRepository roleRepository, RatingRepository ratingRepository, ReviewRepository reviewRepository, WishlistRepository wishlistRepository, ShoppingCartRepository shoppingCartRepository, UserRepository userRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, PasswordEncoder passwordEncoder, UserService userService, AuthorService authorService, PictureRepository pictureRepository, ExchangeRateService exchangeRateService, BookService bookService, ScheduledTaskAuditRepository scheduledTaskAuditRepository, JdbcTemplate jdbcTemplate) {
        this.publisherRepository = publisherRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.currencyRepository = currencyRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.roleRepository = roleRepository;
        this.ratingRepository = ratingRepository;
        this.reviewRepository = reviewRepository;
        this.wishlistRepository = wishlistRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.authorService = authorService;
        this.pictureRepository = pictureRepository;
        this.exchangeRateService = exchangeRateService;
        this.bookService = bookService;
        this.resourceLoader = resourceLoader;
        this.scheduledTaskAuditRepository = scheduledTaskAuditRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (this.roleRepository.count() < 2 || this.publisherRepository.count() == 0) {
            generateDatabase();
        }
    }

    
    public void generateDatabase() throws IOException {
        generatePictures();
        generatePublishers();
        generateAuthors();
        generateCategories();
        generateCurrencies();
        generateExchangeRates();
        generateBooks();
        generateRoles();
        generateUsers();
        generatePlaceholderReviews();
        generateScheduledTasks();
    }

    public void generatePictures() throws IOException {
        List<String> authorImagePaths = ImagePathService.getAllImagePaths(
                "src/main/resources/static/images/authors");
        List<String> userImagePaths = ImagePathService.getAllImagePaths(
                "src/main/resources/static/images/users");

        saveImagesToList(authorImagePaths, this.authorPicturesList);
        saveImagesToList(userImagePaths, this.userPicturesList);
        this.pictureRepository.saveAll(this.authorPicturesList);
        this.pictureRepository.saveAll(this.userPicturesList);
    }

    public void saveImagesToList(List<String> paths, List<Picture> imageList) throws IOException {
        for (String path : paths) {
            File file = new File(path);
            System.out.println("Reading image from path: " + file.getAbsolutePath());
            BufferedImage bImage = ImageIO.read(file);
            if (bImage == null) {
                System.out.println("Failed to read image file: " + file.getAbsolutePath());
                continue;
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String fileExtension = getFileExtension(path);
            ImageIO.write(bImage, fileExtension, bos);
            byte[] data = bos.toByteArray();

            Picture picture = new Picture();
            picture.setName(new File(path).getName());
            picture.setData(data);
            picture.setContentType("image/" + fileExtension);
            imageList.add(picture);
        }
    }

    public String getFileExtension(String path) {
        if (path == null) {
            return null;
        }
        int lastIndex = path.lastIndexOf('.');
        if (lastIndex == -1) {
            return "";
        }
        return path.substring(lastIndex + 1);
    }


    public void generatePublishers() {
        // array size 152
        String[] publisherNames = {
                "Global Reads Publishing", "Future House Publishing", "Eclipse Publishing", "Nova Editions",
                "Pioneer Press", "Crimson Leaf Publishing", "Blue Horizon Books", "Twilight Publishing House",
                "New Dawn Publications", "Horizon Books", "Quantum Press", "Infinity Publishing",
                "Beyond Books", "Galactic Press", "Mystic Sea Publications", "Phoenix Rising Press",
                "Silver Moon Press", "Starlight Publishing", "Twilight Tales", "Voyager Books",
                "Wildflower Publishing", "Zenith Press", "Aurora Publications", "Cosmic Books",
                "Dreamscape Publishing", "Eternal Press", "Fountain of Knowledge Publishing",
                "Golden Age Publications", "Harmony Books", "Illuminated Editions",
                "Journeyman Press", "Kindred Spirits Publishing", "Luminous Literary Press",
                "Moonbeam Publications", "Nebula Press", "Oasis Publications",
                "Paradigm Publishers", "Quest Books", "Renaissance Publishing House",
                "Sapphire Books", "Terra Firma Publishing", "Universal Books",
                "Vanguard Press", "Whispering Winds Press", "Xanadu Publications",
                "Yellow Sun Press", "Zephyr Books", "Alpha Waves Publications",
                "Brave New World Press", "Chronicle Books", "Dawn Treader Press",
                "Echo Point Books", "Fable Forge Press", "Gossamer Wings Publishing",
                "High Tide Publishing", "Infinite Ideas Books", "Jubilee Editions",
                "Kaleidoscope Books", "Legendary Press", "Majestic Publishing House",
                "Nightingale Books", "Oceanview Publishing", "Pinnacle Press",
                "Quicksilver Books", "Radiant Publications", "Starfire Press",
                "Tidal Wave Books", "Umbra Editions", "Visionary Press",
                "Wilderness Press", "Expanse Publishing", "Yonder Books", "Zen Books",
                "Atlas Publishing", "Bounty Books", "Candlelight Publications", "Driftwood Press",
                "Ember Editions", "Flare Books", "Grove Press", "Haven Books",
                "Island Press", "Jade Editions", "Kismet Publishers", "Lantern Books",
                "Mosaic Press", "Nectar Press", "Orbit Books", "Pilgrim Press",
                "Quill & Quiver Publishing", "Ripple Books", "Serenity Press", "Tome & Quill",
                "Unbound Publications", "Vista Books", "Windswept House", "Xeno Press",
                "Yearning Press", "Zion Publishing", "Archway Publishing", "Beacon Press",
                "Crestview Publishing", "Dove Publications", "Evergreen Books", "Frostbite Publishing",
                "Glacier Books", "Hollow Publishing", "Iris Publishers", "Jester Books",
                "Knightly Press", "Labyrinth Books", "Mirage Publishers", "Nomad Press",
                "Olympus Publishing", "Parchment House", "Quasar Publications", "Rosewood Press",
                "Silentium Books", "Thistle Publishing", "Underwood Books", "Verdant Publishing",
                "Willow Tree Press", "Xerxes Books", "Yellow Brick Road Press", "Zodiac Publishing",
                "Avid Reader Press", "Blaze Publishing", "Crystal Clear Publications", "Dandelion Books",
                "Elysian Fields Press", "Frosting Publishing", "Glimmerglass Books", "Halo Publishing",
                "Inscribe Press", "Jubilation Publishing", "Knollwood Press", "Lyric Books",
                "Meadowbrook Press", "Nirvana Publishing", "Opal Publications", "Pristine Publishing",
                "Quintessence Publishing", "Rhapsody Books", "Snowflake Publications", "Tanglewood Press",
                "Unity Publishing", "Voyageur Press", "Whimsy Books", "Xylophone Books",
                "Yesteryear Books", "Zenith Publishing", "Accolade Publications"};

        String[] beginnings = {
                "Leading the charge in the world of", "Pioneering new frontiers in",
                "Revolutionizing the landscape of", "At the heart of innovation in",
                "Setting the standards for excellence in", "Breaking new ground in",
        };

        String[] middles = {
                "literature with a commitment to diverse voices,", "publishing, offering unparalleled stories,",
                "narrative non-fiction, exploring untold histories,", "science fiction and fantasy, crafting new worlds,",
                "educational materials, shaping the minds of tomorrow,", "romance, with a heart for every story,",
        };

        String[] ends = {
                "this publisher stands out as a beacon of creativity.", "they are dedicated to enriching readers' shelves and lives.",
                "their catalog is a testament to enduring stories and innovation.", "they bring untold stories to life, illuminating the human experience.",
                "their commitment to quality and originality is unwavering.", "they are at the forefront of delivering compelling content."
        };

        List<String> publisherDescriptions = new ArrayList<>(152);
        for (String beginning : beginnings) {
            for (String middle : middles) {
                for (String end : ends) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(beginning).append(" ").append(middle).append(" ").append(end);
                    publisherDescriptions.add(sb.toString());
                }
            }
        }

        List<Publisher> publishers = new ArrayList<>();
        for (String name : publisherNames) {
            Publisher publisher = new Publisher();
            publisher.setName(name);
            String description = publisherDescriptions.get(random.nextInt(publisherDescriptions.size()));
            publisher.setDescription(description);
            publisher.setLogoColor(Colors.getRandomColor());

            publishers.add(publisher);
        }

        this.publisherRepository.saveAll(publishers);
    }

    public void generateAuthors() {
        // 13200 authors
        String[] firstNames = {
                "James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Thomas", "Charles",
                "Christopher", "Daniel", "Matthew", "Anthony", "Mark", "Donald", "Steven", "Paul", "Andrew", "Joshua",
                "Kenneth", "Kevin", "Brian", "George", "Edward", "Ronald", "Timothy", "Jason", "Jeffrey", "Ryan",
                "Jacob", "Gary", "Nicholas", "Eric", "Jonathan", "Stephen", "Larry", "Justin", "Scott", "Brandon",
                "Benjamin", "Samuel", "Gregory", "Alexander", "Jack", "Dennis", "Jerry", "Tyler", "Aaron", "Jose",
                "Adam", "Henry", "Douglas", "Nathan", "Peter", "Zachary", "Kyle", "Walter", "Harold", "Jeremy",
                "Ethan", "Carl", "Keith", "Roger", "Gerald", "Christian", "Terry", "Sean", "Arthur", "Austin",
                "Noah", "Lawrence", "Jesse", "Joe", "Bryan", "Billy", "Jordan", "Albert", "Dylan", "Bruce",
                "Willie", "Gabriel", "Alan", "Juan", "Logan", "Wayne", "Ralph", "Roy", "Eugene", "Randy",
                "Vincent", "Russell", "Louis", "Philip", "Bobby", "Johnny", "Bradley", "Clarence", "Sam", "Leonard",
                "Francis", "Cody", "Alexander", "Edwin", "Caleb", "Evan", "Antonio", "Frederick", "Elijah", "Dale"
        };

        String[] lastNames = {
                "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor",
                "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Garcia", "Martinez", "Robinson",
                "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall", "Allen", "Young", "Hernandez", "King",
                "Wright", "Lopez", "Hill", "Scott", "Green", "Adams", "Baker", "Gonzalez", "Nelson", "Carter",
                "Mitchell", "Perez", "Roberts", "Turner", "Phillips", "Campbell", "Parker", "Evans", "Edwards", "Collins",
                "Stewart", "Sanchez", "Morris", "Rogers", "Reed", "Cook", "Morgan", "Bell", "Murphy", "Bailey",
                "Rivera", "Cooper", "Richardson", "Cox", "Howard", "Ward", "Torres", "Peterson", "Gray", "Ramirez",
                "James", "Watson", "Brooks", "Kelly", "Sanders", "Price", "Bennett", "Wood", "Barnes", "Ross",
                "Henderson", "Coleman", "Jenkins", "Perry", "Powell", "Long", "Patterson", "Hughes", "Flores", "Washington",
                "Butler", "Simmons", "Foster", "Gonzales", "Bryant", "Alexander", "Russell", "Griffin", "Diaz", "Hayes",
                "Myers", "Ford", "Hamilton", "Graham", "Sullivan", "Wallace", "Woods", "Cole", "West", "Jordan",
                "Owens", "Reynolds", "Fisher", "Ellis", "Harrison", "Gibson", "McDonald", "Cruz", "Marshall", "Ortiz"
        };

        String[] skills = {
                "Award-winning author celebrated for his innovative narrative techniques and profound insight into the human condition,",
                "Bestselling novelist whose gripping narratives have become a staple in the thriller genre,",
                "Critically acclaimed for his meticulous research and the depth of his historical biographies,",
                "Innovative writer known for pushing the boundaries of science fiction and fantasy,",
                "Influential poet whose verses resonate with the complexities of love and loss,",
                "Pioneering journalist who has brought light to some of the most pressing issues of our times,",
                "Visionary playwright whose works challenge perceptions and speak powerfully to a range of audiences,",
                "Groundbreaking academic whose publications have changed the landscape of contemporary literary studies,",
                "Renowned for his explorative essays on modern society and the individual’s place within it,",
                "Leading voice in children's literature, crafting stories that entertain and educate young minds alike,"
        };

        String[] genres = {
                "weaving tales of historical fiction that vividly bring the past to life,",
                "mastering the art of suspense with his mystery and thriller novels,",
                "delving into the complexities of science and technology with a humanistic approach,",
                "creating captivating fantasy worlds that have enchanted readers of all ages,",
                "offering guidance through his transformative self-help books,",
                "exploring the multifaceted nature of romance through his compelling narratives,",
                "resonating with the trials and triumphs of the young adult experience,",
                "penning biographies that uncover the nuanced lives of historical figures,",
                "crafting poetry that explores the deepest emotions and universal human experiences,",
                "shaping young minds with his delightful and insightful children's literature,"
        };

        String[] accolades = {
                "his work has been featured in the New York Times for its remarkable literary achievements.",
                "he is a proud recipient of the National Book Award for his contributions to contemporary literature.",
                "a favorite in book clubs around the world, his novels spark lively discussions and lasting memories.",
                "his writing has garnered numerous literary prizes, recognizing his flair for vivid storytelling.",
                "as a frequent book festival panelist, he inspires aspiring writers with his passion and wisdom.",
                "his leadership in writing workshops has guided many to find their own voice in the literary world.",
                "his genre-defining books have set a high bar for storytelling excellence and creativity.",
                "known for captivating storytelling, he weaves complex characters and plot with finesse.",
                "boasting numerous literary accolades, he is celebrated by critics and readers alike.",
                "his works have been translated into multiple languages, touching the lives of readers across the globe."
        };

        List<String> descriptions = new ArrayList<>(1000);
        for (String skill : skills) {
            for (String genre : genres) {
                for (String accolade : accolades) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(skill).append(" ").append(genre).append(" ").append(accolade);
                    descriptions.add(sb.toString());
                }
            }
        }

        List<Author> authors = new ArrayList<>(13200);
        for (String firstName : firstNames) {
            for (String lastName : lastNames) {
                StringBuilder sb = new StringBuilder();
                sb.append(firstName).append(" ").append(lastName);
                String fullName = sb.toString();
                String description = descriptions.get(random.nextInt(descriptions.size()));

                Author author = new Author();
                author.setFullName(fullName);
                author.setDescription(description);
                author.setPicture(this.authorPicturesList.get(random.nextInt(this.authorPicturesList.size())));

                authors.add(author);
            }
        }

        this.authorRepository.saveAll(authors);
    }

    public void generateCategories() {
        // array size 28
        String[] categories = {
                "Arts & Photography",
                "Biographies & Memoirs",
                "Business & Money",
                "Children's eBooks",
                "Comics, Manga & Graphic Novels",
                "Computers & Technology",
                "Cookbooks, Food & Wine",
                "Crafts, Hobbies & Home",
                "Education & Teaching",
                "Engineering & Transportation",
                "Health, Fitness & Dieting",
                "History",
                "Humor & Entertainment",
                "Law",
                "Literature & Fiction",
                "Medical eBooks",
                "Mystery, Thriller & Suspense",
                "Parenting & Relationships",
                "Politics & Social Sciences",
                "Reference",
                "Religion & Spirituality",
                "Romance",
                "Science & Math",
                "Science Fiction & Fantasy",
                "Self-Help",
                "Sports & Outdoors",
                "Teen & Young Adult",
                "Travel"
        };

        List<Category> categoryList = new ArrayList<>(5909);
        Category level1 = new Category();
        level1.setName("Kindle eBooks");
        categoryList.add(level1);

        for (int i = 0; i < categories.length; i++) {
            String level2Name = categories[i];
            Category level2 = new Category();
            level2.setName(level2Name);
            level2.setParent(level1);
            level1.addSubcategory(level2);

            this.level2NameToLeafCategories.putIfAbsent(level2Name, new ArrayList<>());
            categoryList.add(level2);

            for (int j = 0; j < 10; j++) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Subcategory ").append(i + 1).append(".").append(j + 1);
                String level3Name = sb3.toString();

                Category level3 = new Category();
                level3.setName(level3Name);
                level3.setParent(level2);
                level2.addSubcategory(level3);
                categoryList.add(level3);

                for (int k = 1; k <= 5; k++) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(sb3);
                    sb4.append(".").append(k);
                    String level4Name = sb4.toString();

                    Category level4 = new Category();
                    level4.setName(level4Name);
                    level4.setParent(level3);
                    level3.addSubcategory(level4);
                    categoryList.add(level4);

                    for (int l = 1; l <= 3; l++) {
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append(sb4);
                        sb5.append(".").append(l);
                        String level5Name = sb5.toString();

                        Category level5 = new Category();
                        level5.setName(level5Name);
                        level5.setParent(level4);
                        level4.addSubcategory(level5);

                        categoryList.add(level5);

                        List<Category> leaves = this.level2NameToLeafCategories.get(level2Name);
                        leaves.add(level5);
                    }
                }
            }
        }

        this.categoryRepository.saveAll(categoryList);
    }

    public void generateCurrencies() {
        List<Currency> currencies = Arrays.asList(
                new Currency("Euro", "EUR", "€"),
                new Currency("US Dollar", "USD", "$"),
                new Currency("Australian Dollar", "AUD", "A$"),
                new Currency("Brazilian Real", "BRL", "R$"),
                new Currency("Indian Rupee", "INR", "₹"),
                new Currency("Chinese Yuan", "CNY", "¥"),
                new Currency("Egyptian Pound", "EGP", "E£"),
                new Currency("Nigerian Naira", "NGN", "₦")
        );

        this.currencyRepository.saveAll(currencies);
    }

    public void generateExchangeRates() {
        LocalDate validityDate = LocalDate.of(2024, 4, 2);
        List<ExchangeRate> exchangeRates = List.of(
                new ExchangeRate(this.currencyRepository.findByCodeIgnoreCase("EUR").get(), validityDate,
                        BigDecimal.valueOf(1.00)),
                new ExchangeRate(this.currencyRepository.findByCodeIgnoreCase("USD").get(), validityDate,
                        BigDecimal.valueOf(1.07)),
                new ExchangeRate(this.currencyRepository.findByCodeIgnoreCase("AUD").get(), validityDate,
                        BigDecimal.valueOf(1.65)),
                new ExchangeRate(this.currencyRepository.findByCodeIgnoreCase("BRL").get(), validityDate,
                        BigDecimal.valueOf(5.42)),
                new ExchangeRate(this.currencyRepository.findByCodeIgnoreCase("INR").get(), validityDate,
                        BigDecimal.valueOf(89.43)),
                new ExchangeRate(this.currencyRepository.findByCodeIgnoreCase("CNY").get(), validityDate,
                        BigDecimal.valueOf(7.76)),
                new ExchangeRate(this.currencyRepository.findByCodeIgnoreCase("EGP").get(), validityDate,
                        BigDecimal.valueOf(50.64)),
                new ExchangeRate(this.currencyRepository.findByCodeIgnoreCase("NGN").get(), validityDate,
                        BigDecimal.valueOf(1396.75))
        );

        this.exchangeRateRepository.saveAll(exchangeRates);
    }

    public void generateBooks() {
        // (12, 14, 16, 2688)
        String[] beginnings = {"In a world where magic and mystery intertwine,",
                "Set against the backdrop of a war-torn kingdom,",
                "In the heart of an ancient forest shrouded in secrets,",
                "Amidst the chaos of a galaxy on the brink of destruction,",
                "Exploring the mysteries of a hidden society beneath the sea,",
                "In a time of ancient gods, warlords, and kings, a legend awakens,",
                "In the silent vacuum of space, a lone ship harbors a startling secret,",
                "Under the guise of a peaceful utopia, a society hides a dark underworld,",
                "Within the ruins of a civilization long forgotten, adventurers uncover a relic of untold power,",
                "Through the mists of an enchanted forest, a hidden path leads to realms unknown,",
                "Beyond the reaches of our known world, lies a land where magic flows as freely as water,",
                "In the depths of the urban jungle, a conspiracy threatens to unravel the fabric of society,"
        };
        String[] middles = {"a forbidden love blossoms against all odds. Amidst danger and betrayal,",
                "an unlikely hero rises, armed with a power they barely understand. With the fate of the world hanging in the balance,",
                "darkness threatens to consume everything, leaving nothing but ashes. Heroes and villains alike must choose their sides,",
                "an ancient secret is uncovered, promising power or doom. As alliances shift,",
                "destinies intertwine in a dance of fate and freewill. Caught in a web of lies,",
                "friendship is tested as never before, challenging beliefs and forging new bonds. Amidst trials,",
                "a journey of self-discovery begins, leading to the truth hidden within. Through pain and loss,",
                "rivalries lead to conflict, igniting a spark that could burn the world to the ground. In the shadow of impending doom,",
                "a lone detective confronts mysteries that defy logic. In the shadows,",
                "the discovery of an ancient artifact sets off a race against time. Surrounded by enemies,",
                "a rebellion takes shape, seeking to overturn centuries of oppression. At the heart of the uprising,",
                "an unexpected alliance forms, bridging worlds and cultures. Amidst conflict,",
                "the fabric of reality begins to fray, revealing glimpses of parallel universes. As worlds collide,",
                "a curse threatens to bring about an eternal night. Only a bond forged in destiny can break the spell,"
        };
        String[] ends = {"Can they survive the coming storm, or will their newfound love be lost forever?",
                "Will they find the courage within to face the darkness, or will they falter in the face of adversity?",
                "Is their bond strong enough to save the world, or will it crumble under the weight of their destiny?",
                "What will be the cost of their victory, and is it a price they are willing to pay?",
                "Can love triumph over evil, or will the shadows swallow them whole?",
                "Will they uncover the truth in time to change the course of history, or will they be mere pawns in a larger game?",
                "How will their choices shape the future, and can they live with the consequences?",
                "Can peace ever be restored, or will the world be forever changed by the battles waged?",
                "will the light of hope shine through the darkness, or will it be extinguished forever?",
                "are they prepared to make the ultimate sacrifice for the greater good, or will self-preservation prevail?",
                "can they forge a new future, or are they doomed to repeat the mistakes of the past?",
                "will the seeds of change planted today bloom into a better tomorrow, or will they wither and die?",
                "can a single voice of truth cut through the cacophony of lies, or will it be silenced?",
                "will the pursuit of knowledge lead to enlightenment, or will it open a Pandora's box of new horrors?",
                "can they bridge the chasm between worlds, or will the gap widen beyond repair?",
                "will the dawn of a new era bring peace, or will it herald an age of chaos?"
        };

        List<String> descriptions = new ArrayList<>(2688);
        for (String beginning : beginnings) {
            for (String middle : middles) {
                for (String end : ends) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(beginning).append(" ").append(middle).append(" ").append(end);
                    descriptions.add(sb.toString());
                }
            }
        }

        List<Book> books = new ArrayList<>(194172);
        String line;
        List<Author> authorList = this.authorRepository.findAll();

        BigDecimal usdRate = this.exchangeRateService.getLatestRate("USD").get();
        BigDecimal audRate = this.exchangeRateService.getLatestRate("AUD").get();
        BigDecimal brlRate = this.exchangeRateService.getLatestRate("BRL").get();
        BigDecimal inrRate = this.exchangeRateService.getLatestRate("INR").get();
        BigDecimal cnyRate = this.exchangeRateService.getLatestRate("CNY").get();
        BigDecimal egpRate = this.exchangeRateService.getLatestRate("EGP").get();
        BigDecimal ngnRate = this.exchangeRateService.getLatestRate("NGN").get();
        BigDecimal minPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("30.00");
        List<Publisher> publisherList = this.publisherRepository.findAll();
        List<Category> updatedCategories = new ArrayList<>();

        Resource resource = resourceLoader.getResource("classpath:datafiles/bookdata.csv");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(";");
                if (tokens.length < 6) {
                    System.out.println(line);
                    continue;
                }
                Book book = new Book();

                book.setImageUrl(tokens[0]);
                char[] array = tokens[1].toCharArray();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < array.length; i++) {
                    if ((int) array[i] <= 127) {
                        if (i == 0) {
                            sb.append(String.valueOf(array[i]).toUpperCase());
                            continue;
                        }
                        sb.append(array[i]);
                    }
                }

                String title = sb.toString();
                if (title.length() > 255) {
                    title = title.substring(0, 255);
                }
                if (title.length() < 3) {
                    System.out.println(line);
                    continue;
                }
                book.setTitle(title);

                int authorCount = 1 + random.nextInt(3);
                Set<Picture> currentPictures = new HashSet<>();
                for (int i = 0; i < authorCount; i++) {
                    Author author = authorList.get(random.nextInt(authorList.size()));
                    while (currentPictures.contains(author.getPicture())) {
                        author = authorList.get(random.nextInt(authorList.size()));
                    }

                    currentPictures.add(author.getPicture());
                    book.addAuthors(author);
                    author.addBook(book);
                }

                String categoryName = tokens[5];
                List<Category> leafCategories = this.level2NameToLeafCategories.get(categoryName);
                if (leafCategories == null || leafCategories.isEmpty()) {
                    System.out.println(categoryName);
                    continue;
                }
                Category category = leafCategories.get(random.nextInt(leafCategories.size()));
                book.addCategories(category);
                category.addBook(book);
                if (!updatedCategories.contains(category)) {
                    updatedCategories.add(category);
                }

                BigDecimal priceEur = minPrice.add(BigDecimal.valueOf(Math.random()).multiply(maxPrice.subtract(minPrice)));
                priceEur = this.bookService.round(priceEur);
                book.setPriceEur(priceEur);
                book.setPriceUsd(this.bookService.round(priceEur.multiply(usdRate)));
                book.setPriceAud(this.bookService.round(priceEur.multiply(audRate)));
                book.setPriceBrl(this.bookService.round(priceEur.multiply(brlRate)));
                book.setPriceInr(this.bookService.round(priceEur.multiply(inrRate)));
                book.setPriceCny(this.bookService.round(priceEur.multiply(cnyRate)));
                book.setPriceEgp(this.bookService.round(priceEur.multiply(egpRate)));
                book.setPriceNgn(this.bookService.round(priceEur.multiply(ngnRate)));

                book.setAverageRating(4.0 + Math.random());
                book.setRatingsCount(random.nextLong(100000));
                int publicationDay = 1 + random.nextInt(26);
                int publicationMonth = 1 + random.nextInt(12);
                int publicationYear = 2000 + random.nextInt(24);
                LocalDate publicationDate = LocalDate.of(publicationYear, publicationMonth, publicationDay);
                book.setPublicationDate(publicationDate);
                book.setPurchaseCount(random.nextLong(2000000));

                Publisher publisher = publisherList.get(random.nextInt(publisherList.size()));
                book.setPublisher(publisher);
                publisher.addBook(book);
                book.setDescription(descriptions.get(random.nextInt(descriptions.size())));
                book.setCoverColor(Colors.getRandomColor());

                StringBuilder searchSb = new StringBuilder();
                searchSb.append(book.getTitle()).append(" ");
                searchSb.append(book.getPublisher().getName()).append(" ");
                for (Author author : book.getAuthors()) {
                    searchSb.append(author.getFullName()).append(" ");
                }
                searchSb.deleteCharAt(searchSb.length() - 1);
                book.setSearchColumn(searchSb.toString());

                books.add(book);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.authorRepository.saveAll(authorList);
        this.publisherRepository.saveAll(publisherList);
        this.categoryRepository.saveAll(updatedCategories);
        this.bookRepository.saveAll(books);
    }

    public void generateRoles() {
        Role admin = new Role(Role.UserRole.ADMIN);
        Role user = new Role(Role.UserRole.USER);
        this.roleRepository.save(admin);
        this.roleRepository.save(user);
    }

    public void generateUsers() {
        String[] firstNames = {"James", "John", "Robert", "Michael", "William", "David",
                "Richard", "Joseph", "Thomas", "Charles"};

        String[] lastNames = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis",
                "Miller", "Wilson", "Moore", "Taylor"};

        Role adminRole = this.roleRepository.findByName(Role.UserRole.ADMIN).get();
        Role userRole = this.roleRepository.findByName(Role.UserRole.USER).get();
        List<User> users = new ArrayList<>();
        List<Wishlist> wishlists = new ArrayList<>();
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            wishlists.add(new Wishlist());
            shoppingCarts.add(new ShoppingCart());
        }

        for (int i = 0; i < 10; i++) {
            User user = createUser("user" + (i + 1), "user" + (i + 1) + "@mail.com",
                    "1234", firstNames[i], lastNames[i], 30, wishlists.get(i), shoppingCarts.get(i), userRole);
            user.setPicture(this.userPicturesList.get(i));
            users.add(user);
        }

        users.add(createUser("admin", "admin@mail.com", "1234", "Admin",
                "Admin", 30, wishlists.get(10), shoppingCarts.get(10), adminRole, userRole));
        users.add(createUser("user", "user@mail.com", "1234", "Ivan", "Ivanov",
                30,wishlists.get(11), shoppingCarts.get(11), userRole));

        this.wishlistRepository.saveAll(wishlists);
        this.shoppingCartRepository.saveAll(shoppingCarts);
        users.forEach(this.userService::save);
    }

    public User createUser(String username, String email, String password, String firstName,
                           String lastName, int age, Wishlist wishlist, ShoppingCart shoppingCart, Role... roles) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        for (Role role : roles) {
            user.addRole(role);
        }

        user.setWishlist(wishlist);
        wishlist.setUser(user);
        user.setShoppingCart(shoppingCart);
        shoppingCart.setUser(user);
        user.setSelectedCurrency(this.currencyRepository.findByCodeIgnoreCase("eur").get());

        return user;
    }

    public void generatePlaceholderReviews() {
        String[] reviewTexts = {
                "A tapestry of intricate storytelling, this book weaves together themes of courage, betrayal, and redemption in a way that is both profound and profoundly human. It's a rare gem that holds up a mirror to the soul, challenging and comforting in equal measure.",
                "This narrative is a masterclass in character development, with each chapter peeling back layers of intrigue and complexity. The author's deft prose and sharp dialogue create a world so tangible, it feels as if you could step into it. A truly immersive experience.",
                "From the relentless pull of the opening line to the satisfying weight of the final sentence, this book is a triumphant exploration of the human condition. It manages to be both wildly imaginative and deeply relatable, a narrative alchemy that's as rare as it is captivating.",
                "With an unflinching voice and a keen eye for the poetic in the everyday, this book delivers a story that resonates with the urgency of modern life. It's a poignant reminder of the ties that bind us across divides, told with a tenderness that is all too real.",
                "This novel is a beautiful paradox, brimming with moments of stark reality and ethereal fantasy. The journey it takes you on is one of introspection and discovery, with a narrative rhythm that beats like a heart full of stories yet to be told.",
                "The author has crafted a world rich with detail, yet it's the characters who steal the show. They are flawed, fierce, and unforgettable. Their victories and defeats echo long after the last page is turned, leaving a lingering promise of more tales to come.",
                "Equal parts harrowing and hopeful, this book doesn't just tell a story—it sings a saga. The intertwining plots are like threads in a grand tapestry, each one vibrant with color and meaning. It's a sweeping epic that manages to be both timeless and timely.",
                "This novel takes the road less traveled, with a narrative that twists and turns through uncharted territories of the mind and heart. It's a deep dive into the unknown waters of storytelling, where each chapter is a stroke that takes you closer to the breathtaking depths.",
                "A blend of suspense and sentiment, this book grips you from the start, refusing to let go as it plunges into the complexities of love, loss, and the quest for truth. It's a literary odyssey that promises—and delivers—a journey for the ages.",
                "The story unfolds with the precision of a clockmaker, each piece slotting into place with satisfying clarity. Yet, it's the clock's face—depicting the nuanced expressions of its characters—that will keep you contemplating the nature of time and legacy long after the final tick."
        };
        String[] reviewTitles = {
                "Reflections in the Mirror of the Soul",
                "Unraveling Intrigue: A Journey Through Character",
                "The Alchemy of Imagination: A Human Saga",
                "Voices Unflinching: The Poetry of Everyday Life",
                "Paradoxical Paths: Between Reality and Fantasy",
                "Echoes of Unforgettable Journeys",
                "Singing Sagas: The Tapestry of Time",
                "Untold Depths: Navigating Narrative Waters",
                "The Odyssey of Emotion: Suspense and Sentiment",
                "The Clockwork of Destiny: Time's Legacy Unfolded"
        };
        LocalDate date = LocalDate.of(2024, 4, 2);
        List<Review> reviews = new ArrayList<>();
        List<Rating> ratings = new ArrayList<>();
        List<User> users = this.userRepository.findFirst10ByOrderByIdAsc();

        for (int i = 0; i < 10; i++) {
            Rating rating = new Rating(users.get(i), date, 5);
            Review review = new Review(users.get(i), reviewTexts[i], date, rating);
            review.setTitle(reviewTitles[i]);

            ratings.add(rating);
            reviews.add(review);
        }

        this.userRepository.saveAll(users);
        this.ratingRepository.saveAll(ratings);
        this.reviewRepository.saveAll(reviews);
    }

    public void generateScheduledTasks() {
        LocalDate date = LocalDate.of(2024, 4, 2);
        ScheduledTaskAudit task1 = new ScheduledTaskAudit("FxRateUpdate", date);
        this.scheduledTaskAuditRepository.save(task1);

        ScheduledTaskAudit task2 = new ScheduledTaskAudit("DatabaseBackup", date);
        this.scheduledTaskAuditRepository.save(task2);
    }
}

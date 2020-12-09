package io.github.nandandesai.base.configs;

import io.github.nandandesai.base.models.Book;
import io.github.nandandesai.base.models.Role;
import io.github.nandandesai.base.models.User;
import io.github.nandandesai.base.repositories.BookRepository;
import io.github.nandandesai.base.repositories.RoleRepository;
import io.github.nandandesai.base.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Configuration
public class BookShelfConfig {
    private Logger logger = LoggerFactory.getLogger(BookShelfConfig.class);

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void createFileStorageDirectories() {
        String currentDirectory = System.getProperty("user.dir");
        String userDataRootPath = currentDirectory + File.separator + "userdata";
        try {
            File userDataDir = new File(userDataRootPath);
            if (!userDataDir.isDirectory()) {
                Path userDataPath = Paths.get(userDataRootPath);
                Files.createDirectories(userDataPath);
                logger.info("userdata directory has been created");

                String userPhotoDir = userDataRootPath + File.separator + "userphotos";
                Path userPhotoDirPath = Paths.get(userPhotoDir);
                Files.createDirectories(userPhotoDirPath);
                logger.info("userphotos directory has been created");

                String booksDir = userDataRootPath + File.separator + "books";
                Path booksDirPath = Paths.get(booksDir);
                Files.createDirectories(booksDirPath);
                logger.info("books directory has been created");

                String bookCoversDir = booksDir + File.separator + "covers";
                Path bookCoversDirPath = Paths.get(bookCoversDir);
                Files.createDirectories(bookCoversDirPath);
                logger.info("covers directory has been created");

                String bookPdfsDir = booksDir + File.separator + "pdfs";
                Path bookPdfsDirPath = Paths.get(bookPdfsDir);
                Files.createDirectories(bookPdfsDirPath);
                logger.info("pdfs directory has been created");

                String initResourcesPrefix="classpath:initres" + File.separator;
                Resource pdf1=resourceLoader.getResource(initResourcesPrefix+"LittleBrother.pdf");
                Resource pdf2=resourceLoader.getResource(initResourcesPrefix+"TheFutureOfTheInternet.pdf");
                Resource cover1=resourceLoader.getResource(initResourcesPrefix+"LittleBrother.jpg");
                Resource cover2=resourceLoader.getResource(initResourcesPrefix+"TheFutureOfTheInternet.jpg");
                Resource defaultAvatar=resourceLoader.getResource(initResourcesPrefix+"default-avatar.png");


                UserDataPaths userDataPaths = getUserDataPaths();
                Files.copy(pdf1.getInputStream(), Paths.get(userDataPaths.getBookPdfsPath()+pdf1.getFilename()), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(pdf2.getInputStream(), Paths.get(userDataPaths.getBookPdfsPath()+pdf2.getFilename()), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(cover1.getInputStream(), Paths.get(userDataPaths.getBookCoversPath()+cover1.getFilename()), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(cover2.getInputStream(), Paths.get(userDataPaths.getBookCoversPath()+cover2.getFilename()), StandardCopyOption.REPLACE_EXISTING);
                logger.info("initial PDFs and Covers copied to their respective dirs.");

                Files.copy(defaultAvatar.getInputStream(), Paths.get(userDataPaths.getUserPhotoDirPath()+defaultAvatar.getFilename()), StandardCopyOption.REPLACE_EXISTING);
                logger.info("default avatar copied to it's respective dir.");

                Role FreeRole = roleRepository.findById("Free").get();
                Role PremiumRole = roleRepository.findById("Premium").get();


                Book book1 = new Book();
                book1.setTitle("Little Brother")
                        .setDescription("Little Brother is a novel by Cory Doctorow, published by Tor Books. It was released on April 29, 2008. The novel is about four teenagers in San Francisco who, in the aftermath of a terrorist attack on the San Francisco–Oakland Bay Bridge and BART system, defend themselves against the Department of Homeland Security's attacks on the Bill of Rights.")
                        .setPdfFileName("LittleBrother.pdf")
                        .setCoverPhotoName("LittleBrother.jpg")
                        .setRole(FreeRole);
                bookRepository.save(book1);

                Book book2 = new Book();
                book2.setTitle("The Future of the Internet and How to Stop It")
                        .setDescription("The Future of the Internet and How to Stop It is a book published in 2008 by Yale University Press and authored by Jonathan Zittrain. The book discusses several legal issues regarding the Internet.")
                        .setPdfFileName("TheFutureOfTheInternet.pdf")
                        .setCoverPhotoName("TheFutureOfTheInternet.jpg")
                        .setRole(PremiumRole);
                bookRepository.save(book2);

                logger.info("initial PDFs and Covers metadata added to the database.");

                /*
                * Initialize admin and a user
                * */
                Role adminRole = roleRepository.findById("Admin").get();

                User freeUser = new User();
                freeUser.setProfilePicName("default-avatar.png")
                        .setRole(FreeRole)
                        .setLastSeen(LocalDateTime.now())
                        .setFullName("User")
                        .setEmail("user")
                        .setPassword(passwordEncoder.encode("user"));
                userRepository.save(freeUser);

                User admin = new User();
                admin.setProfilePicName("default-avatar.png")
                        .setRole(adminRole)
                        .setLastSeen(LocalDateTime.now())
                        .setFullName("Admin")
                        .setEmail("admin")
                        .setPassword(passwordEncoder.encode("admin"));
                userRepository.save(admin);

                logger.info("initialized 'admin' and 'user' users.");

            } else {
                logger.info("userdata directory already exists");
            }
            logger.info("app is now ready to use!");
        } catch (IOException e) {
            logger.error("Error creating userdata directory");
            e.printStackTrace();
            int exitCode = SpringApplication.exit(appContext, new ExitCodeGenerator() {
                @Override
                public int getExitCode() {
                    return 1;
                }
            });
            System.exit(exitCode);
        }
    }

    @Bean
    public UserDataPaths getUserDataPaths() {
        String currentDirectory = System.getProperty("user.dir");
        String userDataRootPath = currentDirectory + File.separator + "userdata";
        String userPhotoDir = userDataRootPath + File.separator + "userphotos" + File.separator;
        String booksDir = userDataRootPath + File.separator + "books";
        String bookCoversDir = booksDir + File.separator + "covers" + File.separator;
        String bookPdfsDir = booksDir + File.separator + "pdfs" + File.separator;
        return new UserDataPaths()
                .setUserPhotoDirPath(userPhotoDir)
                .setBookCoversPath(bookCoversDir)
                .setBookPdfsPath(bookPdfsDir)
                .setCurrentJarPath(currentDirectory + File.separator);
    }
}

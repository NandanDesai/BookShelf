package io.github.nandandesai.insecure.services;

import io.github.nandandesai.insecure.configs.UserDataPaths;
import io.github.nandandesai.insecure.dto.BookDto;
import io.github.nandandesai.insecure.dto.PDF;
import io.github.nandandesai.insecure.dto.Photo;
import io.github.nandandesai.insecure.dto.requests.AddBookRequest;
import io.github.nandandesai.insecure.exceptions.InternalServerException;
import io.github.nandandesai.insecure.exceptions.ResourceNotFoundException;
import io.github.nandandesai.insecure.models.Book;
import io.github.nandandesai.insecure.models.Role;
import io.github.nandandesai.insecure.repositories.BookRepository;
import io.github.nandandesai.insecure.repositories.RoleRepository;
import io.github.nandandesai.insecure.utils.FileOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDataPaths userDataPaths;

    public List<BookDto> getAllBooks() throws InternalServerException {
        try {
            return BookDto.getBookDtoListFromBooks(bookRepository.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
    }

    public BookDto getBook(Integer id) throws ResourceNotFoundException, InternalServerException {
        Optional<Book> bookOptional = null;
        try {
            bookOptional = bookRepository.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
        if(!bookOptional.isPresent()){
            throw new ResourceNotFoundException("book with id '"+id+"' not found");
        }
        return BookDto.getBookDtoFromBook(bookOptional.get());
    }


    @PreAuthorize("hasAuthority('Admin')")
    public boolean deleteBook(Integer id) throws ResourceNotFoundException, InternalServerException {
        Optional<Book> bookOptional = null;
        Book book = null;
        try {
            bookOptional = bookRepository.findById(id);

            if(!bookOptional.isPresent()){
                throw new ResourceNotFoundException("book with id '"+id+"' not found");
            }
            book = bookOptional.get();
            bookRepository.delete(book);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
        try {
            FileOperation.deleteFile(userDataPaths.getBookPdfsPath(), book.getPdfFileName());
            FileOperation.deleteFile(userDataPaths.getBookCoversPath(), book.getCoverPhotoName());
            return true;
        }catch (IOException e){
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
    }

    public List<BookDto> searchBook(String keyword) throws InternalServerException {
        List<Book> books = null;
        try {
            books = bookRepository.findByTitleIgnoreCaseContaining(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
        return BookDto.getBookDtoListFromBooks(books);
    }

    @PreAuthorize("hasAuthority('Admin')")
    public BookDto addBook(AddBookRequest addBookRequest) throws ResourceNotFoundException, InternalServerException {
        Book book = new Book();
        book.setTitle(addBookRequest.getTitle())
                .setDescription(addBookRequest.getDesc());
        Optional<Role> optionalRole = null;
        try {
            optionalRole = roleRepository.findById(addBookRequest.getRole());
            if(!optionalRole.isPresent()){
                throw new ResourceNotFoundException("role: "+ addBookRequest.getRole()+" not found");
            }
            book.setRole(optionalRole.get());
            bookRepository.save(book);

            MultipartFile pdfFile = addBookRequest.getPdfFile();
            String pdfFileName = book.getId()+pdfFile.getOriginalFilename();
            try {
                boolean result = FileOperation.writeFile(userDataPaths.getBookPdfsPath(), pdfFileName, pdfFile.getBytes());
                if (!result) {
                    throw new InternalServerException("file with the name '" + pdfFileName + "' already exists");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new InternalServerException(e.getMessage());
            }
            book.setPdfFileName(pdfFileName);

            MultipartFile photo = addBookRequest.getPhoto();
            String photoName = book.getId()+photo.getOriginalFilename();
            try {
                boolean result = FileOperation.writeFile(userDataPaths.getBookCoversPath(), photoName, photo.getBytes());
                if (!result) {
                    throw new InternalServerException("file with the name '" + photoName + "' already exists");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new InternalServerException(e.getMessage());
            }
            book.setCoverPhotoName(photoName);
            bookRepository.save(book);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BookDto.getBookDtoFromBook(book);
    }

    public Photo getCoverPhoto(Integer bookId) throws InternalServerException, ResourceNotFoundException {
        Optional<Book> bookOptional = null;
        try {
            bookOptional = bookRepository.findById(bookId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!bookOptional.isPresent()){
            throw new ResourceNotFoundException("book with ID: "+bookId+" not found");
        }
        Book book = bookOptional.get();
        String photoName = book.getCoverPhotoName();
        try {
            byte[] bytes = FileOperation.readFile(userDataPaths.getBookCoversPath(), photoName);

            //need to determine the image type here and set the mime type accordingly
            //temporarily setting the mime type to image/jpg

            return new Photo().setPhotoBytes(bytes)
                    .setMimeType("image/jpg");
        } catch (NoSuchFileException e) {
            String message = "No photo with the name '" + photoName + "' found";
            logger.error(message);
            throw new ResourceNotFoundException(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
    }

    //making this as PostAuthorize so that it goes through the ResourceNotFound checks
    //otherwise ResourceNotFoundException don't get caught when thrown from BookPdfAccessCheck class.
    @PostAuthorize("@bookPdfAccessCheck.verify(#bookId, authentication.principal.grantedAuthorities[0].userId)")
    public PDF getPdf(Integer bookId) throws ResourceNotFoundException, InternalServerException {
        Optional<Book> bookOptional = null;
        try {
            bookOptional = bookRepository.findById(bookId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
        if(!bookOptional.isPresent()){
            throw new ResourceNotFoundException("book with ID: "+bookId+" not found");
        }
        Book book = bookOptional.get();
        String pdfName = book.getPdfFileName();
        try {
            byte[] bytes = FileOperation.readFile(userDataPaths.getBookPdfsPath(), pdfName);
            return new PDF().setPdfBytes(bytes)
                    .setMimeType("application/pdf");
        } catch (NoSuchFileException e) {
            String message = "No pdf with the name '" + pdfName + "' found";
            logger.error(message);
            throw new ResourceNotFoundException(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
    }

}

package second_project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import second_project.models.Book;
import second_project.models.Person;

import java.util.List;

public interface BookService {
    public Book getBookById (int id);

    public List<Book> getAllBooks ();
   // public Iterable<Book> findPage(int pageNumber, int itemsOnPage); //todo check the efficient of this one
    public Iterable<Book> getAllBooks (int pageNumber, int itemsOnPage);
    public void addBook (Book book);
    public void updateBook (int id, Book updatedBook);
    public void deleteBook (int id);
    public Person getOwner(int bookId);
    public void releaseBookFromTheOwner(int bookId);
    public void setOwnerForBook(int bookId, Person person);

}

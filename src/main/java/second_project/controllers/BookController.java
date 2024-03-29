package second_project.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import second_project.models.Book;
import second_project.models.Person;
import second_project.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import second_project.service.PersonService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {
    private final BookService bookService;
    private final PersonService personService;


    @Autowired
    public BookController(BookService bookService, PersonService personService) {
        this.bookService = bookService;
        this.personService = personService;
    }

    @GetMapping("/books/new")
    public String newBook(@ModelAttribute("newBook") Book book) {
        return "book/add-new-book";
    }

    @PostMapping("/create-new-book")
    public String createNewBook(@ModelAttribute("newBook") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "book/add-new-book";
        }
        bookService.addBook(book);
        return "redirect:/books";
    }

    @GetMapping("/books/{id}")
    public String showBookInfo(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        //
        Optional<Person> bookOwner = Optional.ofNullable(bookService.getOwner(id));
        if (bookOwner.isPresent()) {
            model.addAttribute("owner", bookOwner.get());
        } else {
            model.addAttribute("people", personService.getAllPeople());
        }
        return "book/book-info";
    }

    @GetMapping("/books/{id}/edit")
    public String updateBookInfo(Model model, @PathVariable("id") int id) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "book/update-book";
    }

    @PatchMapping("/books/{id}")
    public String executeUpdatingPersonInfo(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult, @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            // book.setId(id); //todo
            return "book/update-book";
        }
        bookService.updateBook(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/delete-book/{id}")
    public String deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }

    @PatchMapping("/set-book-free/{id}")
    public String makeBookFree(@PathVariable int id) {
        // Book book = bookService.getBookById(id);
        bookService.releaseBookFromTheOwner(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/set-book-owner/{id}")
    public String setBookOwner(@PathVariable int id, @ModelAttribute("person") Person selectedPerson) {
        System.out.println(selectedPerson.getId());
        bookService.setOwnerForBook(id, selectedPerson);
        return "redirect:/books/" + id;

    }


    @GetMapping("/books/execute-searching")
    public String showResultOfSearchingBook(@RequestParam(value = "typedString") String typedString, Model model) {
        List<Book> books = bookService.searchBookByTitle(typedString);
        model.addAttribute("books", books);
        return "book/search-book";
    }

    @GetMapping("/books")
    public String getAllPagesWithBooks(Model model, @RequestParam(value = "sortDir", required = false) String sortDir) {
        return getOnePageWithBooks(model, 1);
    }

    @GetMapping("/books/page/{pageNumber}") // todo
    public String getOnePageWithBooks(Model model, @PathVariable(value = "pageNumber") int currentPage) {
        if (currentPage == 0) {
            bookService.getAllBooks();
        }

        Page<Book> page = bookService.findPage(currentPage);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<Book> books = page.getContent();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("books", books);

        return "/book/books-pagination";
    }

    @GetMapping("/books/page/{pageNumber}/sort")
    public String getOnePageWithSortingBooks(Model model, @PathVariable("pageNumber") int currentPage,
                                             @RequestParam(value = "sortDir", required = false) String sortDir) {

        Page<Book> page = bookService.findBooksWithSorting(sortDir, currentPage);
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<Book> books = page.getContent();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("books", books);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        return "/book/books-pagination";

    }
}
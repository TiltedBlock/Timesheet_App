package rit_task.timesheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rit_task.timesheet.repositories.TimeEntryRepository;
import rit_task.timesheet.repositories.UserRepository;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
public class MainController {

    @Autowired
    TimeEntryRepository timeEntryRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = { "/create"}, method = RequestMethod.POST)
    public ResponseEntity create(@Valid @RequestBody TimeEntry timeEntryInput) {

        if (isDateInvalid(timeEntryInput.getDate())) {
            return ResponseEntity.badRequest().body("Enter a valid date");
        }

        TimeEntry timeEntry = new TimeEntry(timeEntryInput);
        // Use constructor to create unique ID for the entry

        timeEntryRepository.save(timeEntry).subscribe();
        return new ResponseEntity<Mono<TimeEntry>>(timeEntryRepository.findById(timeEntry.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(value = {"/createuser"}, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createUser(@RequestBody UserDTO dto) {

        User newUser = new User(dto.getUsername(), new BCryptPasswordEncoder().encode(dto.getPassword()));
        userRepository.save(newUser).subscribe();

    }

    @RequestMapping(value = { "/update/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity update(@PathVariable("id") String id, @Valid @RequestBody TimeEntry timeEntryInput) {

        if (isDateInvalid(timeEntryInput.getDate())) {
            return ResponseEntity.badRequest().body("Enter a valid date");
        }

        timeEntryInput.setId(id);

        timeEntryRepository.save(timeEntryInput).subscribe();
        return new ResponseEntity<Mono<TimeEntry>>(timeEntryRepository.findById(timeEntryInput.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = { "/{id}"}, method = RequestMethod.GET)
    public ResponseEntity<Mono<TimeEntry>>  findById(@PathVariable String id) {
        return new ResponseEntity<Mono<TimeEntry>>(timeEntryRepository.findById(id), HttpStatus.OK);
    }

    @RequestMapping(value = {"/date/{date}"}, method = RequestMethod.GET)
    public Flux<TimeEntry> filterByDate(@PathVariable String date) {
        return this.timeEntryRepository.findAllByDate(date);
    }

    @RequestMapping(value = {"/category/{category}"}, method = RequestMethod.GET)
    public Flux<TimeEntry> filterByCategory(@PathVariable String category) {

        // Make sure capitalization of the input doesn't matter
        category = category.toLowerCase();
        char[] categoryAsChars = category.toCharArray();
        categoryAsChars[0] = Character.toUpperCase(categoryAsChars[0]);
        category = String.valueOf(categoryAsChars);

        return this.timeEntryRepository.findAllByCategory(category);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") String id) {
        this.timeEntryRepository.deleteById(id).subscribe();
    }

    private boolean isDateInvalid(String dateString) {

        try {
            LocalDate testDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yy"));
            return false;
        } catch (DateTimeParseException e) {
            return true;
        }

    }



}

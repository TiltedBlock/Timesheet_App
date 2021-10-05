package rit_task.timesheet.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rit_task.timesheet.TimeEntry;

@Repository
public interface TimeEntryRepository extends ReactiveMongoRepository<TimeEntry, String> {

    @Query
    Mono<TimeEntry> findById(String id);

    @Query
    Flux<TimeEntry> findAllByDate(String date);

    @Query
    Flux<TimeEntry> findAllByCategory(String category);

}

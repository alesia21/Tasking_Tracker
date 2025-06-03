package com.example.tasking_tracker.entity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecifications {


    //Returning a specification that filter tasks by their status
    //If status is null , no filtering is applied, returns null.
    public static Specification<Task> hasStatus(String status) {
        return (root, query, cb) ->  //building a predicate using lambda exp;
                //the root represent the entity being queried in this case Task , when calling root.get(status) we get the status field on the task table
                //query-> jpa criteriaquery<task> / object under construction. using query to alter which columns are selected.
                //cb - criteria builder, inclung methods like equal comparisons, combining conditions
                //if status is null returing null means no filtering
                //otherwise create a predicate where root.status equals the given status

                status == null ? null
                        : cb.equal(root.get("status"), status);
    }
//Same as status
    public static Specification<Task> hasPriority(String priority) {
        return (root, query, cb) -> priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> dueBefore(LocalDate date) {
        return (root, query, cb) -> date == null ? null : cb.lessThanOrEqualTo(root.get("dueDate"), date);
    }
}

package com.ituwei.crud.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Entity
@Table(name ="todo_item")
@NoArgsConstructor
public class TodoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private long id;
    @Getter
    @Setter
    @NotBlank(message = "Description is required")
    private String description;
    @Getter
    @Setter
    private Boolean complete;
    @Getter
    @Setter
    private Instant createdDate;
    @Getter
    @Setter
    private Instant modifiedDate;

    public TodoItem(String description, Boolean complete, Instant createdDate, Instant modifiedDate) {
        this.description = description;
        this.complete = false;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String toString() {
        return  String.format("TodoItem{id=%d, description='%s', complete='%s', createdDate='%s', modifiedDate='%s'}",
                id, description, complete, createdDate, modifiedDate);
    }
}

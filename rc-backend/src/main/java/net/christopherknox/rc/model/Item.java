package net.christopherknox.rc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Integer id;
    private String title;
    private String category;
    private Priority priority;
    private LocalDate added;
    private LocalDate completed;
}

package net.christopherknox.rc.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Item {
    private Integer id;
    private String title;
    private String category;
    private LocalDateTime added;
    private LocalDateTime completed;
    private Integer priority;
}

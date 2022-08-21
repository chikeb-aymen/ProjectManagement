package com.programming.projectservice.mappers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SprintMapper {

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Start Date cannot be null")
    private Date startDate;

    @NotNull(message = "End Date cannot be null")
    private Date endDate;
}

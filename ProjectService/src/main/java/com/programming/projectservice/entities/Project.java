package com.programming.projectservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
public class Project {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    //TODO enum
    private String type;

    private Long leadUser;

    //TODO S3 amazon
    private String icon;

    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;

    //Users who work in this project are in the third microservices (UserProjectService)


    @OneToMany(mappedBy = "project")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<Sprint> sprints;

}

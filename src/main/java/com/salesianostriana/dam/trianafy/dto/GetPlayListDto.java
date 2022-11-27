package com.salesianostriana.dam.trianafy.dto;

import com.salesianostriana.dam.trianafy.model.Playlist;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
public class GetPlayListDto {

    @GeneratedValue
    @Id
    private Long id;
    private String name;
    private int numberOfSongs;

}

package com.salesianostriana.dam.trianafy.dto;

import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import lombok.*;


@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetSongDto {


    private Long id;
    private String title;
    private String album;
    private String artist;
    private String year;



}

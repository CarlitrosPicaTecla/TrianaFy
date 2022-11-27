package com.salesianostriana.dam.trianafy.dto;

import com.salesianostriana.dam.trianafy.model.Song;
import org.springframework.stereotype.Component;

@Component
public class SongDtoConverter {

    public Song createSongDtoToSong(CreateSongDto c){

        return new Song(
                c.getTitle(),
                c.getAlbum(),
                c.getYear()
        );

    }

    public GetSongDto createSongToSongDto(Song s){
        return GetSongDto
                .builder()
                .id(s.getId())
                .title(s.getTitle())
                .artist(s.getArtist().getName())
                .album(s.getAlbum())
                .year(s.getYear())
                .build();
    }
}

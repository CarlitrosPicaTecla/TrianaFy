package com.salesianostriana.dam.trianafy.dto;

import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Builder
@Component
public class PlayListDtoConverter {

    public Playlist createPlayListDtoToPlayList(CreatePlayListDto c){

        List<Song>  songs = new ArrayList<>();
        return  Playlist.builder()
                .name(c.getName())
                .description(c.getDescription())
                .songs(songs)
                .build();


    }


    public GetPlayListDto playListToPlayListResponse(Playlist p){
        return GetPlayListDto
                .builder()
                .id(p.getId())
                .name(p.getName())
                .numberOfSongs(p.getSongs().size())
                .build();

    }
    public GetSongDto songToSongResponse(Song song) {
        return GetSongDto
                .builder()
                .id(song.getId())
                .title(song.getTitle())
                .artist(song.getArtist().getName())
                .album(song.getAlbum())
                .year(song.getYear())
                .build();
    }


}

package com.salesianostriana.dam.trianafy.controller;


import com.salesianostriana.dam.trianafy.dto.CreateSongDto;
import com.salesianostriana.dam.trianafy.dto.GetSongDto;
import com.salesianostriana.dam.trianafy.dto.SongDtoConverter;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor


public class SongController {


    private final SongDtoConverter songDtoConverter;
    private final ArtistService artistService;


    @Autowired
    private final SongService songService;

    @GetMapping("/songs/")
    public ResponseEntity <List<GetSongDto>> getSong(){

        List<Song> songs= songService.findAll();

        if(songs.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        else {
            List<GetSongDto> result= songs.stream().map(s -> songDtoConverter.createSongToSongDto(s))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        }

    }
    @GetMapping("/songs/{id}")
    public ResponseEntity<GetSongDto> getSongById(@PathVariable Long id){
        if (songService.findById(id).isPresent()) {
            return ResponseEntity.ok(songDtoConverter.createSongToSongDto(songService.findById(id).get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/songs/")
    public ResponseEntity<GetSongDto>  addSong(@RequestBody CreateSongDto dto){
        if (dto.getArtistId()==null){
            return ResponseEntity.badRequest().build();
        }
        else{

            Song nuevo= songDtoConverter.createSongDtoToSong(dto);

            Artist artist = artistService.findById(dto.getArtistId()).get();
            nuevo.setArtist(artist);

            GetSongDto nuevoConvert = songDtoConverter.createSongToSongDto(songService.add(nuevo));

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevoConvert);
        }

    }

    @PutMapping("/songs/{id}")
    public ResponseEntity<GetSongDto> editSong(@RequestBody CreateSongDto song, @PathVariable Long id){


        if (song.getArtistId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (song.getArtistId() != null ) {

            Song nuevo = songDtoConverter.createSongDtoToSong(song);
            Artist artist = artistService.findById(song.getArtistId()).get();
            nuevo.setArtist(artist);
            nuevo.setId(id);
            return ResponseEntity.ok().body(songDtoConverter.createSongToSongDto(songService.edit(nuevo)));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/songs/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id){
        if (songService.findById(id).isPresent()) {
            songService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}

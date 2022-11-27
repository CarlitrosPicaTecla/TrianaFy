package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.dto.CreatePlayListDto;
import com.salesianostriana.dam.trianafy.dto.GetPlayListDto;
import com.salesianostriana.dam.trianafy.dto.GetSongDto;
import com.salesianostriana.dam.trianafy.dto.PlayListDtoConverter;
import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
import com.salesianostriana.dam.trianafy.repos.PlaylistRepository;
import com.salesianostriana.dam.trianafy.service.PlaylistService;
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

public class PlayListController {

    private final PlayListDtoConverter playListDtoConverter;

    private final SongService songService;

    @Autowired
    private PlaylistService playlistService;

    @GetMapping("/playlist/")
    public ResponseEntity<List<GetPlayListDto>> getPlayList(){
        List<Playlist> data = playlistService.findAll();

        if(data.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        else {
            List<GetPlayListDto> result =
                    data.stream()
                            .map(playListDtoConverter::playListToPlayListResponse)
                            .collect(Collectors.toList());

            return ResponseEntity
                    .ok()
                    .body(result);
        }


    }

    @GetMapping("/playlist/{id}")
    public ResponseEntity<Playlist> getPlayListById(@PathVariable Long id){
        if (playlistService.findById(id).isPresent()) {
            return ResponseEntity.ok(playlistService.findById(id).get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @PostMapping("/playlist/")
    public ResponseEntity<Playlist> addPlayList(@RequestBody CreatePlayListDto dto){
        if (dto.getName() != "") {


            Playlist nuevo = playListDtoConverter.createPlayListDtoToPlayList(dto);

            playlistService.add(nuevo);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nuevo);
        }
        return ResponseEntity.badRequest().build();

    }

    //no se yo
    @PutMapping("/playlist/{id}")
    public ResponseEntity<GetPlayListDto> editPlayList(@RequestBody CreatePlayListDto dto, @PathVariable Long id){

        if (!playlistService.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (dto.getName() == "") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        }
            Playlist nuevo = playListDtoConverter.createPlayListDtoToPlayList(dto);

            return ResponseEntity.ok().body( playListDtoConverter.playListToPlayListResponse(playlistService.edit(nuevo)));
    }

    @DeleteMapping("/playlist/{id}")
    public ResponseEntity<?> deletePlayList(@PathVariable Long id){

        if(playlistService.findById(id).isPresent()){

            playlistService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();


    }

    //---------//


    @GetMapping("/playlist/{id}/songs")
    public ResponseEntity<Playlist> getSongOfPlayList(@PathVariable Long id){
        if (playlistService.findById(id).isPresent()) {
            return ResponseEntity.ok(playlistService.findById(id).orElse(null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/playlist/{id1}/songs/{id2}")
    public ResponseEntity<GetSongDto> getSongOfPlayList(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2){
        if (playlistService.findById(id1).isPresent() && songService.findById(id2).isPresent()) {
            return ResponseEntity.ok(playListDtoConverter.songToSongResponse(songService.findById(id2).get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/playlist/{id1}/songs/{id2}")
    public ResponseEntity<Playlist> addSongList(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2){
        if(playlistService.findById(id1).isPresent() && songService.findById(id2).isPresent()){
            Song song = songService.findById(id2).orElse(null);
            playlistService.findById(id1).get().getSongs().add(song);
            return ResponseEntity.ok(playlistService.findById(id1).get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @DeleteMapping("/playlist/{id1}/songs/{id2}")
    public ResponseEntity<?> removeSongOfList( @PathVariable("id1") Long id1, @PathVariable("id2") Long id){

        Song s = songService.findById(id).get();

        if (playlistService.findById(id1).isPresent() && songService.findById(id).isPresent()
                && playlistService.findById(id1).get().getSongs().contains(s)) {
            playlistService.findById(id1).get().getSongs().remove(s);
            playlistService.edit(playlistService.findById(id1).get());
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}


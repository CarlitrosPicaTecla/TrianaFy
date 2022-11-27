package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ArtistController {

    private final SongService songService;

    @Autowired
    private final ArtistService artistService;
    @GetMapping("/artist/")
    public ResponseEntity <List<Artist>> getArtist(){

        if (artistService.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(artistService.findAll());
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable Long id){

        if (!artistService.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }

        return ResponseEntity.of(artistService.findById(id));

    }

    @PostMapping("/artist/")
    public ResponseEntity<Artist> addArtist(@RequestBody Artist a){

        if (a.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.add(a));
    }

    @PutMapping("/artist/{id}")
    public ResponseEntity<Artist> editArtist(@RequestBody Artist artist, @PathVariable Long id){

        if (!artistService.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.of(artistService.findById(id).map(a ->{
            a.setName(artist.getName());
            artistService.edit(a);
            return a;
        }));
    }

    @DeleteMapping("/artist/{id}")
    public  ResponseEntity<Artist> deleteArtist(@PathVariable Long id){
        if (artistService.findById(id).isPresent()) {
            Artist a = artistService.findById(id).get();
            songService.findAll().stream().filter(song -> song.getArtist().equals(a)).forEach(song -> song.setArtist(null));
            artistService.deleteById(id);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}

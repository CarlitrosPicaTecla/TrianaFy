package com.salesianostriana.dam.trianafy.controller;


import com.salesianostriana.dam.trianafy.dto.CreateSongDto;
import com.salesianostriana.dam.trianafy.dto.GetSongDto;
import com.salesianostriana.dam.trianafy.dto.SongDtoConverter;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.service.ArtistService;
import com.salesianostriana.dam.trianafy.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Devuelve una lista de todas las canciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Canciones encontradas",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Artist.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                            { "id": 1, "title": "HeartBreaker", "artist": "Led Zeppelin", "album" : "Led Zeppelin II","year": 1970},
                                            { "id": 2, "title": "Immigrant Song", "artist": "Led Zeppelin", "album" : "Led Zeppelin III","year": 1972},                
                                            { "id": 3, "title": "War Pigs", "artist": "Black Sabbath", "album" : "Black Sabbath","year": 1969}                   
                                            ]
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado niguna cancion",
                    content = @Content)})
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
    @Operation(summary = "Devuelve una cancion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancion encontrada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = {@ExampleObject(
                                    value = """                                            
                                            { "id": 3, "title": "War Pigs", "artist": "Black Sabbath", "album" : "Black Sabbath","year": 1969}                   
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado ninguna cancion con ese ID",
                    content = @Content)})
    @GetMapping("/songs/{id}")
    public ResponseEntity<GetSongDto> getSongById(@PathVariable Long id){
        if (songService.findById(id).isPresent()) {
            return ResponseEntity.ok(songDtoConverter.createSongToSongDto(songService.findById(id).get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @Operation(summary = "Añade una nueva cancion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cancion Creada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = {@ExampleObject(
                                    value = """                                            
                                            { "id": 4, "title": "National Acrobat", "artist": "Black Sabbath", "album" : "Sabbath Bloody Sabbath","year": 1972}   
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400", description = "Los datos de creacion de la cancion no son correctos",
                    content = @Content)})
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
    @Operation(summary = "Edita a una cancion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancion Editada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = {@ExampleObject(
                                    value = """                                            
                                            { "id": 4, "title": "She lose control", "artist": "Joy Division", "album" : "Unknow Pleasures","year": 1979}   
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400", description = "No se encuentra la cancion a editar",
                    content = @Content)})
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
    @Operation(summary = "Elimina una cancion por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cancion eliminado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class)
                    )})
    })
    @DeleteMapping("/songs/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Long id){
        if (songService.findById(id).isPresent()) {
            songService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}

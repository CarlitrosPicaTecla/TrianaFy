package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.dto.CreatePlayListDto;
import com.salesianostriana.dam.trianafy.dto.GetPlayListDto;
import com.salesianostriana.dam.trianafy.dto.GetSongDto;
import com.salesianostriana.dam.trianafy.dto.PlayListDtoConverter;
import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.model.Playlist;
import com.salesianostriana.dam.trianafy.model.Song;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
import com.salesianostriana.dam.trianafy.repos.PlaylistRepository;
import com.salesianostriana.dam.trianafy.service.PlaylistService;
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

public class PlayListController {

    private final PlayListDtoConverter playListDtoConverter;

    private final SongService songService;

    @Autowired
    private PlaylistService playlistService;
    @Operation(summary = "Devuelve una lista de todas las playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PlayLists encontradas",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Artist.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                            { "id": 1, "name": "Rock 70/80", "numberOfSongs": "25"},
                                            { "id": 2, "name": "Disco Funk", "numberOfSongs": "18"}                
                                            ]
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado niguna playList",
                    content = @Content)})
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
    @Operation(summary = "Devuelve los datos de una playlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PlayList encontrada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = {@ExampleObject(
                                    value = """                                            
                                            {   "id": 1,
                                                "name": "Rock 70/80",
                                                "description": "Para el autobus",
                                                "songs": [
                                                  { "id": 2, "title": "Immigrant Song",
                                                    "artist": "Led Zeppelin",
                                                    "album" : "Led Zeppelin III", "year": 1972},
                                                  { "id": 3, "title": "Another song",
                                                    "artist": "Another Artist name",
                                                    "album" : "Another album", "year": 2020},
                                                 ...
                                                ]
                                             }
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado ninguna playlist con ese ID",
                    content = @Content)})
    @GetMapping("/playlist/{id}")
    public ResponseEntity<Playlist> getPlayListById(@PathVariable Long id){
        if (playlistService.findById(id).isPresent()) {
            return ResponseEntity.ok(playlistService.findById(id).get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Crea una playlist ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "PlayList creada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Playlist.class),
                            examples = {@ExampleObject(
                                    value = """
                                            {    "id": 13,
                                                 "name": "rock",
                                                 "description": "Para el bus",
                                                 "songs": []}
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400", description = " Los datos para crear la playlist son incorrectos",
                    content = @Content)})
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

    @Operation(summary = "Edita una playlist por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Playlist editada correctamente",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Playlist.class),
                            examples = {@ExampleObject(
                                    value = """
                                            {    "id": 13,
                                                 "name": "nombre nuevo",
                                                 "numberOfSongs": 0}
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400", description = " Se han introducido los datos incorrectamente",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encuentra la playlist",
                    content = @Content)})
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

    @Operation(summary = "Elimina una playlist por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PlayList eliminada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Playlist.class),
                            examples = {@ExampleObject(
                                    value = """
                                            {}
                                            """
                            )}
                    )})
    })
    @DeleteMapping("/playlist/{id}")
    public ResponseEntity<?> deletePlayList(@PathVariable Long id){

        if(playlistService.findById(id).isPresent()){

            playlistService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();


    }

    //---------//

    @Operation(summary = "Devuelve todas las canciones de una playlist por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Playlist con canciones encontrada",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Playlist.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                            {
                                                "id": 12,
                                                "name": "Random",
                                                "description": "Una lista muy loca",
                                                "songs": [
                                                    {
                                                        "id": 9,
                                                        "title": "Enter Sandman",
                                                        "album": "Metallica",
                                                        "year": "1991",
                                                        "artist": {
                                                            "id": 3,
                                                            "name": "Metallica"
                                                        }
                                                    },
                                                    {
                                                        "id": 8,
                                                        "title": "Love Again",
                                                        "album": "Future Nostalgia",
                                                        "year": "2021",
                                                        "artist": {
                                                            "id": 2,
                                                            "name": "Dua Lipa"
                                                        }
                                                    }]}
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404", description = "No se encontra la playlist",
                    content = @Content)})
    @GetMapping("/playlist/{id}/songs")
    public ResponseEntity<Playlist> getSongOfPlayList(@PathVariable Long id){
        if (playlistService.findById(id).isPresent()) {
            return ResponseEntity.ok(playlistService.findById(id).get());
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
    @Operation(summary = "Introduce una cancion a la lista deseada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Se ha añadido correctamente",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Playlist.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                            {
                                                "id": 12,
                                                "name": "Random",
                                                "description": "Una lista muy loca",
                                                "songs": [
                                                    {
                                                        "id": 9,
                                                        "title": "Enter Sandman",
                                                        "album": "Metallica",
                                                        "year": "1991",
                                                        "artist": {
                                                            "id": 3,
                                                            "name": "Metallica"
                                                        }
                                                    },
                                                    {
                                                        "id": 8,
                                                        "title": "Love Again",
                                                        "album": "Future Nostalgia",
                                                        "year": "2021",
                                                        "artist": {
                                                            "id": 2,
                                                            "name": "Dua Lipa"
                                                        }
                                                    }]                                                   
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404", description = "No se encontrado la lista",
                    content = @Content)})
    @PostMapping("/playlist/{id1}/songs/{id2}")
    public ResponseEntity<Playlist> addSongList(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2){
        if(playlistService.findById(id1).isPresent() && songService.findById(id2).isPresent()){
            Song song = songService.findById(id2).orElse(null);
            playlistService.findById(id1).get().getSongs().add(song);
            return ResponseEntity.ok(playlistService.findById(id1).get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }
    @Operation(summary = "Eliminar una canción de la lista seleccionada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cancion de la lista borrada",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Playlist.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            {}                                                   
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404", description = "No encuentra la playlist",
                    content = @Content)})

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


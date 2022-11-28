package com.salesianostriana.dam.trianafy.controller;

import com.salesianostriana.dam.trianafy.model.Artist;
import com.salesianostriana.dam.trianafy.repos.ArtistRepository;
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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ArtistController {

    private final SongService songService;

    @Autowired
    private final ArtistService artistService;

    @Operation(summary = "Devuelve una lista de todos los artistas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artistas encontrados",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Artist.class)),
                            examples = {@ExampleObject(
                                    value = """
                                            [
                                            {"id": 1, "name": "Led Zeppelin"},
                                            {"id": 2, "name": "Black Sabbath"}                  
                                            ]
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado nigun artista",
                    content = @Content)})
    @GetMapping("/artist/")
    public ResponseEntity <List<Artist>> getArtist(){

        if (artistService.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(artistService.findAll());
    }

    @Operation(summary = "Devuelve un artista por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista encontrado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = {@ExampleObject(
                                    value = """                                            
                                            {"id": 1, "name": "Led Zeppelin"},
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "404", description = "No se ha encontrado un artista con ese ID",
                    content = @Content)})
    @GetMapping("/artist/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable Long id){

        if (!artistService.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }

        return ResponseEntity.of(artistService.findById(id));

    }
    @Operation(summary = "Añade un nuevo artista a la lista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artista Creado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = {@ExampleObject(
                                    value = """                                            
                                            {"id": 3, 
                                            "name": "The Doors"}
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400", description = "Los datos de creacion del artista no son correctos",
                    content = @Content)})
    @PostMapping("/artist/")
    public ResponseEntity<Artist> addArtist(@RequestBody Artist a){

        if (a.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(artistService.add(a));
    }
    @Operation(summary = "Edita a un artista por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista Editado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class),
                            examples = {@ExampleObject(
                                    value = """                                            
                                            {"id": 1, "name": "Joy Division"}
                                            """
                            )}
                    )}),
            @ApiResponse(responseCode = "400", description = "No se encuentra el artista a editar",
                    content = @Content)})
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
    @Operation(summary = "Elimina a un artista por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artista eliminado",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Artist.class)
                    )})
    })
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

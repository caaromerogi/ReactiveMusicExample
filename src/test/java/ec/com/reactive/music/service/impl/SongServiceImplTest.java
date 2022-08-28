package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.AlbumDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.domain.entities.Album;
import ec.com.reactive.music.domain.entities.Song;
import ec.com.reactive.music.repository.ISongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class SongServiceImplTest {
    @Mock
    ISongRepository songRepositoryMock;

    ModelMapper modelMapper;

    SongServiceImpl songService;

    @BeforeEach
    void init(){
        this.modelMapper = new ModelMapper();
        this.songService = new SongServiceImpl(songRepositoryMock,modelMapper);
    }


    @Test
    void findAllSongs() {
        //1. Que tipo de prueba voy a hacer? Exitosa o fallida. - Exitosa -> Resultado: Mono<ResponseEntity<Flux<SongDTO>>>

        //2. Armar el escenario con la respuesta esperada
       ArrayList<Song> songs = new ArrayList<>();
       songs.add(new Song());
       songs.add(new Song());

        ArrayList<SongDTO> listSongsDTO = songs
                .stream()
                .map(song -> modelMapper.map(song,SongDTO.class))
                .collect(Collectors.toCollection(ArrayList::new));

        var fluxResult = Flux.fromIterable(songs);
        var fluxResultDTO = Flux.fromIterable(listSongsDTO);

        //La respuesta esperada
        ResponseEntity<Flux<SongDTO>> respEntResult = new ResponseEntity<>(fluxResultDTO, HttpStatus.FOUND);

        //3. Mockeo - Mockear el resultado esperado
        Mockito.when(songRepositoryMock.findAll()).thenReturn(fluxResult);

        //4. Servicio
        var service = songService.findAllSongs();

        //5. Stepverifier
        StepVerifier.create(service)
                .expectNextMatches(fluxResponseEntity -> fluxResponseEntity.getStatusCode().is3xxRedirection())
                .expectComplete()
                .verify();
    }

    @Test
    void findAllSongsError() {

        //La respuesta esperada
        ResponseEntity<Flux<SongDTO>> respEntResult = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        //3. Mockeo - Mockear el resultado esperado
        Mockito.when(songRepositoryMock.findAll()).thenReturn(Flux.empty());

        //4. Servicio
        var service = songService.findAllSongs();

        //5. Stepverifier
        StepVerifier.create(service)
                .expectNextMatches(fluxResponseEntity -> fluxResponseEntity.getStatusCode().is2xxSuccessful())
                .expectComplete()
                .verify();
    }

    @Test
    void findSongById() {
        Song songExpected = new Song(
                "fdhuchuadsa23",
                "23",
                "vhcixierji3i",
                "zzz",
                "mv",
                "mbz",
                LocalTime.of(0,3,20));

        var songDTOExpected = modelMapper.map(songExpected,SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(songDTOExpected,HttpStatus.FOUND);

        Mockito.when(songRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.just(songExpected));

        var service = songService.findSongById("fdhuchuadsa23");

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        //Si está utilizando lo que yo mockee
        Mockito.verify(songRepositoryMock).findById("fdhuchuadsa23");
    }

    @Test
    void findSongByIdError() { //Not found

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Mockito.when(songRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.empty());

        var service = songService.findSongById("fdhuchuadsa23");

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete().verify();

        Mockito.verify(songRepositoryMock).findById("fdhuchuadsa23");
    }

    @Test
    void saveSong() {
        Song songExpected = new Song(
                "fdhuchuadsa23",
                "23",
                "vhcixierji3i",
                "zzz",
                "mv",
                "mbz",
                LocalTime.of(0,3,20));

        var songDTOExpected = modelMapper.map(songExpected,SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(songDTOExpected,HttpStatus.CREATED);

        Mockito.when(songRepositoryMock.save(Mockito.any(Song.class))).thenReturn(Mono.just(songExpected));

        var service = songService.saveSong(songDTOExpected);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        //Si está utilizando lo que yo mockee
        Mockito.verify(songRepositoryMock).save(songExpected);
    }

    @Test
    void saveSongByIdError() { //Not found
        Song songExpected = new Song(
                "fdhuchuadsa23",
                "23",
                "vhcixierji3i",
                "zzz",
                "mv",
                "mbz",
                LocalTime.of(0,3,20));

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

        Mockito.when(songRepositoryMock.save(Mockito.any(Song.class))).thenReturn(Mono.empty());

        var service = songService.saveSong(modelMapper.map(songExpected, SongDTO.class));

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete().verify();

        Mockito.verify(songRepositoryMock).save(songExpected);
    }


    @Test
    void updateSong() {
        Song songInitial = new Song(
                "fdhuchuadsa23",
                "23",
                "vhcixierji3i",
                "zzz",
                "mv",
                "mbz",
                LocalTime.of(0,3,20));

        var songEdited = songInitial.toBuilder().name("23 de herrera editao").build();

        var songDTOExpected = modelMapper.map(songEdited,SongDTO.class);



        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(songDTOExpected,HttpStatus.ACCEPTED);

        Mockito.when(songRepositoryMock.findById("fdhuchuadsa23")).thenReturn(Mono.just(songInitial));
        Mockito.when(songRepositoryMock.save(modelMapper.map(songDTOExpected, Song.class))).thenReturn(Mono.just(songEdited));

        var service = songService.updateSong("fdhuchuadsa23",songDTOExpected);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        //Si está utilizando lo que yo mockee
        Mockito.verify(songRepositoryMock).save(songEdited);
    }

    @Test
    void updateSongError() {
        Song songInitial = new Song(
                "fdhuchuadsa23",
                "23",
                "vhcixierji3i",
                "zzz",
                "mv",
                "mbz",
                LocalTime.of(0,3,20));

        var songEdited = songInitial.toBuilder().name("23 de herrera editao").build();

        var songDTOExpected = modelMapper.map(songEdited,SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

        Mockito.when(songRepositoryMock.findById("fdhuchuadsa23")).thenReturn(Mono.just(songInitial));

        var service = songService.updateSong("fdhuchuadsa23",songDTOExpected);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        //Si está utilizando lo que yo mockee
        Mockito.verify(songRepositoryMock).save(songEdited);
    }

    @Test
    void deleteSong() {
        Song songInitial = new Song(
                "fdhuchuadsa23",
                "23",
                "vhcixierji3i",
                "zzz",
                "mv",
                "mbz",
                LocalTime.of(0,3,20));

        ResponseEntity<String> idDeletedResponse = new ResponseEntity<>(songInitial.getIdSong(),HttpStatus.ACCEPTED);

        Mockito.when(songRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.just(songInitial));
        Mockito.when(songRepositoryMock.deleteById(Mockito.any(String.class))).thenReturn(Mono.empty());


        var service = songService.deleteSong("fdhuchuadsa23");

        StepVerifier.create(service)
                .expectNext(idDeletedResponse)
                .expectComplete().verify();

        Mockito.verify(songRepositoryMock).findById("fdhuchuadsa23");
        Mockito.verify(songRepositoryMock).deleteById("vhcixierji3i");
    }

    @Test
    void deleteSongError() {
        Song songInitial = new Song(
                "fdhuchuadsa23",
                "23",
                "vhcixierji3i",
                "zzz",
                "mv",
                "mbz",
                LocalTime.of(0,3,20));

        ResponseEntity<String> idDeletedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Mockito.when(songRepositoryMock.findById(Mockito.any(String.class))).thenReturn(Mono.empty());

        var service = songService.deleteSong("fdhuchuadsa23");

        StepVerifier.create(service)
                .expectNext(idDeletedResponse)
                .expectComplete().verify();

        Mockito.verify(songRepositoryMock).findById("fdhuchuadsa23");

    }

}
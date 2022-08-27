package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.entities.Playlist;
import ec.com.reactive.music.repository.IPlaylistRepository;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class PlaylistServiceImplTest {

    @Mock
    IPlaylistRepository iPlaylistRepository;

    ModelMapper modelMapper;

    PlaylistServiceImpl playlistServiceImpl;

    @BeforeEach
    void init(){
        modelMapper = new ModelMapper();
        playlistServiceImpl = new PlaylistServiceImpl(iPlaylistRepository,modelMapper);
    }

    @Test
    @DisplayName("savePlaylist")
    void savePlaylistTest(){
        Playlist playlistExpected = new Playlist();
        playlistExpected.setIdPlaylist("123456789");
        playlistExpected.setUsername("caaromerogisss");
        playlistExpected.setDuration(LocalTime.of(0,0,0));
        playlistExpected.setName("Azucar");
        playlistExpected.setSongs(new ArrayList<>());

        var playlistDTOExpected = modelMapper.map(playlistExpected, PlaylistDTO.class);

        ResponseEntity<PlaylistDTO> playlistDTOResponse = new ResponseEntity<>(playlistDTOExpected, HttpStatus.CREATED);

        Mockito.when(iPlaylistRepository.save(Mockito.any(Playlist.class))).thenReturn(Mono.just(playlistExpected));

        var service = playlistServiceImpl.savePlaylist(playlistDTOExpected);

        StepVerifier.create(service)
                .expectNext(playlistDTOResponse)
                .expectComplete()
                .verify();

        Mockito.verify(iPlaylistRepository).save(playlistExpected);
    }





}
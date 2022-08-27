package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.domain.entities.Playlist;
import ec.com.reactive.music.repository.IPlaylistRepository;
import ec.com.reactive.music.repository.ISongRepository;
import ec.com.reactive.music.service.IPlaylistService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaylistServiceImpl implements IPlaylistService {
    @Autowired
    IPlaylistRepository iPlaylistRepository;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public Mono<ResponseEntity<Flux<PlaylistDTO>>> findAllPlaylists() {
        return this.iPlaylistRepository
                .findAll()
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NO_CONTENT.toString())))
                .map(playlist -> entityToDTO(playlist))
                .collectList()
                .map(playlistDTOS -> new ResponseEntity<>(Flux.fromIterable(playlistDTOS),HttpStatus.FOUND))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(Flux.empty(),HttpStatus.NO_CONTENT)));
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> findPlaylistsById(String id) {
        return this.iPlaylistRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString()))) //Capture the error
                .map(this::entityToDTO)
                .map(playlistDTO -> new ResponseEntity<>(playlistDTO, HttpStatus.FOUND)) //Mono<ResponseEntity<AlbumDTO>>
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> savePlaylist(PlaylistDTO playlistDTO) {
        return this.iPlaylistRepository
                .save(DTOToEntity(playlistDTO))
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.EXPECTATION_FAILED.toString())))
                .map(playlist -> entityToDTO(playlist))
                .map(playlistDTO1 -> new ResponseEntity<>(playlistDTO1,HttpStatus.CREATED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED)));
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> updatePlaylist(String id, PlaylistDTO playlistDTO) {
        return this.iPlaylistRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> {
                    playlistDTO.setIdPlaylist(playlist.getIdPlaylist());
                    return this.savePlaylist(playlistDTO);
                })
                .map(playlistDTOResponseEntity -> new ResponseEntity<>(playlistDTOResponseEntity.getBody(),HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_MODIFIED)));
    }

    @Override
    public Mono<ResponseEntity<String>> deletePlaylist(String id) {
        return this.iPlaylistRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> this.iPlaylistRepository
                        .deleteById(playlist.getIdPlaylist())
                        .map(monoVoid -> new ResponseEntity<>(id, HttpStatus.ACCEPTED)))
                .thenReturn(new ResponseEntity<>(id, HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> saveSongInPlaylist(String idPlaylist,SongDTO songDTO) {
        return iPlaylistRepository
                .findById(idPlaylist)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .map(this::entityToDTO)
                .flatMap(playlistDTO -> {
                    PlaylistDTO newDTO = new PlaylistDTO();
                    newDTO.setIdPlaylist(playlistDTO.getIdPlaylist());
                    newDTO.setName(playlistDTO.getName());
                    newDTO.setUsername(playlistDTO.getUsername());
                    newDTO.setDuration(playlistDTO.getDuration());
                    newDTO.addDuration(songDTO.getDuration());
                    newDTO.setSongs(playlistDTO.getSongs());
                    newDTO.addSongDTO(songDTO);
                    return this.savePlaylist(newDTO);
                })
                .map(playlistDTOResponseEntity -> new ResponseEntity<>(playlistDTOResponseEntity.getBody(),HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_MODIFIED)));
    }


    @Override
    public Mono<ResponseEntity<Void>> deleteSongInPlaylist(String idPlaylist, SongDTO songDTO) {
        return iPlaylistRepository
                .findById(idPlaylist)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .map(this::entityToDTO)
                .flatMap(playlistDTO -> {
                    PlaylistDTO newDTO = new PlaylistDTO();
                    newDTO.setIdPlaylist(playlistDTO.getIdPlaylist());
                    newDTO.setName(playlistDTO.getName());
                    newDTO.setUsername(playlistDTO.getUsername());
                    newDTO.setDuration(playlistDTO.getDuration());
                    newDTO.substractDuration(songDTO.getDuration());
                    newDTO.setSongs(playlistDTO.getSongs());
                    newDTO.deleteSong(songDTO);
                    return this.savePlaylist(newDTO);
                })
                .map(playlistDTOResponseEntity -> new ResponseEntity<Void>(HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_MODIFIED)));

    }

    @Override
    public Playlist DTOToEntity(PlaylistDTO playlistDTO) {
        return this.modelMapper.map(playlistDTO, Playlist.class);
    }

    @Override
    public PlaylistDTO entityToDTO(Playlist playlist) {
        return this.modelMapper.map(playlist,PlaylistDTO.class);
    }
}

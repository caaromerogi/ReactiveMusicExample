package ec.com.reactive.music.service;

import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.domain.entities.Playlist;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlaylistService {
    Mono<ResponseEntity<Flux<PlaylistDTO>>> findAllPlaylists();
    Mono<ResponseEntity<PlaylistDTO>> findPlaylistsById(String id);
    Mono<ResponseEntity<PlaylistDTO>> savePlaylist(PlaylistDTO playlistDTO);
    Mono<ResponseEntity<PlaylistDTO>> updatePlaylist(String id, PlaylistDTO playlistDTO);
    Mono<ResponseEntity<String>> deletePlaylist(String id);
    Mono<ResponseEntity<PlaylistDTO>> saveSongInPlaylist(String idPlaylist, SongDTO songDTO);
    Mono<ResponseEntity<Void>> deleteSongInPlaylist(String idPlaylist, SongDTO songDTO);

    //ModelMapper functions
    Playlist DTOToEntity (PlaylistDTO playlistDTO);
    PlaylistDTO entityToDTO(Playlist playlist);
}

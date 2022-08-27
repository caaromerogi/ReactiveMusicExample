package ec.com.reactive.music.web;

import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.repository.ISongRepository;
import ec.com.reactive.music.service.IPlaylistService;
import ec.com.reactive.music.service.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class PlaylistResource {
    @Autowired
    IPlaylistService iPlaylistService;

    @Autowired
    ISongService iSongService;

    @GetMapping("/getAllPlaylists")
    public Mono<ResponseEntity<Flux<PlaylistDTO>>> getAllPlaylists(){
        return iPlaylistService.findAllPlaylists();
    }

    @GetMapping("/getPlaylist/{id}")
    public Mono<ResponseEntity<PlaylistDTO>> getPlaylistById(@PathVariable String id){
        return iPlaylistService
                .findPlaylistsById(id);
    }

    @PostMapping("/createPlaylist")
    public Mono<ResponseEntity<PlaylistDTO>> savePlaylist(@RequestBody PlaylistDTO playlist){
        return iPlaylistService.savePlaylist(playlist);
    }

    @PutMapping("/saveSongPlaylist/{idPlaylist}/{idSong}")
    public Mono<ResponseEntity<PlaylistDTO>> saveSong(@PathVariable("idPlaylist") String idPlaylist,
                                                      @PathVariable("idSong") String idSong){
        return iSongService.findSongById(idSong)
                .flatMap(songDTOResponseEntity -> iPlaylistService.saveSongInPlaylist(idPlaylist, songDTOResponseEntity.getBody()));
    }

    @DeleteMapping("/deletePlaylistSong/{idPlaylist}/{idSong}")
    public Mono<ResponseEntity<Void>> deleteSong(@PathVariable String idPlaylist, @PathVariable String idSong){
        return iSongService.findSongById(idSong)
                .flatMap(songDTOResponseEntity -> iPlaylistService.deleteSongInPlaylist(idPlaylist, songDTOResponseEntity.getBody()));
    }

    @DeleteMapping("/deletePlaylist/{id}")
    public Mono<ResponseEntity<String>> deletePlaylist(@PathVariable String id){
        return iPlaylistService.deletePlaylist(id);
    }
}

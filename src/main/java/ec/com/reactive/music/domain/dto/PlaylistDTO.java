package ec.com.reactive.music.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ec.com.reactive.music.domain.entities.Song;
import lombok.*;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss")
public class PlaylistDTO {
    private String idPlaylist;
    private String name;
    private String username;
    private ArrayList<SongDTO> songs = new ArrayList<>();
    private LocalTime duration = LocalTime.of(0,0,0);

    public void addSongDTO(SongDTO songDTO){
        songs.add(songDTO);
    }

    public void addDuration(LocalTime duration){
        this.duration = duration.plusHours(this.duration.getHour())
                .plusMinutes(this.duration.getMinute())
                .plusSeconds(this.duration.getSecond());
    }

    public void substractDuration(LocalTime duration){
        this.duration = this.duration.minusHours(duration.getHour())
                .minusMinutes(duration.getMinute())
                .minusSeconds(duration.getSecond());
    }

    public void deleteSong(SongDTO song){
        songs.remove(song);
    }


}


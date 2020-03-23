package main.Database;

import org.jetbrains.annotations.NotNull;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import main.DataClasses.Song;

public class SongDatabase {

    private List<Song> songs = new ArrayList<>();

    private final int TITLE = 1;
    private final int ARTIST = 2;
    private final int ALBUM = 3;
    private final int YEAR = 4;

    public List<Song> findSong(String userLine, Integer userChoice) throws SQLException {
        ResultSet result = ProjectConnectionPool.getInstance().createResultSet(selectAllData());

        songs.clear();
        songs = setSongList(result);

        switch(userChoice) {
            case TITLE:
                return songs.stream().filter(s -> s.getTitle().contains(userLine)).collect(Collectors.toList());
            case ARTIST:
                return songs.stream().filter(s -> s.getArtistName().contains(userLine)).collect(Collectors.toList());
            case ALBUM:
                return songs.stream().filter(s -> s.getAlbumName().contains(userLine)).collect(Collectors.toList());
            case YEAR:
                return songs.stream().filter(s -> s.getYear().contains(userLine)).collect(Collectors.toList());
            default:
                throw new RuntimeException("Incorrect input!");
        }
    }

    public List<Song> addSongToPlaylist(Integer userChoice) throws SQLException {
        createStatement().executeUpdate(createPlaylist());
        createStatement().executeUpdate(addSongToUserPlaylist(userChoice));
        putResultToList();
        return songs;
    }

    private void putResultToList() throws SQLException {
        ResultSet result = ProjectConnectionPool.getInstance().createResultSet(selectDataFromUserPlaylist());

        songs.clear();

        while(result.next()) {
            Song song = new Song(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
            songs.add(song);
        }
    }

    private Statement createStatement() throws SQLException {
        return ProjectConnectionPool.getInstance().getConnection().createStatement();
    }

    @NotNull
    private List<Song> setSongList(ResultSet result) throws SQLException {

        List<Song> songs = new ArrayList<>();

        while(result.next()) {
            Song song = new Song(result.getInt(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
            songs.add(song);
        }

        return songs;
    }

    @NotNull
    private String selectAllData() {
        return "SELECT awesomePlaylist.songs.ID, title, artistName, albumName, year FROM awesomePlaylist.songs LEFT JOIN awesomePlaylist.artists ON (awesomePlaylist.songs.artistID = awesomePlaylist.artists.ID)";
    }

    @NotNull
    private String createPlaylist() {
        return "CREATE TABLE IF NOT EXISTS `awesomePlaylist`.`userPlaylist` (`ID` INT NOT NULL AUTO_INCREMENT, `songID` INT NOT NULL, INDEX `songID_idx` (`songID` ASC) VISIBLE, INDEX `ID` (`ID` ASC) VISIBLE, CONSTRAINT `songID` FOREIGN KEY (`songID`) REFERENCES `awesomePlaylist`.`songs` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE)";
    }

    @NotNull
    private String addSongToUserPlaylist(Integer userChoice) {
        return "INSERT INTO awesomePlaylist.userPlaylist VALUES (NULL, " + userChoice + ")";
    }

    @NotNull
    private String selectDataFromUserPlaylist() {
        return "SELECT awesomePlaylist.userPlaylist.ID, title, artistName, albumName, year FROM awesomePlaylist.userPlaylist LEFT JOIN awesomePlaylist.songs ON (awesomePlaylist.userPlaylist.songID = awesomePlaylist.songs.ID) LEFT JOIN awesomePlaylist.artists ON (awesomePlaylist.songs.artistID = awesomePlaylist.artists.ID)";
    }
}
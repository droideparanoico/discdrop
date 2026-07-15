package io.discdrop.mbz.dto;

import java.util.List;

public class ArtistSearchResult {
    public int count;
    public int offset;
    public List<ArtistDto> artists;

    public static class ArtistDto {
        public String id;
        public String name;
        public String disambiguation;
        public String type;
        public String country;
        public String score;
        public Area area;

        public static class Area {
            public String name;
        }
    }
}

package io.discdrop.mbz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ReleaseGroupBrowseResult {
    @JsonProperty("release-group-count")
    public int count;

    @JsonProperty("release-group-offset")
    public int offset;

    @JsonProperty("release-groups")
    public List<ReleaseGroupDto> releaseGroups;

    public static class ReleaseGroupDto {
        public String id;
        public String title;
        @JsonProperty("first-release-date")
        public String firstReleaseDate;
        @JsonProperty("primary-type")
        public String primaryType;
        @JsonProperty("secondary-types")
        public List<String> secondaryTypes;
        @JsonProperty("artist-credit")
        public List<ArtistCredit> artistCredit;

        public static class ArtistCredit {
            public String name;
            public String joinphrase;
            public Artist artist;

            public static class Artist {
                public String id;
                public String name;
            }
        }
    }
}

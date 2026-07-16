# DiscDrop

Follow MusicBrainz artists and track their **release groups** (not individual releases, to avoid duplicate vinyl/CD editions). Shows a combined web feed plus an RSS feed of new release groups, ordered by first-release-date.

Built with Java 21 + Quarkus, htmx, and daisyUI.

## Run (dev)

```bash
./mvnw quarkus:dev
```

App at http://localhost:8080 — RSS at http://localhost:8080/rss

## Configuration (`application.properties`)

| Property | Default | Notes |
|---|---|---|
| `discdrop.mbz.user-agent` | `DiscDrop/1.0 (david.alvarez.81@gmail.com)` | **Required.** MusicBrainz needs a meaningful contact string. Override per environment. |
| `quarkus.rest-client."musicbrainz".url` | `https://musicbrainz.org/ws/2` | MBZ API base. |
| `discdrop.mbz.rate-limit-ms` | `1000` | Enforced gap between MBZ calls (≤1 req/s). |
| `discdrop.feed.page-size` | `25` | Feed page size / load-more batch. |
| `discdrop.rss.item-count` | `50` | RSS items. |

The H2 file database lives in `./data/discdrop.mv.db` (gitignored).

## Architecture

See [discdrop-plan.md](discdrop-plan.md) for the full design.

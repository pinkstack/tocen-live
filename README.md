# Točen Live

Točen Live is Real-time WebSocket server for serving MOL LPP Bus information.

## Usage

This service comes pre-packaged as a public Docker image and can be simply ran with the following `run` command.

```bash
export TOCEN_API_KEY="<ask>"
docker run -ti --rm -e TOCEN_API_KEY=${TOCEN_API_KEY} ghcr.io/pinkstack/tocen-live
```

Or as a daemon with `-d`

```bash
docker run -d --name=tocen-live -e TOCEN_API_KEY=${TOCEN_API_KEY} ghcr.io/pinkstack/tocen-live
```

## Configuration

The service can accept the following environment variables

- `TOCEN_API_KEY` - API Key for the underlying LPP WebService
- `REFRESH_INTERVAL` - Internal refresh interval used for refreshing internal state. Default value - `100 milliseconds`

## Development

To run the test suite one shall use:

```bash
sbt test
```

To run the service in development mode

```bash
sbt "~run"
sbt "~reStart" # with sbt-revolver for JVM based restarts
```

To build and publish a new Docker image to [container registry][cr] use

```bash
sbt docker:publish
sbt docker:publishLocal # to publish to local (development) registry
```

- [Oto Brglez](https://github.com/otobrglez)

[cr]: https://github.com/pinkstack/tocen-live/pkgs/container/tocen-live

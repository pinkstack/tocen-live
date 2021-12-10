FROM ghcr.io/graalvm/graalvm-ce:21

LABEL maintainer="Oto Brglez <otobrglez@gmail.com>"
LABEL org.opencontainers.image.url="https://github.com/pinkstack/tocen-live"
LABEL org.opencontainers.image.source="https://github.com/pinkstack/tocen-live"

# install native-image
RUN gu install native-image

# work dir on /app
WORKDIR /app

# add jar file which built with sbt assembly
ADD target/*/tocen-live.jar tocen-live.jar

# built graal native image
# --static - creates a statically linked executable that has no dependencies on external libraries
# --verbose - verbose build output which useful for debugging
# --allows-incomplete-classpath - allows image building with an incomplete class path and reports type resolution errors at run time when they are accessed the first time, instead of during image building
# --report-unsupported-elements-at-runtime - reports usage of unsupported methods and fields at run time when they are accessed the first time, instead of as an error during image building
# --no-fallback - fail the build instead of generating fallback code when native image cannot resolve uses of reflection or other issues
RUN native-image --static --verbose --allow-incomplete-classpath --report-unsupported-elements-at-runtime  --no-fallback -jar tocen-live.jar tocen-live

# command
ENTRYPOINT ["/app/tocen-live"]

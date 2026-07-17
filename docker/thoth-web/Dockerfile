FROM node:18.10.0 as build
COPY ./thoth-web /app
WORKDIR /app
RUN npm i -g npm@9.8.0
RUN npm i -g @angular/cli
RUN npm i -g @ionic/cli
RUN npm install
RUN ionic build --prod

# Pinned, self-hosted drawio editor. thoth-web used to embed the live
# https://embed.diagrams.net/, but diagrams.net auto-deploys and has broken the
# integration (e.g. SVG export changes). Hosting a fixed release ourselves means
# the editor never changes under us. Bump DRAWIO_VERSION deliberately, only
# after verifying SVG export still round-trips.
FROM alpine:3.20 as drawio
ARG DRAWIO_VERSION=28.2.9
RUN apk add --no-cache curl unzip \
 && curl -fsSL -o /tmp/draw.war \
      "https://github.com/jgraph/drawio/releases/download/v${DRAWIO_VERSION}/draw.war" \
 && mkdir -p /drawio \
 && unzip -q /tmp/draw.war -d /drawio \
 && rm -rf /drawio/WEB-INF /drawio/META-INF /tmp/draw.war

FROM nginx:alpine
COPY --from=build /app/www /usr/share/nginx/html
COPY --from=drawio /drawio /usr/share/nginx/html/drawio
COPY ./docker/thoth-web/default.conf /etc/nginx/conf.d/default.conf
CMD /init-env.sh
FROM node:18.10.0 as build
COPY ./thoth-web /app
WORKDIR /app
RUN npm i -g npm@9.8.0
RUN npm i -g @angular/cli
RUN npm i -g @ionic/cli
RUN npm install
RUN ionic build --prod

FROM nginx:alpine
COPY --from=build /app/www /usr/share/nginx/html
COPY ./docker/thoth-web/default.conf /etc/nginx/conf.d/default.conf
CMD /init-env.sh
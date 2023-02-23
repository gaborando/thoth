FROM node:18.10.0 as build
COPY ./thoth-web /app
WORKDIR /app
RUN npm install
RUN npm i -g @ionic/cli
RUN ionic build --prod

FROM nginx:alpine
COPY --from=build /app/www /usr/share/nginx/html
COPY ./docker/thoth-web/default.conf /etc/nginx/conf.d/default.conf
CMD /init-env.sh
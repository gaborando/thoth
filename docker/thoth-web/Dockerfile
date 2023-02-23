FROM node:18.10.0 as build
COPY ./thoth-web /app
WORKDIR /app
RUN npm install
RUN npm i -g @ionic/cli
RUN ionic build --prod
RUN ls -alF /app/www

FROM nginx:alpine
COPY --from=build /app/www /usr/share/nginx/html
RUN ls -alF /usr/share/nginx/html
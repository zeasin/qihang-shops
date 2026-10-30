FROM node:18-alpine AS build
WORKDIR /build

COPY package.json package-lock.json ./
RUN npm ci --registry=https://registry.npmmirror.com

COPY . .
RUN npm run build:prod

FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /build/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

FROM node:20-alpine AS builder
WORKDIR /app
COPY frontend/package.json ./package.json
RUN npm install --legacy-peer-deps --no-audit --no-fund
COPY frontend/ ./
RUN npm run build -- --configuration production

FROM nginx:1.27-alpine
COPY frontend/nginx.frontend.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/empleado-frontend /usr/share/nginx/html
EXPOSE 80

# Cat Breeds App

Aplicación web para consultar razas de gatos. Permite registrarse, iniciar sesión y consultar un catálogo completo de razas con información detallada e imágenes, consumiendo datos de la CatAPI.



---

## Instrucciones de ejecución

### Requisitos previos

- Docker y Docker Compose instalados
- Conexión a internet
- (Opcional) API key (https://thecatapi.com)

### Pasos

1. Clonar el repositorio:
   ```bash
   git clone <url-del-repo>
   cd catapp
   ```

2. (Opcional) Agregar la API key en `src/main/resources/application.yml`:
   ```yaml
   catapi:
     api-key: tu-api-key-aqui
   ```

3. Levantar todos los servicios:
   ```bash
   docker compose up --build
   ```

4. Acceder desde el navegador:
   - **Frontend:** http://localhost:4200
   - **Backend:** http://localhost:8080

### Notas

- La primera ejecución puede tardar varios minutos mientras Docker descarga las imágenes y Maven resuelve las dependencias.
- Los datos de MongoDB persisten en la carpeta `./data`. Para empezar con una base de datos limpia, eliminar esa carpeta antes de ejecutar.
- Para detener la aplicación: `docker compose down`

---

## Arquitectura

El proyecto está dividido en tres capas desplegadas con Docker Compose:

```
┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐
│                 │        │                 │        │                 │
│    Frontend     │──────▶ │    Backend      │──────▶ │    MongoDB      │
│  Angular 21     │  HTTP  │  Spring Boot 3  │        │                 │
│  puerto 4200    │        │  puerto 8080    │        │  puerto 27017   │
│                 │        │                 │        │                 │
└─────────────────┘        └────────┬────────┘        └─────────────────┘
                                    │
                                    │ HTTP
                                    ▼
                           ┌─────────────────┐
                           │                 │
                           │    CatAPI       │
                           │  thecatapi.com  │
                           │                 │
                           └─────────────────┘
```

### Backend — Arquitectura Hexagonal (Ports & Adapters)

El backend sigue arquitectura hexagonal para separar la lógica de negocio de los detalles de infraestructura:

```
src/
├── domain/
│   ├── model/          # Entidades del dominio (User, Breed, CatImage)
│   └── ports/
│       ├── in/         # Casos de uso (AuthUseCase, BreedUseCase, ImageUseCase)
│       └── out/        # Puertos de salida (UserRepositoryPort, CatApiPort)
├── application/
│   └── service/        # Implementación de casos de uso
└── infrastructure/
    ├── adapter/
    │   ├── in/web/     # Controllers REST, DTOs, Mappers
    │   └── out/
    │       ├── persistence/   # Repositorio MongoDB
    │       └── external/      # Cliente CatAPI (WebClient)
    ├── config/         # SecurityConfig, WebClientConfig
    └── security/       # JWT Filter, Token Provider
```

### Frontend — Angular standalone

```
src/app/
├── domain/             # Modelos e interfaces
├── infrastructure/
│   ├── services/       # Servicios HTTP (AuthService, BreedService, etc.)
│   └── interceptors/   # AuthInterceptor (agrega JWT a cada request)
├── core/
│   └── guards/         # AuthGuard, NoAuthGuard
└── presentation/
    └── pages/          # Componentes de página (login, register, breeds, etc.)
```

---

## Decisiones técnicas

### Programación reactiva con WebFlux
Se eligió Spring WebFlux en lugar del modelo tradicional de Servlets para manejar las llamadas a la CatAPI de forma no bloqueante. Esto permite que el backend procese múltiples peticiones simultáneas sin ocupar un hilo por cada una, lo cual es especialmente útil cuando se encadenan llamadas HTTP externas con consultas a MongoDB.

### Arquitectura hexagonal en el backend
Se aplicó arquitectura hexagonal para desacoplar completamente la lógica de negocio de los detalles de infraestructura. Esto hace que sea fácil cambiar la base de datos, el cliente HTTP externo o el framework web sin tocar el dominio. Los casos de uso se definen como interfaces en `ports/in` y las implementaciones concretas viven en infraestructura.

### Autenticación sin estado con JWT
Se optó por JWT en lugar de sesiones del servidor para mantener el backend completamente sin estado. El token se guarda en `sessionStorage` en el frontend (se limpia al cerrar el tab) y se envía en cada request via un interceptor HTTP. El backend valida el token en un filtro de WebFlux antes de llegar al controlador.

### MongoDB como base de datos
Se eligió MongoDB por la naturaleza flexible de los datos de usuarios y la compatibilidad nativa con el driver reactivo de Spring Data, lo que permite encadenar operaciones de base de datos dentro del flujo reactivo sin salir del paradigma.

### Angular standalone con Zone.js
Se usa Angular en modo standalone (sin NgModules) para simplificar la estructura del proyecto. Se mantiene Zone.js activo junto con `provideZoneChangeDetection` para que la detección de cambios funcione correctamente con los observables HTTP y los eventos del DOM.

### Docker Compose para despliegue local
Se containerizó toda la aplicación con Docker Compose para eliminar dependencias del entorno local. El frontend se sirve con Nginx en producción, el backend se compila con Maven en un multi-stage build, y MongoDB persiste los datos en un volumen local.

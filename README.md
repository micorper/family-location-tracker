# Family Location Tracker

Una aplicación Android para rastrear la ubicación familiar en tiempo real con integración de Firebase y Google Maps.

## 🚀 Características

- **Rastreo en tiempo real**: Ubicación familiar actualizada constantemente
- **Google Maps**: Visualización de mapas interactivos
- **Firebase**: Base de datos y autenticación en tiempo real
- **QR Codes**: Códigos QR para unir familia fácilmente
- **Notificaciones**: Alertas push de ubicación

## 📱 Funcionalidades

### Actividades Principales
- **MainActivity**: Pantalla de inicio y navegación
- **MapActivity**: Mapa principal con ubicaciones familiares
- **ProfileActivity**: Gestión de perfil y configuraciones
- **QRActivity**: Generación y escaneo de códigos QR

### Servicios
- **FirebaseMessagingService**: Notificaciones push

## 🛠️ Tecnologías

- **Lenguaje**: Kotlin
- **Plataforma**: Android
- **Gradle**: 8.2
- **Android Gradle Plugin**: 8.1.4
- **Firebase**: Realtime Database, Authentication
- **Google Maps**: Android API
- **CI/CD**: GitHub Actions

## 📋 Requisitos

- Android Studio Arctic Fox o superior
- JDK 11
- Firebase Project configurado
- Google Maps API Key

## 🔧 Configuración

1. **Clonar repositorio**
2. **Configurar Firebase**:
   - Descargar `google-services.json`
   - Colocar en `app/`
3. **Configurar Google Maps**:
   - Agregar API Key en `app/src/main/AndroidManifest.xml`
4. **Compilar**: `./gradlew assembleDebug`

## 📦 Compilación en la Nube

Este proyecto incluye **GitHub Actions** para compilación automática:

1. Push a `main` activa la compilación
2. Ve a la pestaña **"Actions"** en GitHub
3. Descarga el APK desde los artifacts
4. APK listo para instalar en Android

## 📝 Estructura del Proyecto


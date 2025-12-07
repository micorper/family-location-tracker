#!/bin/bash

# Script de Compilación Definitiva - Family Location Tracker
# Proyecto completo con todos los recursos Android

echo "🚀 Family Location Tracker - Compilación Definitiva"
echo "=================================================="

# Verificar directorio
if [ ! -f "build.gradle" ] || [ ! -d "app" ]; then
    echo "❌ Error: No estás en el directorio correcto del proyecto"
    echo "Ejecuta: cd ~/Escritorio/FamilyLocationTracker_COMPLETO"
    exit 1
fi

echo "📍 Directorio: $(pwd)"
echo "✅ Estructura del proyecto verificada"

# Verificar permisos
if [ ! -x "./gradlew" ]; then
    echo "🔧 Corrigiendo permisos del gradlew..."
    chmod +x ./gradlew
fi

# Limpiar completamente
echo "🧹 Limpiando proyecto completamente..."
rm -rf .gradle
rm -rf app/build
rm -rf build
rm -rf ~/.gradle/caches/

# Verificar configuración
echo "📋 Verificando configuración..."
echo "✅ ANDROID_HOME: $ANDROID_HOME"
echo "✅ JAVA_HOME: $JAVA_HOME"

if [ -z "$ANDROID_HOME" ]; then
    echo "⚠️ ANDROID_HOME no está configurado"
    echo "💡 Ejecuta: export ANDROID_HOME=/home/miguel/Android/Sdk"
fi

if [ -z "$JAVA_HOME" ]; then
    echo "⚠️ JAVA_HOME no está configurado"
    echo "💡 Ejecuta: export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64"
fi

# Compilar con información detallada
echo ""
echo "🔨 Iniciando compilación con stacktrace..."
./gradlew clean assembleDebug --stacktrace --info

# Verificar resultado
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ ¡COMPILACIÓN EXITOSA!"
    echo "=================================================="
    
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    
    if [ -f "$APK_PATH" ]; then
        echo "📱 APK generado:"
        echo "   📍 $APK_PATH"
        echo "   📊 Tamaño: $(ls -lh "$APK_PATH" | awk '{print $5}')"
        echo "   🕒 Fecha: $(ls -l "$APK_PATH" | awk '{print $6, $7, $8}')"
        echo ""
        echo "🎯 Para instalar en dispositivo:"
        echo "   adb install $APK_PATH"
        echo ""
        echo "📋 Información del APK:"
        echo "   Package: com.tufamilia.location"
        echo "   Version: 1.0"
        echo "   Target SDK: 34"
        echo "   Min SDK: 24"
    else
        echo "⚠️ Advertencia: APK no encontrado en la ubicación esperada"
    fi
else
    echo ""
    echo "❌ ERROR EN LA COMPILACIÓN"
    echo "=================================================="
    echo "💡 Revisa los errores mostrados arriba"
    echo "🔧 Posibles soluciones:"
    echo "   1. Verificar que Android SDK está instalado"
    echo "   2. Verificar variables de entorno ANDROID_HOME y JAVA_HOME"
    echo "   3. Ejecutar: ./gradlew --refresh-dependencies"
    echo "   4. Verificar conexión a internet para descargar dependencias"
    exit 1
fi

echo ""
echo "🎉 ¡Proceso completado exitosamente!"
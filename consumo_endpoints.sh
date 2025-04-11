#!/bin/bash

BASE_URL_PARCIAL="http://localhost:8080/api/productos"
BASE_URL_TALLER="http://localhost:8081"

echo "🌐 Probando endpoint raíz del taller de spring boot con docker..."
curl -s -X GET "$BASE_URL_TALLER/"
echo -e "\n"

echo "✅ Probando creación de producto..."
curl -s -X POST "$BASE_URL_PARCIAL/crear" \
  -H "Content-Type: application/json" \
  -d '{"codigo":"P002","nombre":"Pantalon","precio":75000,"cantidad":50}'
echo -e "\n"

echo "📋 Listando productos..."
curl -s -X GET "$BASE_URL_PARCIAL/listar"
echo -e "\n"

echo "🔍 Obteniendo producto por código (P002)..."
curl -s -X GET "$BASE_URL_PARCIAL/obtener/P002"
echo -e "\n"

echo "✏️ Actualizando producto (P002)..."
curl -s -X PUT "$BASE_URL_PARCIAL/actualizar/P002" \
  -H "Content-Type: application/json" \
  -d '{"codigo":"P002","nombre":"Pantalon actualizado","precio":75000,"cantidad":25}'
echo -e "\n"

echo "❌ Eliminando producto (P002)..."
curl -s -X DELETE "$BASE_URL_PARCIAL/eliminar/P002"
echo -e "\n"

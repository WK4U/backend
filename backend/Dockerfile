# Etapa 1: Build do Gradle
FROM gradle:8.14.3-jdk17 AS builder
WORKDIR /app

# 1. Copia arquivos de configuração e o wrapper (prioriza o cache)
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle/ ./gradle/

# 2. Dá permissão de execução (se for o caso)
RUN chmod +x ./gradlew

# 3. Baixa as dependências. Se build.gradle não mudar, esta etapa usará o cache.
# Isso garante que o Docker saiba que ele deve invalidar o cache se o build.gradle mudar.
RUN ./gradlew dependencies --no-daemon

# 4. Copia o código-fonte (que muda com frequência)
COPY src/ ./src/

# 5. Roda o build (que será rápido se as dependências já tiverem sido baixadas)
RUN ./gradlew build --no-daemon -x test

# O resto do Dockerfile (Estágio 2) permanece o mesmo.
# ...
# -------------------------
# Etapa 2: Criar a imagem final com apenas o JAR
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copia o JAR gerado da etapa de build
# Seu JAR tem o nome "backend-0.0.1-SNAPSHOT-plain.jar" (visto na pasta libs)
# Vamos usar o curinga (*.jar) para pegar o arquivo correto.
COPY --from=builder /app/build/libs/*.jar app.jar

# Expõe a porta do Spring Boot (ajuste se necessário)
EXPOSE 8080

# Comando para rodar o Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]

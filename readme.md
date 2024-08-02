# Cosmos Odyssey Backend Server

See repositoorium sisaldab Cosmos Odyssey veebirakenduse tuumikteenust, 
mis on loodud pakkuma parimaid reisimarsruute meie päikesesüsteemi planeetide vahel. 
Rakendus võimaldab klientidel valida reisimarsruute ja teha broneeringuid.

## Omadused

* **REST/JSON API**: Rakendus pakub veebiteenust (API), mille kaudu saavad kasutajad pärida teavet 
erinevate planeetide vaheliste reiside kohta ja teha broneeringuid. 
See tähendab, et kasutajad saavad saata päringuid serverile ja saada vastuseid JSON-vormingus.

* **PostgreSQL Andmebaas**: Andmebaas, kuhu salvestatakse kõik kasutajate broneeringud ja marsruutide andmed.

## Tehnoloogiad

* Spring Boot
* Gradle
* Java
* PostgreSQL
* Flyway: Andmebaasi skeemi migratsioonide haldamiseks.

## Alustamine

### Projekti ehitamine

1. Klooni repositoorium:

    ```bash
    git clone https://github.com/kenbockler/cosmos-odyssey-core.git
    ```
2. Liigu kloonitud repositooriumi kausta:

    ```bash
    cd cosmos-odyssey-core
    ```
3. Ehitada projekt:

    ```bash
    ./gradlew build
    ```

### Rakenduse käivitamine

1. Käivita rakendus:

    ```bash
    java -jar build/libs/cosmos-odyssey-core-1.0.0.jar
    ```

Rakendus peaks nüüd töötama ja olema ligipääsetav pordil 9090.

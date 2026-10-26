# PokéRun — Proyecto de Programación Avanzada

**Integrantes:** Benjamín Alucema, Sebastian Pino y Vicente Ruiz.

**Asignatura:** ICI2241 — Programación Avanzada, PUCV.

**Profesor:** Leonardo González Catalán.

**Tecnologías:** Java 11, libGDX 1.12.1, backend LWJGL3 y Gradle Wrapper 8.10.1.

**Entorno del equipo:** Eclipse 2020-06 y JDK instalado en `C:\Program Files\Java\jdk-11.0.32.1`.

PokéRun es un juego de escritorio en el que el jugador elige un Pokémon, esquiva ataques elementales, recoge objetos beneficiosos y dispara mientras avanza por distintas rutas. El proyecto aplica programación orientada a objetos y los patrones Singleton, Strategy, Template Method y Builder.

Esta entrega contiene **25 Clases**. El nombre técnico de la carpeta del proyecto es `GameLluviaMenu2024`.

---

## 1. ¿En qué consiste el juego? — GM1

### Objetivo y recorrido

El jugador comienza eligiendo entre Charmander, Squirtle y Bulbasaur. Debe conservar sus puntos de salud y completar tres rutas para alcanzar el Santuario Varitipo. Prismeon, una evolución propia de Eevee representada en el código por `Eevee`, actúa como rival.

| Ruta | Nombre | Distancia de la ruta |
| --- | --- | --- |
| 1 | Pradera del Alba | 600 m |
| 2 | Bosque Prisma | 850 m |
| 3 | Santuario Varitipo | 1800 m |

La campaña tiene una distancia total de 3250 metros de juego. Al superar las primeras rutas, el personaje evoluciona y obtiene nuevas habilidades. Completar la campaña desbloquea a Pikachu y el modo infinito, que registra su récord por separado. La partida termina en derrota si la salud llega a cero.

### Elementos y mecánicas

- **Movimiento lateral y disparos:** permiten esquivar amenazas y atacar al rival.
- **Afinidades elementales:** fuego, agua, planta y eléctrico modifican el efecto de los impactos. Los ataques del mismo elemento benefician al jugador; los demás pueden dañarlo según su afinidad.
- **Bayas y recompensas:** ayudan a recuperar salud u obtener beneficios. Las pasivas incluyen regeneración, imán y potencia.
- **Evolución:** desbloquea el dash tras la primera evolución y el escudo tras la segunda.
- **Prismeon:** recibe daño en su barrera y genera recompensas al acumular daño efectivo.
- **Dificultad:** cambian las velocidades, los intervalos y los patrones de ataque durante la partida.
- **Opciones:** ajustes de música y efectos, movimiento reducido, alto contraste y ayuda.
- **Persistencia:** conserva récords, victorias, desbloqueos y preferencias entre ejecuciones.

### Controles

| Pantalla o situación | Tecla | Acción |
| --- | --- | --- |
| Menú | Flechas izquierda/derecha o números | Elegir personaje disponible |
| Menú | Enter | Iniciar |
| Menú | I | Cambiar campaña/infinito cuando está desbloqueado |
| Menú o pausa | O | Abrir ajustes |
| Partida | A / D o flechas izquierda/derecha | Moverse |
| Partida | Espacio | Disparar |
| Partida | Shift o X | Dash, cuando está desbloqueado |
| Partida | E o C | Escudo, cuando está desbloqueado |
| Partida | Esc | Pausar o continuar |
| Pausa | Enter / Q | Continuar / volver al menú |
| Partida | F3 | Mostrar u ocultar hitboxes |
| Juego | M | Silenciar o reactivar audio |
| Juego | F12 | Guardar captura PNG |
| Resultado | Enter / R | Volver al menú / otra aventura |

Los botones visibles también permiten interactuar con el ratón.

---

## 2. Juego base y modificaciones — GM2

El proyecto conserva el nombre `GameLluviaMenu2024` y parte de una mecánica de objetos que caen y un personaje que se desplaza horizontalmente. En PokéRun, esa mecánica se amplía con combate, afinidades y progresión.

| Aspecto | Desarrollo en PokéRun |
| --- | --- |
| Objetos que caen | Ataques elementales y bayas con efectos diferentes |
| Personaje controlado | Selección de inicial, salud, afinidad y evolución |
| Objetivo | Campaña de tres rutas y modo infinito desbloqueable |
| Combate | Disparos del jugador, rival, barrera y recompensas |
| Habilidades | Dash, escudo y pasivas |
| Interfaz | Menú, tutorial, pausa, ajustes y resultados |
| Guardado | Perfil local, récords y desbloqueos |
| Diseño | Separación de dominio, presentación, persistencia y arranque |

La lógica de `Partida` se mantiene separada de las clases gráficas y de guardado. Las pantallas interpretan las entradas y muestran el estado del dominio. Esto permite explicar las reglas sin depender del dibujo o del audio. El análisis detallado y los diagramas se encuentran en el informe del proyecto.

---

## 3. Estructura del proyecto

| Ruta | Contenido |
| --- | --- |
| `core/src/main/java/puppy/code/` | Clase principal del juego |
| `core/src/main/java/puppy/code/domain/` | Reglas, personajes, entidades, colisiones y patrones |
| `core/src/main/java/puppy/code/presentation/` | Pantallas y dibujo |
| `core/src/main/java/puppy/code/persistence/` | Perfil y almacenamiento |
| `lwjgl3/src/main/java/puppy/code/lwjgl3/` | Lanzador de escritorio |
| `assets/` | Sprites, texturas, fuentes y audio |
| `docs/` | Informe, inventario, relaciones y evidencias |
| `docs/uml/` | Diagramas UML y fuentes editables |
| `docs/licenses/` | Información de licencias de recursos |
| `gradle/wrapper/` | Configuración y archivos del Gradle Wrapper |
| `build.gradle`, `settings.gradle`, `gradle.properties` | Configuración de compilación y módulos |

### Las 25 clases, interfaces y enumeraciones

| Tipo | Responsabilidad |
| --- | --- |
| `GameLluviaMenu` | Ciclo de vida de libGDX, pantallas, audio y capturas |
| `Partida` | Coordinar reglas, entidades, progreso y estados de partida |
| `Jugador` | Salud, movimiento, evolución, habilidades y afinidad |
| `Eevee` | Rival Prismeon, barrera y recompensas |
| `Nivel` | Configuración de rutas y parámetros de dificultad |
| `Caja` | Geometría y detección de colisiones |
| `EntidadCayendo` | Clase abstracta y algoritmo común de colisión |
| `Ataque` | Entidad elemental con efecto sobre el jugador |
| `Baya` | Entidad recolectable beneficiosa |
| `ProyectilJugador` | Disparo producido por el jugador |
| `ComportamientoElemento` | Interfaz de cálculo del efecto elemental |
| `TipoElemental` | Estrategia para afinidades elementales |
| `TipoElectrico` | Estrategia eléctrica |
| `AtaqueBuilder` | Interfaz de construcción de ataques |
| `ConstructorAtaque` | Construcción y validación del producto |
| `DirectorAtaques` | Orden de los pasos de construcción |
| `Elemento` | Enumeración de elementos |
| `Inicial` | Enumeración de personajes seleccionables |
| `Pasiva` | Enumeración de beneficios pasivos |
| `Tecnica` | Enumeración de técnicas |
| `GestorPerfil` | Carga y guardado del perfil mediante Singleton |
| `Lienzo` | Dibujo y gestión de recursos visuales |
| `PantallaMenu` | Selección, inicio y opciones |
| `PantallaPartida` | Entrada, presentación de partida y resultados |
| `Lwjgl3Launcher` | Punto de entrada de escritorio |

---

## 4. Instalación, compilación y ejecución

### Requisitos

- JDK 11; ruta utilizada por el equipo: `C:\Program Files\Java\jdk-11.0.32.1`.
- Eclipse 2020-06 con integración Gradle (Buildship) para importar el proyecto.
- Conexión a Internet en la primera compilación para descargar Gradle y dependencias.
- Entorno de escritorio con soporte gráfico para LWJGL3.

Gradle descarga las dependencias declaradas. No es necesario instalar libGDX por separado ni agregar sus JAR manualmente. El juego no requiere base de datos ni servidor.

### Importar en Eclipse 2020-06

1. Descomprimir `PokeRun_25_CORREGIDO_Entrega.zip`.
2. Descomprimir también `PokeRun_25_CORREGIDO_Eclipse.zip` en una carpeta fija.
3. Abrir Eclipse, preferentemente en un workspace limpio para evitar mezclar entregas.
4. Entrar en **Window > Preferences > Java > Installed JREs**.
5. Pulsar **Add > Standard VM** y seleccionar como **JRE home**:
    
    ```
    C:\Program Files\Java\jdk-11.0.32.1
    ```
    
6. Marcar este JDK como predeterminado. Aunque Eclipse llame a esta sección “Installed JREs”, se debe seleccionar la carpeta del JDK.
7. Ir a **File > Import > Gradle > Existing Gradle Project**.
8. Seleccionar `GameLluviaMenu2024`, la carpeta que contiene `settings.gradle` y `gradlew.bat`.
9. Elegir **Gradle Wrapper**, configurar el Java home de Gradle con el mismo JDK 11 y desactivar el modo sin conexión para la primera importación.
10. Finalizar y esperar la sincronización. Deben aparecer los módulos `core` y `lwjgl3`.
11. Abrir **Gradle Tasks**, localizar el módulo `lwjgl3` y ejecutar **application > run**.

La versión de Buildship instalada influye en la importación. Si Eclipse no sincroniza el Wrapper, se puede compilar y ejecutar desde CMD con los comandos siguientes. La importación en el equipo concreto debe comprobarse antes de entregar.

### Ejecutar desde CMD con el JDK indicado

Abrir **CMD** en la raíz `GameLluviaMenu2024`. Los siguientes comandos usan sintaxis de CMD, no de PowerShell:

```bash
set "JAVA_HOME=C:\Program Files\Java\jdk-11.0.32.1"
set "PATH=%JAVA_HOME%\bin;%PATH%"
java -version
javac -version
gradlew.bat --version
gradlew.bat :lwjgl3:run --console=plain
```

Las versiones de Java y del compilador deben corresponder a Java 11. La tarea `run` configura `assets/` como directorio de trabajo.

### Compilar y generar un ejecutable

Desde esa misma ventana de CMD:

```bash
gradlew.bat clean build :lwjgl3:dist --console=plain
```

La tarea `dist` reúne el programa, los recursos y las dependencias en:

```
lwjgl3\build\libs\PokeRun.jar
```

Para ejecutarlo:

```bash
"C:\Program Files\Java\jdk-11.0.32.1\bin\java.exe" -jar lwjgl3\build\libs\PokeRun.jar
```

### Ejecutar el JAR incluido en la entrega

Desde la carpeta que contiene `PokeRun_25_CORREGIDO.jar`:

```bash
"C:\Program Files\Java\jdk-11.0.32.1\bin\java.exe" -jar PokeRun_25_CORREGIDO.jar
```

El JAR incluido y el generado con `dist` tienen nombres diferentes. El punto de entrada de ambos es `puppy.code.lwjgl3.Lwjgl3Launcher`.

---

## 5. UML, herencia e interfaces — GM3 y GM4

| Relación | Aplicación |
| --- | --- |
| Generalización | `Ataque` y `Baya` heredan de `EntidadCayendo` |
| Realización | `TipoElemental` y `TipoElectrico` implementan `ComportamientoElemento` |
| Realización | `ConstructorAtaque` implementa `AtaqueBuilder` |
| Asociación | `Jugador` mantiene una estrategia `ComportamientoElemento` |
| Asociación | `DirectorAtaques` mantiene una referencia a `AtaqueBuilder` |
| Uso y creación | `Partida` administra personajes y entidades de la simulación |
| Herencia de libGDX | `GameLluviaMenu` extiende `Game` |
| Herencia de libGDX | `PantallaMenu` y `PantallaPartida` extienden `ScreenAdapter` |
| Interfaz de libGDX | `Lienzo` implementa `Disposable` |

La clase abstracta y sus dos subclases participan en las colisiones reales de la partida. Las dos implementaciones de la interfaz elemental se seleccionan según el personaje y se utilizan para resolver impactos. Los tipos de libGDX se muestran como externos y no forman parte de los 25 tipos propios.

---

## 6. Encapsulamiento y principios de diseño — GM5

- **Estado protegido:** `Jugador` administra salud, recargas y evolución mediante operaciones del dominio; `GestorPerfil` controla su carga y guardado.
- **Validación:** las entidades y los niveles comprueban parámetros para impedir estados inválidos.
- **Colecciones:** `Partida` expone vistas no modificables de sus listas. Esto protege su estructura, aunque no convierte en inmutables todos los objetos contenidos.
- **Polimorfismo:** las entidades comparten el protocolo de colisión y cada subtipo aplica su efecto; las estrategias resuelven afinidades mediante una interfaz.
- **Dependencia de abstracciones:** `DirectorAtaques` trabaja con `AtaqueBuilder`, y `Jugador` con `ComportamientoElemento`.
- **Separación de responsabilidades:** el dominio resuelve reglas, las pantallas gestionan interacción y presentación, y `GestorPerfil` administra archivos.

Como asignación de responsabilidades GRASP, `Jugador` y `Nivel` actúan como expertos en su información; `Partida` coordina las operaciones del juego y crea las entidades que administra. La separación entre dominio y presentación reduce el acoplamiento. `Lienzo` concentra dibujo y recursos visuales, por lo que esa parte mantiene una responsabilidad más amplia.

---

## 7. Patrones de diseño obligatorios — GM6 a GM9

### GM6 — Singleton

**Problema:** varias pantallas necesitan consultar el mismo perfil y registrar resultados sin mantener copias independientes del progreso.

**Solución:** `GestorPerfil` tiene constructor privado, una instancia estática y el acceso `getInstancia()`. `GameLluviaMenu` obtiene esa instancia y proporciona acceso al perfil durante el juego.

**Participante principal:** `GestorPerfil`. Su responsabilidad incluye récords, desbloqueos, opciones y persistencia.

### GM7 — Strategy

**Problema:** el efecto de un ataque depende de la afinidad del personaje, y esas reglas deben poder variar sin concentrarse en la pantalla.

**Solución:** `ComportamientoElemento` define el contrato. `TipoElemental` y `TipoElectrico` implementan las reglas; `Inicial` permite seleccionar la estrategia correspondiente y `Jugador` delega en ella el cálculo del impacto.

**Participantes:** contexto `Jugador`, estrategia `ComportamientoElemento`, estrategias concretas `TipoElemental` y `TipoElectrico`.

### GM8 — Template Method

**Problema:** ataques y bayas necesitan seguir la misma secuencia de colisión, pero producen efectos distintos.

**Solución:** `EntidadCayendo.procesarColision()` es `final` y fija el orden: comprobar vigencia y salud, detectar colisión, aplicar el efecto y consumir la entidad. El paso `aplicarEfecto()` es abstracto y se redefine en `Ataque` y `Baya`.

**Participantes:** clase abstracta `EntidadCayendo`; clases concretas `Ataque` y `Baya`. Así se comparte el algoritmo y se evita aplicar dos veces una entidad consumida.

### GM9 — Builder

**Problema:** un ataque necesita posición, movimiento, datos de combate y técnica coherentes antes de entrar en la simulación.

**Solución:** `AtaqueBuilder` define los pasos; `ConstructorAtaque` reúne y valida los datos. `DirectorAtaques` ejecuta `reset`, `posicion`, `movimiento`, `combate` y `tecnica`, y obtiene el producto con `getResult()`. `Partida` utiliza el director para generar ataques.

**Participantes:** builder `AtaqueBuilder`, constructor concreto `ConstructorAtaque`, director `DirectorAtaques`, producto `Ataque` y cliente `Partida`.

Los diagramas de estas relaciones están incluidos en el UML completo; el informe desarrolla su contexto y justificación.



## 8. Documentación y comprobación de entrega

| Requisito | Evidencia principal |
| --- | --- |
| GM1 — Descripción para el jugador | Objetivos, mecánicas, controles y capturas de este README |
| GM2 — Análisis y modificaciones | Sección 2 e informe del proyecto |
| GM3 — UML | Diagramas en `docs/uml/`, con tipos propios y de libGDX |
| GM4 — Herencia e interfaces | `EntidadCayendo`, `Ataque`, `Baya` y estrategias elementales |
| GM5 — Encapsulamiento y principios | Sección 6, código e informe |
| GM6 — Singleton | `GestorPerfil` |
| GM7 — Strategy | `ComportamientoElemento`, `TipoElemental`, `TipoElectrico` y `Jugador` |
| GM8 — Template Method | `EntidadCayendo.procesarColision()` |
| GM9 — Builder | `AtaqueBuilder`, `ConstructorAtaque`, `DirectorAtaques` y `Ataque` |
| GM10 — GitHub | URL real del repositorio e historial con seis commits como mínimo |


El README sirve como guía del proyecto y no sustituye el informe ni los demás documentos de evaluación.

---

## 9. Problemas frecuentes

| Problema | Qué revisar |
| --- | --- |
| Eclipse utiliza otra versión de Java | Configurar el JDK 11 tanto en Installed JREs como en el Java home de Gradle |
| No aparece la opción de importar Gradle | Revisar que Eclipse tenga Buildship; utilizar CMD mientras se configura la integración |
| La sincronización de Gradle falla | Revisar conexión, modo sin conexión y compatibilidad de Buildship; probar el Wrapper desde CMD |
| No encuentra texturas o sonidos al ejecutar una clase directamente | Usar `:lwjgl3:run`, que configura el directorio de recursos, o el JAR empaquetado |
| Aparece `NoClassDefFoundError` relacionado con `DibujoPrismeon` | Usar el JAR corregido de esta entrega y no mezclar archivos de versiones anteriores |
| Pikachu o infinito aparecen bloqueados | Completar la campaña con el perfil actual |
| El progreso sigue apareciendo tras cambiar de carpeta el proyecto | El perfil se guarda fuera del proyecto, en la carpeta de datos del usuario |
| No se oye audio | Revisar M y los volúmenes; el juego puede continuar si falla la inicialización del audio |

---

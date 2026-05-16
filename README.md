# Escape Perfecto

Juego de escritorio hecho en Java Swing para NetBeans, con base de datos MySQL y patron repositorio.

## Idea del juego

Un jugador responde preguntas para ganar segundos. Con esos segundos, el equipo intenta tomar premios de la jaula antes de que la puerta se cierre. El objetivo es acumular el mayor valor posible sin quedar atrapado.

## Tecnologias

- Java 17
- Java Swing
- MySQL
- Maven
- Patron Repository

## Como abrirlo en NetBeans

1. Abre NetBeans.
2. Selecciona `File > Open Project`.
3. Elige esta carpeta.
4. Ejecuta el proyecto con `Run Project`.

La interfaz principal esta en `src/main/java/com/escaperfecto/ui/GameFrame.java`.
NetBeans tambien puede abrir `GameFrame.form` para editar los componentes desde la pestana `Design`.

La base de datos MySQL `escape_perfecto` se crea automaticamente la primera vez que se ejecuta, siempre que el servidor MySQL este encendido.

Por defecto usa:

- Usuario: `root`
- Password: vacio
- Servidor: `localhost:3306`

Si tu MySQL tiene otra configuracion, puedes usar variables de entorno:

- `DB_USER`
- `DB_PASSWORD`
- `DB_HOST_URL`
- `DB_NAME`

## Como verificarlo

Desde NetBeans puedes usar `Run Project` para abrir el juego. Si quieres verificar compilacion y pruebas desde terminal, usa Maven:

```bash
mvn test
```

En esta instalacion tambien funciona con el Maven incluido en NetBeans:

```powershell
& 'C:\Program Files\NetBeans-22\netbeans\java\maven\bin\mvn.cmd' test
```

## Como jugar

1. Escribe los nombres del participante que responde y del participante que entra a la jaula.
2. Elige una seccion de cultura general o deja `Todas`.
3. Presiona `Nueva partida`.
4. Responde hasta un maximo de 10 preguntas para ganar segundos.
5. Si respondes mal, la partida termina.
6. Puedes abrir la puerta despues de 1, 2, 3 o las preguntas que decidas.
7. Presiona `Abrir puerta` para iniciar el contador y entrar a la jaula.
8. Usa esos segundos para tomar premios antes de que llegue a cero.
9. Presiona `Salir de la jaula` para detener el contador y seguir respondiendo.
10. Si el tiempo llega a cero estando dentro, la puerta se cierra y la partida continua.
11. La partida tambien termina si tomas `Escape seguro` o cuando se contestan las 10 preguntas.

## Secciones de preguntas

- Historia
- Geografia
- Ciencia y Naturaleza
- Arte y Literatura
- Entretenimiento y Cultura Pop
- Deportes

El juego incluye 30 preguntas de cultura general y 14 premios disponibles en la jaula.

## Comodines

Cada partida permite usar una vez cada comodin:

- `Cambiar seccion`: permite elegir otra seccion y cargar una pregunta nueva.
- `Eliminar opcion`: oculta una respuesta incorrecta de la pregunta actual.
- `Cambiar pregunta`: salta la pregunta actual y muestra la siguiente.

## Estructura

- `model`: clases del dominio del juego.
- `repository`: contratos y repositorios JDBC.
- `service`: logica principal de la partida.
- `ui`: interfaz grafica Swing.
- `db`: conexion e inicializacion de MySQL.



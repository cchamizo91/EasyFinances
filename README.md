# EASYFINANCES

## Introducción
EasyFinances es una aplicación Android de finanzas personales. Este proyecto se ha desarrollado utilizando Git para manejar versiones y ramas específicas para cada funcionalidad.

## Funcionalidades
-  **Interfaz principal y menú de navegación**.
  Rama : **feature-main-ui**
-  **Autenticación:** Gestión de autenticación (login, registro, recuperación de contraseña).
  Rama: **feature-authentication**
-  **Gestión de Transacciones:** Añadir, listar y eliminar transacciones.
  Rama: **feature-transactions**
-  **Salud financiera:** Visualización de gráficos e indicadores.
  Rama: **feature-graphics**
-  **Calendario Financiero:** Funcionalidades de calendario.
  Rama: **feature-calendar**
-  **Generación de informes en PDF**.
  Rama: **feature-report**
-  **Diseño responsive**.
  UI adaptado con `ConstraintLayout` y `Material Design`

## Tecnologías utilizadas
- Kotlin + Android Studio
- Firebase Firestore + Firebase Auth
- MPAndroidChart (gráficos)
- MaterialCalendarView (calendario)
- ViewBinding
- Git (Github flow por ramas)

## Estructura del proyecto
-- El código está organizado por módulos para mantener claridad de los componentes:
- **`activities/`**: Contiene las principales Activities de la aplicación, como LoginActivity, RegisterActivity, OptionActivity, entre otras.
- **`adapters/`**: Maneja los adaptadores de RecyclerView para las transacciones y recordatorios.
- **`models/`**: Contiene las clases de datos, como `Transaction` y `Reminder`
- **`utils/`**: Incluye utilidades varias, como decoradores para calendarios.
- **`res/`**: Almacena recursos gráficos, layouts...
- **`MainActivity.kt/`**: Entrada principal de la app.

## Instrucciones de Instalación
1. Clona el repositorio:
   ```bash
   git clone git clone https://github.com/cchamizo91/easyfinances.git
2. Abre el proyecto en Android Studio
3. Configura Firebase y añade el archivo `google-services.json` en la carpeta `app/` .
4. Ejecuta la app en un emulador o dispositivo físico.

## Licencia

Este proyecto tiene una licencia personalizada. El código puede ser consultado únicamente con fines educativos. No se permite su modificación, distribución ni uso comercial.

Consulta el archivo [LICENSE](./LICENSE) para más detalles.

## Autor

Nombre: Carlos Chamizo Alberto
Curso: Desarrollo de Aplicaciones Multiplataforma (DAM)
Año académico: 2025
Proyecto académico: EasyFinances

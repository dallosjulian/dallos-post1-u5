package com.ejemplo.servlet;

import com.ejemplo.model.Tarea;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Servlet principal para la gestión de tareas.
 * Maneja el ciclo de vida, almacenamiento en applicationScope y sessionScope,
 * filtrado dinámico combinado y operaciones CRUD con patrón PRG.
 */
@WebServlet(name = "TareasServlet", urlPatterns = {"/tareas"})
public class TareasServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private List<Tarea> tareas = new ArrayList<>();
    private int contadorId = 1;

    /**
     * Método auxiliar para calcular fechas sumando días a una fecha base.
     */
    private Date sumarDias(Date base, int dias) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        Date hoy = new Date();
        tareas.add(new Tarea(contadorId++, "Estudiar Servlets y ciclo de vida", "Estudio", "Alta", sumarDias(hoy, 2)));
        tareas.add(new Tarea(contadorId++, "Completar laboratorio de JSP y JSTL", "Evaluación", "Alta", sumarDias(hoy, 4)));
        tareas.add(new Tarea(contadorId++, "Repasar conceptos de HttpSession y cookies", "Estudio", "Media", sumarDias(hoy, 6)));
        tareas.add(new Tarea(contadorId++, "Diseñar arquitectura multicapa en Jakarta EE", "Estudio", "Baja", sumarDias(hoy, 8)));

        // Publica la lista compartida en applicationScope para toda la aplicación
        getServletContext().setAttribute("tareas", tareas);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        String paramQ = req.getParameter("q");
        String paramCat = req.getParameter("cat");
        String paramPrioridad = req.getParameter("prioridad");

        boolean vienenParametros = (paramQ != null || paramCat != null || paramPrioridad != null);

        String filtroTexto;
        String filtroCategoria;
        String filtroPrioridad;

        if (vienenParametros) {
            filtroTexto = paramQ != null ? paramQ.trim() : "";
            filtroCategoria = paramCat != null ? paramCat.trim() : "";
            filtroPrioridad = paramPrioridad != null ? paramPrioridad.trim() : "";

            // Guardar en sesión para persistir el filtro entre navegaciones
            session.setAttribute("filtroTexto", filtroTexto);
            session.setAttribute("filtroCategoria", filtroCategoria);
            session.setAttribute("filtroPrioridad", filtroPrioridad);
        } else {
            // Restaurar los últimos filtros guardados desde la sesión
            Object stFiltroTexto = session.getAttribute("filtroTexto");
            Object stFiltroCat = session.getAttribute("filtroCategoria");
            Object stFiltroPrio = session.getAttribute("filtroPrioridad");

            filtroTexto = stFiltroTexto != null ? (String) stFiltroTexto : "";
            filtroCategoria = stFiltroCat != null ? (String) stFiltroCat : "";
            filtroPrioridad = stFiltroPrio != null ? (String) stFiltroPrio : "";
        }

        // Preparar atributos y despachar la vista
        prepararVistaTareas(req, filtroTexto, filtroCategoria, filtroPrioridad);
        req.getRequestDispatcher("/WEB-INF/views/tareas.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");

        if (accion == null) {
            resp.sendRedirect(req.getContextPath() + "/tareas");
            return;
        }

        switch (accion) {
            case "agregar":
                procesarAgregar(req, resp);
                break;

            case "eliminar":
                procesarEliminar(req, resp);
                break;

            case "completar":
                procesarCompletar(req, resp);
                break;

            case "identificar":
                procesarIdentificar(req, resp);
                break;

            case "limpiar-filtros":
                procesarLimpiarFiltros(req, resp);
                break;

            default:
                resp.sendRedirect(req.getContextPath() + "/tareas");
                break;
        }
    }

    private void procesarAgregar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String titulo = req.getParameter("titulo");

        if (titulo == null || titulo.trim().isEmpty()) {
            req.setAttribute("error", "El título de la tarea no puede estar vacío.");
            
            // Restaurar filtros de sesión para renderizar la vista correctamente tras error
            HttpSession session = req.getSession();
            String fTexto = (String) session.getAttribute("filtroTexto");
            String fCat = (String) session.getAttribute("filtroCategoria");
            String fPrio = (String) session.getAttribute("filtroPrioridad");

            prepararVistaTareas(req, fTexto != null ? fTexto : "", fCat != null ? fCat : "", fPrio != null ? fPrio : "");
            req.getRequestDispatcher("/WEB-INF/views/tareas.jsp").forward(req, resp);
            return;
        }

        String categoria = req.getParameter("categoria");
        if (categoria == null || categoria.trim().isEmpty()) {
            categoria = "General";
        }

        String prioridad = req.getParameter("prioridad");
        if (prioridad == null || prioridad.trim().isEmpty()) {
            prioridad = "Media";
        }

        Date fechaLimite = sumarDias(new Date(), 5);
        Tarea nuevaTarea = new Tarea(contadorId++, titulo.trim(), categoria.trim(), prioridad.trim(), fechaLimite);
        tareas.add(nuevaTarea);

        // Patrón Post-Redirect-Get (PRG)
        resp.sendRedirect(req.getContextPath() + "/tareas");
    }

    private void procesarEliminar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam.trim());
                tareas.removeIf(t -> t.getId() == id);
            } catch (NumberFormatException ignored) {
            }
        }
        // Patrón Post-Redirect-Get (PRG)
        resp.sendRedirect(req.getContextPath() + "/tareas");
    }

    private void procesarCompletar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam.trim());
                tareas.stream()
                        .filter(t -> t.getId() == id)
                        .findFirst()
                        .ifPresent(t -> t.setCompletada(true));
            } catch (NumberFormatException ignored) {
            }
        }
        // Patrón Post-Redirect-Get (PRG)
        resp.sendRedirect(req.getContextPath() + "/tareas");
    }

    private void procesarIdentificar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nombre = req.getParameter("nombre");
        if (nombre != null && !nombre.trim().isEmpty()) {
            req.getSession().setAttribute("usuario", nombre.trim());
        }
        // Patrón Post-Redirect-Get (PRG)
        resp.sendRedirect(req.getContextPath() + "/tareas");
    }

    private void procesarLimpiarFiltros(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        session.removeAttribute("filtroTexto");
        session.removeAttribute("filtroCategoria");
        session.removeAttribute("filtroPrioridad");
        resp.sendRedirect(req.getContextPath() + "/tareas?q=&cat=&prioridad=");
    }

    /**
     * Aplica el filtro con Java Streams y prepara los atributos de petición para tareas.jsp
     */
    private void prepararVistaTareas(HttpServletRequest req, String filtroTexto, String filtroCategoria, String filtroPrioridad) {
        final String fTexto = filtroTexto != null ? filtroTexto.trim().toLowerCase() : "";
        final String fCat = filtroCategoria != null ? filtroCategoria.trim() : "";
        final String fPrio = filtroPrioridad != null ? filtroPrioridad.trim() : "";

        // Filtrado combinado de tres criterios usando Java Streams
        List<Tarea> tareasFiltradas = tareas.stream()
                .filter(t -> {
                    boolean cumpleTexto = fTexto.isEmpty() ||
                            (t.getTitulo() != null && t.getTitulo().toLowerCase().contains(fTexto));

                    boolean cumpleCat = fCat.isEmpty() ||
                            "todas".equalsIgnoreCase(fCat) ||
                            (t.getCategoria() != null && t.getCategoria().equalsIgnoreCase(fCat));

                    boolean cumplePrioridad = fPrio.isEmpty() ||
                            "todas".equalsIgnoreCase(fPrio) ||
                            (t.getPrioridad() != null && t.getPrioridad().equalsIgnoreCase(fPrio));

                    return cumpleTexto && cumpleCat && cumplePrioridad;
                })
                .collect(Collectors.toList());

        // Extraer lista ordenada de categorías únicas existentes
        List<String> categorias = tareas.stream()
                .map(Tarea::getCategoria)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(c -> !c.isEmpty())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());

        // Asignar atributos al request
        req.setAttribute("tareas", tareasFiltradas);
        req.setAttribute("categorias", categorias);
        req.setAttribute("filtroTexto", filtroTexto);
        req.setAttribute("filtroCategoria", filtroCategoria);
        req.setAttribute("filtroPrioridad", filtroPrioridad);
    }
}

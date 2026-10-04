package com.ejemplo.servlet;

import com.ejemplo.model.Tarea;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Servlet para visualizar el detalle individual de una tarea.
 * Consume la lista compartida en applicationScope (ServletContext) sin duplicar memoria
 * y despacha hacia la vista detalle.jsp mediante forward.
 */
@WebServlet(name = "DetalleTareaServlet", urlPatterns = {"/tareas/detalle"})
public class DetalleTareaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/tareas?error=id-invalido");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam.trim());
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/tareas?error=id-invalido");
            return;
        }

        // Obtener la lista compartida directamente desde applicationScope (ServletContext)
        @SuppressWarnings("unchecked")
        List<Tarea> tareas = (List<Tarea>) getServletContext().getAttribute("tareas");

        if (tareas == null) {
            resp.sendRedirect(req.getContextPath() + "/tareas?error=no-encontrada");
            return;
        }

        // Buscar la tarea por su identificador
        Optional<Tarea> tareaOpt = tareas.stream()
                .filter(t -> t.getId() == id)
                .findFirst();

        if (tareaOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/tareas?error=no-encontrada");
            return;
        }

        // Asignar la tarea encontrada al request y despachar mediante FORWARD a la vista
        req.setAttribute("tarea", tareaOpt.get());
        req.getRequestDispatcher("/WEB-INF/views/detalle.jsp").forward(req, resp);
    }
}

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalle de Tarea - #${tarea.id}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<div class="contenedor">

    <!-- Enlace de Retorno -->
    <p style="margin-bottom: 20px;">
        <a href="${pageContext.request.contextPath}/tareas" class="btn-link">← Volver a la lista de tareas</a>
    </p>

    <!-- Encabezado de la Vista de Detalle -->
    <h1>
        <span>📄 Detalle de la Tarea #${tarea.id}</span>
        <span class="subtitulo">Información Extendida</span>
    </h1>

    <!-- Tarjeta con Tabla Vertical de Atributos -->
    <div class="detalle">
        <table class="detalle-tabla">
            <tbody>
            <tr>
                <th>ID</th>
                <td><strong>#${tarea.id}</strong></td>
            </tr>
            <tr>
                <th>Título</th>
                <td>
                    <span class="${tarea.completada ? 'completada' : ''}" style="font-size: 1.1rem; font-weight: 600;">
                        <c:out value="${tarea.titulo}"/>
                    </span>
                </td>
            </tr>
            <tr>
                <th>Categoría</th>
                <td>
                    <span class="badge badge-cat"><c:out value="${tarea.categoria}"/></span>
                </td>
            </tr>
            <tr>
                <th>Prioridad</th>
                <td>
                    <c:choose>
                        <c:when test="${tarea.prioridad eq 'Alta'}">
                            <span class="badge badge-alta">Alta</span>
                        </c:when>
                        <c:when test="${tarea.prioridad eq 'Media'}">
                            <span class="badge badge-media">Media</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge badge-baja">Baja</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th>Fecha Límite</th>
                <td>
                    <strong>
                        <fmt:formatDate value="${tarea.fechaLimite}" pattern="dd/MM/yyyy"/>
                    </strong>
                </td>
            </tr>
            <tr>
                <th>Estado Actual</th>
                <td>
                    <c:choose>
                        <c:when test="${tarea.completada}">
                            <span class="badge badge-completada">✓ Tarea Completada</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge badge-pendiente">⏳ Tarea Pendiente</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            </tbody>
        </table>

        <!-- Sección Inferior de Acciones -->
        <div class="detalle-acciones">
            <a href="${pageContext.request.contextPath}/tareas" class="btn btn-secundario">Regresar al Listado</a>

            <c:choose>
                <c:when test="${not tarea.completada}">
                    <form action="${pageContext.request.contextPath}/tareas" method="post" style="margin: 0;">
                        <input type="hidden" name="accion" value="completar">
                        <input type="hidden" name="id" value="${tarea.id}">
                        <button type="submit" class="btn btn-exito">✓ Marcar como Completada</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="tarea-completada-msg">
                        <span>✨ Esta tarea ya se encuentra completada con éxito.</span>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Tareas - Jakarta EE</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<div class="contenedor">

    <!-- Encabezado Principal -->
    <h1>
        <span>📋 Sistema de Gestión de Tareas</span>
        <span class="subtitulo">Jakarta EE 10 / Tomcat 10+</span>
    </h1>

    <!-- Barra de Identificación de Usuario y Sesión -->
    <div class="barra-usuario">
        <c:choose>
            <c:when test="${not empty sessionScope.usuario}">
                <div class="usuario-saludo">
                    👋 Bienvenido(a), <strong>${fn:escapeXml(sessionScope.usuario)}</strong>
                </div>
                <form action="${pageContext.request.contextPath}/tareas" method="post" class="form-identificar">
                    <input type="hidden" name="accion" value="identificar">
                    <input type="text" name="nombre" placeholder="Cambiar nombre..." required>
                    <button type="submit" class="btn btn-secundario">Cambiar</button>
                </form>
            </c:when>
            <c:otherwise>
                <div class="usuario-saludo">
                    👤 Sesión anónima
                </div>
                <form action="${pageContext.request.contextPath}/tareas" method="post" class="form-identificar">
                    <input type="hidden" name="accion" value="identificar">
                    <input type="text" name="nombre" placeholder="Tu nombre..." required>
                    <button type="submit" class="btn btn-primario">Identificarse</button>
                </form>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Alertas de Error y Notificaciones -->
    <c:if test="${not empty error}">
        <div class="alerta alerta-error">
            ⚠️ <span>${fn:escapeXml(error)}</span>
        </div>
    </c:if>

    <c:if test="${param.error eq 'no-encontrada'}">
        <div class="alerta alerta-error">
            ⚠️ <span>La tarea solicitada no fue encontrada o ya no existe en el sistema.</span>
        </div>
    </c:if>

    <c:if test="${param.error eq 'id-invalido'}">
        <div class="alerta alerta-error">
            ⚠️ <span>El identificador de tarea proporcionado no es válido.</span>
        </div>
    </c:if>

    <!-- Formulario de Filtro Combinado (GET) -->
    <div class="seccion-filtro">
        <div class="filtro-titulo">🔍 Filtrar Tareas</div>
        <form action="${pageContext.request.contextPath}/tareas" method="get" class="filtro">
            <input type="text" name="q" value="${fn:escapeXml(filtroTexto)}" placeholder="Buscar por título...">

            <select name="cat">
                <option value="">Todas las categorías</option>
                <c:forEach var="c" items="${categorias}">
                    <option value="${c}" ${c eq filtroCategoria ? 'selected' : ''}>${c}</option>
                </c:forEach>
            </select>

            <select name="prioridad">
                <option value="">Todas las prioridades</option>
                <option value="Alta" ${filtroPrioridad eq 'Alta' ? 'selected' : ''}>Alta</option>
                <option value="Media" ${filtroPrioridad eq 'Media' ? 'selected' : ''}>Media</option>
                <option value="Baja" ${filtroPrioridad eq 'Baja' ? 'selected' : ''}>Baja</option>
            </select>

            <button type="submit" class="btn btn-primario">Filtrar</button>
            <a href="${pageContext.request.contextPath}/tareas?q=&cat=&prioridad=" class="btn btn-secundario">Limpiar</a>
        </form>
    </div>

    <!-- Formulario de Inserción Rápida (POST con PRG) -->
    <div class="seccion-agregar">
        <form action="${pageContext.request.contextPath}/tareas" method="post" class="form-agregar">
            <input type="hidden" name="accion" value="agregar">
            <input type="text" name="titulo" placeholder="¿Qué tarea deseas agregar? (Ej. Preparar informe final)...">
            <button type="submit" class="btn btn-primario">➕ Agregar Tarea</button>
        </form>
    </div>

    <!-- Contador de Tareas y Encabezado de Tabla -->
    <div class="contador-header">
        <h2>Listado de Tareas</h2>
        <span class="contador-tareas">${fn:length(tareas)} tareas encontradas</span>
    </div>

    <!-- Tabla Principal de Tareas -->
    <table>
        <thead>
        <tr>
            <th style="width: 50px;">#</th>
            <th>Título</th>
            <th>Categoría</th>
            <th>Prioridad</th>
            <th>Estado</th>
            <th style="text-align: center;">Detalle</th>
            <th style="text-align: center;">Acción</th>
        </tr>
        </thead>
        <tbody>
        <c:choose>
            <c:when test="${not empty tareas}">
                <c:forEach var="t" items="${tareas}">
                    <tr>
                        <td><strong>${t.id}</strong></td>
                        <td>
                            <span class="${t.completada ? 'completada' : ''}">
                                ${fn:escapeXml(t.titulo)}
                            </span>
                        </td>
                        <td>
                            <span class="badge badge-cat">${fn:escapeXml(t.categoria)}</span>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${t.prioridad eq 'Alta'}">
                                    <span class="badge badge-alta">Alta</span>
                                </c:when>
                                <c:when test="${t.prioridad eq 'Media'}">
                                    <span class="badge badge-media">Media</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-baja">Baja</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${t.completada}">
                                    <span class="badge badge-completada">✓ Completada</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-pendiente">⏳ Pendiente</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td style="text-align: center;">
                            <a href="${pageContext.request.contextPath}/tareas/detalle?id=${t.id}" class="btn-link">Ver detalle</a>
                        </td>
                        <td style="text-align: center;">
                            <form action="${pageContext.request.contextPath}/tareas" method="post" style="display:inline;">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${t.id}">
                                <button type="submit" class="btn btn-peligro" onclick="return confirm('¿Seguro que deseas eliminar esta tarea?');">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr>
                    <td colspan="7" style="text-align: center; color: var(--color-texto-muted); padding: 30px;">
                        No se encontraron tareas que coincidan con los criterios seleccionados.
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>

</div>
</body>
</html>

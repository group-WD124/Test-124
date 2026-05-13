<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Hotels - Hotel Management System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <c:set var="currentPage" value="hotels"/>
  <%@ include file="nav.jsp" %>

  <main class="main-content">
    <section class="hero-panel compact-hero">
      <div>
        <p class="hero-kicker">Hotel dashboard CRUD</p>
        <h1>Manage hotel details</h1>
        <p>Create, update, read, and delete the hotel profile records used by the system.</p>
      </div>
      <a href="${pageContext.request.contextPath}/hotels?action=add" class="btn btn-primary">New Hotel</a>
    </section>

    <section class="page module-layout fade-in">
      <div class="card module-form-card">
        <div class="card-header">
          <div>
            <div class="card-title">${mode == 'edit' ? 'Edit hotel' : 'Create hotel'}</div>
            <div class="card-subtitle">Hotel ID is assigned automatically.</div>
          </div>
        </div>
        <form action="${pageContext.request.contextPath}/hotels" method="post" class="form-grid">
          <input type="hidden" name="id" value="${form.id}">
          <div class="form-group">
            <label for="hotelName">Hotel Name</label>
            <input type="text" id="hotelName" name="hotelName" value="${form.hotelName}" required>
          </div>
          <div class="form-group">
            <label for="location">Location</label>
            <input type="text" id="location" name="location" value="${form.location}" required>
          </div>
          <div class="form-group">
            <label for="contactNumber">Contact Number</label>
            <input type="tel" id="contactNumber" name="contactNumber" value="${form.contactNumber}" required>
          </div>
          <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="${form.email}" required>
          </div>
          <div class="form-group full">
            <label for="description">Description</label>
            <textarea id="description" name="description">${form.description}</textarea>
          </div>
          <div class="form-group full">
            <label for="facilities">Facilities</label>
            <textarea id="facilities" name="facilities" placeholder="Pool, restaurant, Wi-Fi">${form.facilities}</textarea>
          </div>
          <div class="form-actions">
            <a href="${pageContext.request.contextPath}/hotels" class="btn btn-outline">Clear</a>
            <button type="submit" class="btn btn-primary">${mode == 'edit' ? 'Save Changes' : 'Create Hotel'}</button>
          </div>
        </form>
      </div>

      <div class="card module-list-card">
        <div class="card-header">
          <div>
            <div class="card-title">Hotel records</div>
            <div class="card-subtitle">Read, update, or delete saved hotels.</div>
          </div>
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Hotel</th>
                <th>Location</th>
                <th>Contact</th>
                <th>Facilities</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty hotels}">
                  <tr><td colspan="6"><div class="empty-state">No hotel records yet.</div></td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="h" items="${hotels}">
                    <tr>
                      <td>#${h.id}</td>
                      <td>
                        <span class="guest-name">${h.hotelName}</span><br>
                        <small>${h.email}</small>
                      </td>
                      <td>${h.location}</td>
                      <td>${h.contactNumber}</td>
                      <td><small>${h.facilities}</small></td>
                      <td>
                        <div class="table-actions">
                          <a href="${pageContext.request.contextPath}/hotels?action=edit&id=${h.id}" class="btn btn-outline btn-sm">Edit</a>
                          <a href="${pageContext.request.contextPath}/hotels?action=delete&id=${h.id}" class="btn btn-danger btn-sm" onclick="return confirm('Delete this hotel record?')">Delete</a>
                        </div>
                      </td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  </main>
</body>
</html>

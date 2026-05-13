<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Rooms - Hotel Management System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <c:set var="currentPage" value="rooms"/>
  <%@ include file="nav.jsp" %>

  <main class="main-content">
    <section class="hero-panel compact-hero">
      <div>
        <p class="hero-kicker">Room dashboard CRUD</p>
        <h1>${sessionScope.authUser.admin ? 'Manage hotel rooms' : 'View rooms'}</h1>
        <p>${sessionScope.authUser.admin ? 'Create rooms, update status, and remove records.' : 'Choose an available room and start a reservation.'}</p>
      </div>
      <c:choose>
        <c:when test="${sessionScope.authUser.admin}">
          <a href="${pageContext.request.contextPath}/rooms?action=add" class="btn btn-primary">New Room</a>
        </c:when>
        <c:otherwise>
          <a href="${pageContext.request.contextPath}/reservations?action=add" class="btn btn-primary">Reserve Room</a>
        </c:otherwise>
      </c:choose>
    </section>

    <section class="page fade-in">
      <c:if test="${sessionScope.authUser.admin}">
        <div class="card">
          <div class="card-header">
            <div>
              <div class="card-title">${mode == 'edit' ? 'Edit room' : 'Create room'}</div>
              <div class="card-subtitle">Room ID is assigned automatically.</div>
            </div>
          </div>
          <form action="${pageContext.request.contextPath}/rooms" method="post" class="form-grid">
            <input type="hidden" name="id" value="${form.id}">
            <div class="form-group">
              <label for="roomNumber">Room Number</label>
              <input type="text" id="roomNumber" name="roomNumber" value="${form.roomNumber}" required>
            </div>
            <div class="form-group">
              <label for="roomType">Room Type</label>
              <select id="roomType" name="roomType" required>
                <option value="Single" ${form.roomType == 'Single' ? 'selected' : ''}>Single</option>
                <option value="Double" ${form.roomType == 'Double' ? 'selected' : ''}>Double</option>
                <option value="Deluxe" ${form.roomType == 'Deluxe' ? 'selected' : ''}>Deluxe</option>
                <option value="Suite" ${form.roomType == 'Suite' ? 'selected' : ''}>Suite</option>
              </select>
            </div>
            <div class="form-group">
              <label for="pricePerNight">Price Per Night</label>
              <input type="number" step="0.01" min="0" id="pricePerNight" name="pricePerNight" value="${form.pricePerNight}" required>
            </div>
            <div class="form-group">
              <label for="status">Status</label>
              <select id="status" name="status" required>
                <option value="Available" ${form.status == 'Available' ? 'selected' : ''}>Available</option>
                <option value="Booked" ${form.status == 'Booked' ? 'selected' : ''}>Booked</option>
                <option value="Maintenance" ${form.status == 'Maintenance' ? 'selected' : ''}>Maintenance</option>
              </select>
            </div>
            <div class="form-group full">
              <label for="image">Image URL</label>
              <input type="url" id="image" name="image" value="${form.image}">
            </div>
            <div class="form-group full">
              <label for="description">Description</label>
              <textarea id="description" name="description">${form.description}</textarea>
            </div>
            <div class="form-actions">
              <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline">Clear</a>
              <button type="submit" class="btn btn-primary">${mode == 'edit' ? 'Save Changes' : 'Create Room'}</button>
            </div>
          </form>
        </div>
      </c:if>

      <div class="room-grid">
        <c:choose>
          <c:when test="${empty rooms}">
            <div class="empty-state card">No rooms found.</div>
          </c:when>
          <c:otherwise>
            <c:forEach var="room" items="${rooms}">
              <article class="room-card">
                <div class="room-image" style="background-image:url('${room.image}')"></div>
                <div class="room-body">
                  <div class="room-title-line">
                    <div>
                      <span class="chip">Room ${room.roomNumber}</span>
                      <h2>${room.roomType}</h2>
                    </div>
                    <span class="badge badge-${room.status}">${room.status}</span>
                  </div>
                  <p>${room.description}</p>
                  <div class="room-price">LKR <fmt:formatNumber value="${room.pricePerNight}" pattern="#,##0.00"/> <small>/ night</small></div>
                  <div class="table-actions">
                    <c:choose>
                      <c:when test="${sessionScope.authUser.admin}">
                        <a href="${pageContext.request.contextPath}/rooms?action=edit&id=${room.id}" class="btn btn-outline btn-sm">Edit</a>
                        <a href="${pageContext.request.contextPath}/rooms?action=delete&id=${room.id}" class="btn btn-danger btn-sm" onclick="return confirm('Delete room ${room.roomNumber}?')">Delete</a>
                      </c:when>
                      <c:otherwise>
                        <c:if test="${room.available}">
                          <a href="${pageContext.request.contextPath}/reservations?action=add&roomId=${room.id}" class="btn btn-primary btn-sm">Reserve</a>
                        </c:if>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>
              </article>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </div>
    </section>
  </main>
</body>
</html>

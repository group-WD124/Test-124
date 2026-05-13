<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Reservations - Hotel Management System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <c:set var="currentPage" value="reservations"/>
  <%@ include file="nav.jsp" %>

  <main class="main-content">
    <section class="hero-panel compact-hero">
      <div>
        <p class="hero-kicker">Reservation CRUD</p>
        <h1>${sessionScope.authUser.admin ? 'Check reservations' : 'My reservations'}</h1>
        <p>Create reservations, update dates or status, and cancel bookings when needed.</p>
      </div>
      <a href="${pageContext.request.contextPath}/reservations?action=add" class="btn btn-primary">New Reservation</a>
    </section>

    <section class="page fade-in">
      <c:if test="${param.msg == 'added'}"><div class="alert alert-success">Reservation created.</div></c:if>
      <c:if test="${param.msg == 'updated'}"><div class="alert alert-success">Reservation updated.</div></c:if>
      <c:if test="${param.msg == 'cancelled'}"><div class="alert alert-danger">Reservation cancelled.</div></c:if>
      <c:if test="${param.msg == 'deleted'}"><div class="alert alert-danger">Reservation deleted.</div></c:if>

      <div class="card reservation-table-card">
        <div class="table-wrap">
          <table class="reservation-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>User</th>
                <th>Room</th>
                <th>Check-In</th>
                <th>Check-Out</th>
                <th>Nights</th>
                <th>Guests</th>
                <th>Status</th>
                <th>Total</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty reservations}">
                  <tr><td colspan="10"><div class="empty-state">No reservations found.</div></td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="r" items="${reservations}">
                    <tr>
                      <td>#${r.id}</td>
                      <td><span class="guest-name">${r.userName}</span></td>
                      <td><span class="chip">${r.roomNumber} - ${r.roomType}</span></td>
                      <td>${r.checkIn}</td>
                      <td>${r.checkOut}</td>
                      <td>${r.nights}</td>
                      <td>${r.numberOfGuests}</td>
                      <td><span class="badge badge-${r.status}">${r.status}</span></td>
                      <td class="money-cell">LKR <fmt:formatNumber value="${r.totalAmount}" pattern="#,##0.00"/></td>
                      <td>
                        <div class="table-actions">
                          <a href="${pageContext.request.contextPath}/reservations?action=edit&id=${r.id}" class="btn btn-outline btn-sm">Edit</a>
                          <a href="${pageContext.request.contextPath}/payments?action=add&reservationId=${r.id}" class="btn btn-outline btn-sm">Pay</a>
                          <c:if test="${r.status != 'Cancelled'}">
                            <a href="${pageContext.request.contextPath}/reservations?action=cancel&id=${r.id}" class="btn btn-danger btn-sm" onclick="return confirm('Cancel reservation #${r.id}?')">Cancel</a>
                          </c:if>
                          <c:if test="${sessionScope.authUser.admin}">
                            <a href="${pageContext.request.contextPath}/reservations?action=delete&id=${r.id}" class="btn btn-danger btn-sm" onclick="return confirm('Delete reservation #${r.id}?')">Delete</a>
                          </c:if>
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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dashboard - Hotel Management System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <c:set var="currentPage" value="dashboard"/>
  <%@ include file="nav.jsp" %>

  <main class="main-content">
    <section class="hero-panel">
      <div>
        <p class="hero-kicker">${isAdmin ? 'Admin dashboard' : 'Guest dashboard'}</p>
        <h1>Hotel Management System</h1>
        <p>${isAdmin ? 'Manage hotels, rooms, reservations, payments, and customer feedback.' : 'Browse rooms, reserve a stay, pay for bookings, and share feedback.'}</p>
      </div>
      <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/rooms" class="btn btn-light">View Rooms</a>
        <a href="${pageContext.request.contextPath}/reservations?action=add" class="btn btn-primary">New Reservation</a>
      </div>
    </section>

    <section class="page fade-in">
      <c:if test="${not empty dbError}">
        <div class="alert alert-danger">Database error: ${dbError}</div>
      </c:if>

      <c:choose>
        <c:when test="${isAdmin}">
          <div class="stats-grid">
            <div class="stat-card" style="--card-accent:#006ce4">
              <div class="stat-icon">Users</div>
              <div class="stat-value">${userCount}</div>
              <div class="stat-label">Registered accounts</div>
            </div>
            <div class="stat-card" style="--card-accent:#008234">
              <div class="stat-icon">Hotels</div>
              <div class="stat-value">${hotelCount}</div>
              <div class="stat-label">Hotel records</div>
            </div>
            <div class="stat-card" style="--card-accent:#febb02">
              <div class="stat-icon">Rooms</div>
              <div class="stat-value">${availableRoomCount}/${roomCount}</div>
              <div class="stat-label">Available rooms</div>
            </div>
            <div class="stat-card" style="--card-accent:#5b45d9">
              <div class="stat-icon">Bookings</div>
              <div class="stat-value">${confirmedCount}</div>
              <div class="stat-label">Confirmed reservations</div>
            </div>
            <div class="stat-card" style="--card-accent:#008234">
              <div class="stat-icon">Revenue</div>
              <div class="stat-value"><fmt:formatNumber value="${totalRevenue}" pattern="#,##0"/></div>
              <div class="stat-label">Paid amount in LKR</div>
            </div>
            <div class="stat-card" style="--card-accent:#d4111e">
              <div class="stat-icon">Rating</div>
              <div class="stat-value"><fmt:formatNumber value="${averageRating}" pattern="0.0"/></div>
              <div class="stat-label">Average feedback</div>
            </div>
          </div>

          <div class="section-heading">
            <div>
              <h2>Reservations</h2>
              <p>Pending: ${pendingCount}, confirmed: ${confirmedCount}, cancelled: ${cancelledCount}.</p>
            </div>
            <a href="${pageContext.request.contextPath}/reservations" class="btn btn-outline btn-sm">View all</a>
          </div>

          <div class="card">
            <div class="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>User</th>
                    <th>Room</th>
                    <th>Check-In</th>
                    <th>Check-Out</th>
                    <th>Guests</th>
                    <th>Status</th>
                    <th>Total</th>
                  </tr>
                </thead>
                <tbody>
                  <c:choose>
                    <c:when test="${empty recentReservations}">
                      <tr><td colspan="8"><div class="empty-state">No reservations yet.</div></td></tr>
                    </c:when>
                    <c:otherwise>
                      <c:forEach var="r" items="${recentReservations}" end="7">
                        <tr>
                          <td>#${r.id}</td>
                          <td><span class="guest-name">${r.userName}</span></td>
                          <td><span class="chip">${r.roomNumber} - ${r.roomType}</span></td>
                          <td>${r.checkIn}</td>
                          <td>${r.checkOut}</td>
                          <td>${r.numberOfGuests}</td>
                          <td><span class="badge badge-${r.status}">${r.status}</span></td>
                          <td>LKR <fmt:formatNumber value="${r.totalAmount}" pattern="#,##0.00"/></td>
                        </tr>
                      </c:forEach>
                    </c:otherwise>
                  </c:choose>
                </tbody>
              </table>
            </div>
          </div>
        </c:when>
        <c:otherwise>
          <div class="property-strip">
            <a class="property-tile tile-blue" href="${pageContext.request.contextPath}/rooms">
              <span>Rooms</span>
              <strong>Find a room</strong>
              <small>View available rooms and prices.</small>
            </a>
            <a class="property-tile tile-gold" href="${pageContext.request.contextPath}/payments">
              <span>Payments</span>
              <strong>Pay bookings</strong>
              <small>Complete or review payment records.</small>
            </a>
            <a class="property-tile tile-green" href="${pageContext.request.contextPath}/feedback">
              <span>Feedback</span>
              <strong>Share a rating</strong>
              <small>Tell the hotel how your stay went.</small>
            </a>
          </div>

          <div class="section-heading">
            <div>
              <h2>My reservations</h2>
              <p>Your current booking history.</p>
            </div>
            <a href="${pageContext.request.contextPath}/reservations?action=add" class="btn btn-primary btn-sm">Reserve room</a>
          </div>

          <div class="card">
            <div class="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Room</th>
                    <th>Check-In</th>
                    <th>Check-Out</th>
                    <th>Status</th>
                    <th>Total</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <c:choose>
                    <c:when test="${empty myReservations}">
                      <tr><td colspan="7"><div class="empty-state">No reservations yet.</div></td></tr>
                    </c:when>
                    <c:otherwise>
                      <c:forEach var="r" items="${myReservations}" end="7">
                        <tr>
                          <td>#${r.id}</td>
                          <td><span class="chip">${r.roomNumber} - ${r.roomType}</span></td>
                          <td>${r.checkIn}</td>
                          <td>${r.checkOut}</td>
                          <td><span class="badge badge-${r.status}">${r.status}</span></td>
                          <td>LKR <fmt:formatNumber value="${r.totalAmount}" pattern="#,##0.00"/></td>
                          <td>
                            <a href="${pageContext.request.contextPath}/payments?action=add&reservationId=${r.id}" class="btn btn-outline btn-sm">Pay</a>
                            <a href="${pageContext.request.contextPath}/feedback?action=add&reservationId=${r.id}" class="btn btn-outline btn-sm">Feedback</a>
                          </td>
                        </tr>
                      </c:forEach>
                    </c:otherwise>
                  </c:choose>
                </tbody>
              </table>
            </div>
          </div>
        </c:otherwise>
      </c:choose>
    </section>
  </main>
</body>
</html>

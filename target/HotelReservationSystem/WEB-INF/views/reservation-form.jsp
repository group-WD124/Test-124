<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${mode == 'edit' ? 'Edit' : 'New'} Reservation - Hotel Management System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <c:set var="currentPage" value="reservations"/>
  <%@ include file="nav.jsp" %>

  <main class="main-content">
    <section class="hero-panel compact-hero">
      <div>
        <p class="hero-kicker">${mode == 'edit' ? 'Update reservation' : 'Create reservation'}</p>
        <h1>${mode == 'edit' ? 'Edit booking' : 'Reserve a room'}</h1>
        <p>Select the guest, room, dates, and number of guests.</p>
      </div>
      <a href="${pageContext.request.contextPath}/reservations" class="btn btn-light">Back to Reservations</a>
    </section>

    <section class="page reservation-page fade-in">
      <div class="card reservation-card">
        <div class="card-header reservation-header">
          <div>
            <div class="card-title">${mode == 'edit' ? 'Edit Reservation' : 'New Reservation'}</div>
            <div class="card-subtitle">Status starts as Pending and becomes Confirmed after a paid payment.</div>
          </div>
        </div>

        <c:if test="${not empty formError}">
          <div class="alert alert-danger form-alert">${formError}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/reservations" method="post" class="form-grid reservation-form-grid">
          <input type="hidden" name="id" value="${form.id}">

          <c:choose>
            <c:when test="${sessionScope.authUser.admin}">
              <div class="form-group">
                <label for="userId">User</label>
                <select id="userId" name="userId" required>
                  <option value="">Select user</option>
                  <c:forEach var="u" items="${users}">
                    <option value="${u.id}" ${form.userId == u.id ? 'selected' : ''}>${u.name} - ${u.email}</option>
                  </c:forEach>
                </select>
              </div>
            </c:when>
            <c:otherwise>
              <input type="hidden" name="userId" value="${sessionScope.authUser.id}">
            </c:otherwise>
          </c:choose>

          <div class="form-group">
            <label for="roomId">Room</label>
            <select id="roomId" name="roomId" required>
              <option value="">Select room</option>
              <c:forEach var="room" items="${rooms}">
                <option value="${room.id}"
                        data-rate="${room.pricePerNight}"
                        ${form.roomId == room.id ? 'selected' : ''}
                        ${(not room.available and form.roomId != room.id) ? 'disabled' : ''}>
                  Room ${room.roomNumber} - ${room.roomType} - LKR <fmt:formatNumber value="${room.pricePerNight}" pattern="#,##0.00"/> (${room.status})
                </option>
              </c:forEach>
            </select>
          </div>

          <div class="form-group">
            <label for="checkIn">Check-In Date</label>
            <input type="date" id="checkIn" name="checkIn" value="${form.checkIn}" required>
          </div>
          <div class="form-group">
            <label for="checkOut">Check-Out Date</label>
            <input type="date" id="checkOut" name="checkOut" value="${form.checkOut}" required>
          </div>
          <div class="form-group">
            <label for="numberOfGuests">Number of Guests</label>
            <input type="number" id="numberOfGuests" name="numberOfGuests" value="${form.numberOfGuests}" min="1" required>
          </div>

          <c:if test="${sessionScope.authUser.admin}">
            <div class="form-group">
              <label for="status">Reservation Status</label>
              <select id="status" name="status">
                <option value="Pending" ${form.status == 'Pending' ? 'selected' : ''}>Pending</option>
                <option value="Confirmed" ${form.status == 'Confirmed' ? 'selected' : ''}>Confirmed</option>
                <option value="Cancelled" ${form.status == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
              </select>
            </div>
          </c:if>

          <div class="booking-summary">
            <div>
              <span class="summary-kicker">Estimated total</span>
              <strong id="totalDue">LKR 0.00</strong>
              <p id="summaryHint">Select a room and date range.</p>
            </div>
            <div class="summary-breakdown">
              <span><b id="summaryRate">LKR 0.00</b><small>Rate per night</small></span>
              <span><b id="summaryNights">0</b><small>Nights</small></span>
              <span><b id="summaryGuests">${form.numberOfGuests}</b><small>Guest(s)</small></span>
            </div>
          </div>

          <div class="form-actions">
            <a href="${pageContext.request.contextPath}/reservations" class="btn btn-outline">Cancel</a>
            <button type="submit" class="btn btn-primary">${mode == 'edit' ? 'Save Changes' : 'Create Reservation'}</button>
          </div>
        </form>
      </div>
    </section>
  </main>

  <script>
    const roomSelect = document.getElementById('roomId');
    const checkIn = document.getElementById('checkIn');
    const checkOut = document.getElementById('checkOut');
    const guests = document.getElementById('numberOfGuests');
    const totalDue = document.getElementById('totalDue');
    const summaryHint = document.getElementById('summaryHint');
    const summaryRate = document.getElementById('summaryRate');
    const summaryNights = document.getElementById('summaryNights');
    const summaryGuests = document.getElementById('summaryGuests');
    const money = new Intl.NumberFormat('en-LK', { style: 'currency', currency: 'LKR' });

    function parseDate(value) {
      return value ? new Date(value + 'T00:00:00') : null;
    }

    function nights() {
      const start = parseDate(checkIn.value);
      const end = parseDate(checkOut.value);
      if (!start || !end) return 0;
      return Math.max(0, Math.round((end - start) / 86400000));
    }

    function updateSummary() {
      const option = roomSelect.options[roomSelect.selectedIndex];
      const rate = option ? Number(option.dataset.rate || 0) : 0;
      const stayNights = nights();
      const guestCount = Math.max(1, Number(guests.value || 1));
      const total = rate * stayNights;
      summaryRate.textContent = money.format(rate);
      summaryNights.textContent = stayNights;
      summaryGuests.textContent = guestCount;
      totalDue.textContent = money.format(total);
      summaryHint.textContent = rate && stayNights ? stayNights + ' night stay.' : 'Select a room and date range.';
    }

    [roomSelect, checkIn, checkOut, guests].forEach(function (field) {
      field.addEventListener('input', updateSummary);
      field.addEventListener('change', updateSummary);
    });
    updateSummary();
  </script>
</body>
</html>

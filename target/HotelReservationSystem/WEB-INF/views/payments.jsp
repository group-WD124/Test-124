<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Payments - Hotel Management System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <c:set var="currentPage" value="payments"/>
  <%@ include file="nav.jsp" %>

  <main class="main-content">
    <section class="hero-panel compact-hero">
      <div>
        <p class="hero-kicker">Payment CRUD</p>
        <h1>${sessionScope.authUser.admin ? 'Check payment records' : 'My payments'}</h1>
        <p>Create payment records, update status, and keep every reservation payment traceable.</p>
      </div>
      <a href="${pageContext.request.contextPath}/payments?action=add" class="btn btn-primary">New Payment</a>
    </section>

    <section class="page fade-in">
      <c:if test="${param.msg == 'added'}"><div class="alert alert-success">Payment saved.</div></c:if>
      <c:if test="${param.msg == 'updated'}"><div class="alert alert-success">Payment updated.</div></c:if>
      <c:if test="${param.msg == 'deleted'}"><div class="alert alert-danger">Payment deleted.</div></c:if>

      <c:if test="${not empty mode}">
        <div class="card">
          <div class="card-header">
            <div>
              <div class="card-title">${mode == 'edit' ? 'Edit payment' : 'Create payment'}</div>
              <div class="card-subtitle">Paid payments confirm the linked reservation.</div>
            </div>
          </div>
          <c:if test="${not empty formError}">
            <div class="alert alert-danger">${formError}</div>
          </c:if>
          <form action="${pageContext.request.contextPath}/payments" method="post" class="form-grid">
            <input type="hidden" name="id" value="${form.id}">
            <div class="form-group full">
              <label for="reservationId">Reservation</label>
              <select id="reservationId" name="reservationId" required>
                <option value="">Select reservation</option>
                <c:forEach var="r" items="${reservations}">
                  <option value="${r.id}" data-total="${r.totalAmount}" ${form.reservationId == r.id ? 'selected' : ''}>
                    #${r.id} - ${r.userName} - Room ${r.roomNumber} - LKR <fmt:formatNumber value="${r.totalAmount}" pattern="#,##0.00"/>
                  </option>
                </c:forEach>
              </select>
            </div>
            <div class="form-group">
              <label for="amount">Amount</label>
              <input type="number" step="0.01" min="0" id="amount" name="amount" value="${form.amount}" required>
            </div>
            <div class="form-group">
              <label for="paymentMethod">Payment Method</label>
              <select id="paymentMethod" name="paymentMethod" required>
                <option value="Cash" ${form.paymentMethod == 'Cash' ? 'selected' : ''}>Cash</option>
                <option value="Card" ${form.paymentMethod == 'Card' ? 'selected' : ''}>Card</option>
                <option value="Bank Transfer" ${form.paymentMethod == 'Bank Transfer' ? 'selected' : ''}>Bank Transfer</option>
                <option value="Online" ${form.paymentMethod == 'Online' ? 'selected' : ''}>Online</option>
              </select>
            </div>
            <div class="form-group">
              <label for="paymentDate">Payment Date</label>
              <input type="date" id="paymentDate" name="paymentDate" value="${form.paymentDate}" required>
            </div>
            <div class="form-group">
              <label for="status">Payment Status</label>
              <select id="status" name="status" required>
                <option value="Pending" ${form.status == 'Pending' ? 'selected' : ''}>Pending</option>
                <option value="Paid" ${form.status == 'Paid' ? 'selected' : ''}>Paid</option>
                <option value="Failed" ${form.status == 'Failed' ? 'selected' : ''}>Failed</option>
                <c:if test="${sessionScope.authUser.admin}">
                  <option value="Refunded" ${form.status == 'Refunded' ? 'selected' : ''}>Refunded</option>
                </c:if>
              </select>
            </div>
            <div class="form-actions">
              <a href="${pageContext.request.contextPath}/payments" class="btn btn-outline">Cancel</a>
              <button type="submit" class="btn btn-primary">${mode == 'edit' ? 'Save Changes' : 'Save Payment'}</button>
            </div>
          </form>
        </div>
      </c:if>

      <div class="card reservation-table-card">
        <div class="table-wrap">
          <table class="reservation-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Reservation</th>
                <th>User</th>
                <th>Amount</th>
                <th>Method</th>
                <th>Date</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty payments}">
                  <tr><td colspan="8"><div class="empty-state">No payment records found.</div></td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="p" items="${payments}">
                    <tr>
                      <td>#${p.id}</td>
                      <td>${p.reservationLabel}</td>
                      <td><span class="guest-name">${p.userName}</span></td>
                      <td class="money-cell">LKR <fmt:formatNumber value="${p.amount}" pattern="#,##0.00"/></td>
                      <td>${p.paymentMethod}</td>
                      <td>${p.paymentDate}</td>
                      <td><span class="badge badge-${p.status}">${p.status}</span></td>
                      <td>
                        <div class="table-actions">
                          <a href="${pageContext.request.contextPath}/payments?action=edit&id=${p.id}" class="btn btn-outline btn-sm">Edit</a>
                          <a href="${pageContext.request.contextPath}/payments?action=delete&id=${p.id}" class="btn btn-danger btn-sm" onclick="return confirm('Delete payment #${p.id}?')">Delete</a>
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

  <script>
    const reservation = document.getElementById('reservationId');
    const amount = document.getElementById('amount');
    if (reservation && amount) {
      reservation.addEventListener('change', function () {
        const selected = reservation.options[reservation.selectedIndex];
        if (selected && selected.dataset.total && (!amount.value || Number(amount.value) === 0)) {
          amount.value = Number(selected.dataset.total).toFixed(2);
        }
      });
    }
  </script>
</body>
</html>

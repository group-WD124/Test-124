<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Feedback - Hotel Management System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <c:set var="currentPage" value="feedback"/>
  <%@ include file="nav.jsp" %>

  <main class="main-content">
    <section class="hero-panel compact-hero">
      <div>
        <p class="hero-kicker">Feedback CRUD</p>
        <h1>${sessionScope.authUser.admin ? 'Customer feedback' : 'My feedback'}</h1>
        <p>${sessionScope.authUser.admin ? 'Read and manage customer feedback records.' : 'Give a rating and comment for one of your reservations.'}</p>
      </div>
      <c:if test="${not sessionScope.authUser.admin}">
        <a href="${pageContext.request.contextPath}/feedback?action=add" class="btn btn-primary">New Feedback</a>
      </c:if>
    </section>

    <section class="page fade-in">
      <c:if test="${param.msg == 'added'}"><div class="alert alert-success">Feedback saved.</div></c:if>
      <c:if test="${param.msg == 'updated'}"><div class="alert alert-success">Feedback updated.</div></c:if>
      <c:if test="${param.msg == 'deleted'}"><div class="alert alert-danger">Feedback deleted.</div></c:if>

      <c:if test="${not empty mode and not sessionScope.authUser.admin}">
        <div class="card">
          <div class="card-header">
            <div>
              <div class="card-title">${mode == 'edit' ? 'Edit feedback' : 'Create feedback'}</div>
              <div class="card-subtitle">Ratings are stored from 1 to 5.</div>
            </div>
          </div>
          <c:if test="${not empty formError}">
            <div class="alert alert-danger">${formError}</div>
          </c:if>
          <form action="${pageContext.request.contextPath}/feedback" method="post" class="form-grid">
            <input type="hidden" name="id" value="${form.id}">
            <div class="form-group full">
              <label for="reservationId">Reservation</label>
              <select id="reservationId" name="reservationId" required>
                <option value="">Select reservation</option>
                <c:forEach var="r" items="${reservations}">
                  <option value="${r.id}" ${form.reservationId == r.id ? 'selected' : ''}>
                    #${r.id} - Room ${r.roomNumber} - ${r.checkIn} to ${r.checkOut}
                  </option>
                </c:forEach>
              </select>
            </div>
            <div class="form-group">
              <label for="rating">Rating</label>
              <select id="rating" name="rating" required>
                <option value="5" ${form.rating == 5 ? 'selected' : ''}>5 - Excellent</option>
                <option value="4" ${form.rating == 4 ? 'selected' : ''}>4 - Good</option>
                <option value="3" ${form.rating == 3 ? 'selected' : ''}>3 - Average</option>
                <option value="2" ${form.rating == 2 ? 'selected' : ''}>2 - Poor</option>
                <option value="1" ${form.rating == 1 ? 'selected' : ''}>1 - Very Poor</option>
              </select>
            </div>
            <div class="form-group">
              <label for="feedbackDate">Feedback Date</label>
              <input type="date" id="feedbackDate" name="feedbackDate" value="${form.feedbackDate}" required>
            </div>
            <div class="form-group full">
              <label for="comment">Comment</label>
              <textarea id="comment" name="comment">${form.comment}</textarea>
            </div>
            <div class="form-actions">
              <a href="${pageContext.request.contextPath}/feedback" class="btn btn-outline">Cancel</a>
              <button type="submit" class="btn btn-primary">${mode == 'edit' ? 'Save Changes' : 'Save Feedback'}</button>
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
                <th>User</th>
                <th>Reservation</th>
                <th>Rating</th>
                <th>Comment</th>
                <th>Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty feedbackList}">
                  <tr><td colspan="7"><div class="empty-state">No feedback records found.</div></td></tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="f" items="${feedbackList}">
                    <tr>
                      <td>#${f.id}</td>
                      <td><span class="guest-name">${f.userName}</span></td>
                      <td>${f.reservationLabel}</td>
                      <td><span class="rating-pill">${f.rating}/5</span></td>
                      <td>${f.comment}</td>
                      <td>${f.feedbackDate}</td>
                      <td>
                        <div class="table-actions">
                          <c:if test="${not sessionScope.authUser.admin}">
                            <a href="${pageContext.request.contextPath}/feedback?action=edit&id=${f.id}" class="btn btn-outline btn-sm">Edit</a>
                          </c:if>
                          <a href="${pageContext.request.contextPath}/feedback?action=delete&id=${f.id}" class="btn btn-danger btn-sm" onclick="return confirm('Delete feedback #${f.id}?')">Delete</a>
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

<%-- Shared top navigation partial --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="site-header">
  <div class="site-header-inner">
    <a class="brand" href="${pageContext.request.contextPath}/dashboard" aria-label="Hotel Management dashboard">
      <span class="brand-mark">HM</span>
      <span class="brand-name">HotelManage</span>
    </a>

    <nav class="primary-nav" aria-label="Main navigation">
      <a href="${pageContext.request.contextPath}/dashboard"
         class="nav-item ${currentPage == 'dashboard' ? 'active' : ''}">
        <span class="icon">D</span> Dashboard
      </a>
      <c:if test="${sessionScope.authUser.admin}">
        <a href="${pageContext.request.contextPath}/hotels"
           class="nav-item ${currentPage == 'hotels' ? 'active' : ''}">
          <span class="icon">H</span> Hotels
        </a>
      </c:if>
      <a href="${pageContext.request.contextPath}/rooms"
         class="nav-item ${currentPage == 'rooms' ? 'active' : ''}">
        <span class="icon">R</span> Rooms
      </a>
      <a href="${pageContext.request.contextPath}/reservations"
         class="nav-item ${currentPage == 'reservations' ? 'active' : ''}">
        <span class="icon">B</span> Reservations
      </a>
      <a href="${pageContext.request.contextPath}/payments"
         class="nav-item ${currentPage == 'payments' ? 'active' : ''}">
        <span class="icon">P</span> Payments
      </a>
      <a href="${pageContext.request.contextPath}/feedback"
         class="nav-item ${currentPage == 'feedback' ? 'active' : ''}">
        <span class="icon">F</span> Feedback
      </a>
    </nav>

    <div class="header-actions">
      <span class="currency-pill">${sessionScope.authUser.role}</span>
      <span class="user-pill"><c:out value="${sessionScope.authUser.name}"/></span>
      <a href="${pageContext.request.contextPath}/logout" class="btn btn-light btn-sm">Logout</a>
    </div>
  </div>
</header>

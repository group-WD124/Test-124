<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Register - Hotel Management System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-body">
  <main class="auth-shell">
    <section class="auth-stage">
      <div class="auth-visual auth-visual-register">
        <div class="auth-visual-content">
          <a class="brand auth-brand auth-brand-visual" href="${pageContext.request.contextPath}/login" aria-label="HotelManage register">
            <span class="brand-mark">HM</span>
            <span class="brand-name">HotelManage</span>
          </a>
          <span class="auth-pill">Guest account setup</span>
          <h1>Create your stay profile</h1>
          <p>Register once to browse rooms, manage reservations, pay online, and leave post-stay feedback.</p>
          <div class="auth-highlights">
            <span>Fast reservations</span>
            <span>Payment tracking</span>
            <span>Feedback history</span>
          </div>
        </div>
      </div>

      <section class="auth-panel auth-panel-glass">
        <div class="auth-copy">
          <span class="auth-eyebrow">Guest access</span>
          <h2>Create Account</h2>
          <p>User accounts can view rooms, reserve stays, make payments, and leave feedback.</p>
        </div>

        <c:if test="${not empty error}">
          <div class="alert alert-danger auth-alert">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post" class="auth-form">
          <div class="auth-field">
            <label for="name">Full Name</label>
            <div class="auth-input-wrap">
              <input type="text" id="name" name="name" value="${name}" placeholder="Lakvin Perera" autocomplete="name" required>
            </div>
          </div>
          <div class="auth-field">
            <label for="email">Email</label>
            <div class="auth-input-wrap">
              <input type="email" id="email" name="email" value="${email}" placeholder="you@example.com" autocomplete="email" required>
            </div>
          </div>
          <div class="auth-field">
            <label for="phone">Contact Number</label>
            <div class="auth-input-wrap">
              <input type="tel" id="phone" name="phone" value="${phone}" placeholder="0771234567" autocomplete="tel">
            </div>
          </div>
          <div class="auth-field">
            <label for="password">Password</label>
            <div class="auth-input-wrap">
              <input type="password" id="password" name="password" minlength="4" placeholder="Create a password" autocomplete="new-password" required>
            </div>
          </div>
          <button type="submit" class="btn auth-submit">Create Account</button>
        </form>

        <p class="auth-switch">Already registered? <a href="${pageContext.request.contextPath}/login">Sign In</a></p>
      </section>
    </section>
  </main>
</body>
</html>

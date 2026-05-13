<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="Sign in to Hotel Management System — manage rooms, reservations, payments, and guest feedback.">
  <title>Sign In — Hotel Management System</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800;900&family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-body">
  <main class="auth-shell">
    <section class="auth-stage">

      <!-- ── LEFT PANEL: Branding & Visual ───────────────────────────── -->
      <div class="auth-visual">
        <div class="auth-visual-content">
          <a class="brand auth-brand auth-brand-visual" href="${pageContext.request.contextPath}/login" aria-label="Hotel Management System">
            <span class="brand-mark">HM</span>
            <span class="brand-name">HotelManage</span>
          </a>

          <span class="auth-pill">Hotel Management System</span>

          <h1>Welcome<br>Back</h1>

          <p>Manage rooms, reservations, payments, and guest feedback all in one elegant place.</p>

          <div class="auth-highlights">
            <span>🛏 Room operations</span>
            <span>📅 Reservations</span>
            <span>💳 Guest payments</span>
            <span>⭐ Feedback</span>
          </div>
        </div>
      </div>

      <!-- ── RIGHT PANEL: Sign-in Form ───────────────────────────────── -->
      <section class="auth-panel auth-panel-glass">
        <div class="auth-copy">
          <span class="auth-eyebrow">Secure access</span>
          <h2>Sign In</h2>
          <p>Use an Admin or User account to continue into the hotel dashboard.</p>
        </div>

        <c:if test="${param.msg == 'loggedout'}">
          <div class="alert alert-success auth-alert">You have been successfully logged out.</div>
        </c:if>
        <c:if test="${not empty error}">
          <div class="alert alert-danger auth-alert">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" class="auth-form" autocomplete="on">
          <!-- Email -->
          <div class="auth-field">
            <label for="email">Email address</label>
            <div class="auth-input-wrap">
              <input type="email" id="email" name="email" value="${email}"
                     placeholder="you@hotel.com"
                     autocomplete="email" required>
              <span class="auth-input-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24">
                  <rect x="2" y="4" width="20" height="16" rx="2"/>
                  <path d="M2 7l10 7 10-7"/>
                </svg>
              </span>
            </div>
          </div>

          <!-- Password -->
          <div class="auth-field">
            <label for="password">Password</label>
            <div class="auth-input-wrap">
              <input type="password" id="password" name="password"
                     placeholder="Enter your password"
                     autocomplete="current-password" required>
              <span class="auth-input-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24">
                  <rect x="3" y="11" width="18" height="11" rx="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
              </span>
            </div>
          </div>

          <!-- Remember me / Forgot -->
          <div class="auth-tools">
            <label class="auth-checkbox">
              <input type="checkbox" name="rememberMe">
              <span>Remember me</span>
            </label>
            <a class="auth-forgot" href="mailto:admin@hotel.com?subject=Password%20assistance">Forgot password?</a>
          </div>

          <button type="submit" class="btn auth-submit" id="signInBtn">Sign In</button>
        </form>

        <!-- Demo credentials -->
        <div class="demo-logins" aria-label="Demo credentials">
          <span class="demo-item">
            <strong>Admin</strong>
            <em>admin@hotel.com / admin123</em>
          </span>
          <span class="demo-item">
            <strong>User</strong>
            <em>user@hotel.com / user123</em>
          </span>
        </div>

        <p class="auth-switch">Don't have an account? <a href="${pageContext.request.contextPath}/register">Create account</a></p>
      </section>

    </section>
  </main>
</body>
</html>

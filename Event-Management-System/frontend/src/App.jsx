import React from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import LandingPage from './pages/LandingPage';
import UserDashboard from './pages/UserDashboard';
import AdminDashboard from './pages/AdminDashboard';
import EventList from './pages/EventList';
import EventDetails from './pages/EventDetails';
import PaymentPage from './pages/PaymentPage';
import BookingSuccessPage from './pages/BookingSuccessPage';
import TicketPage from './pages/TicketPage';
import FeedbackPage from './pages/FeedbackPage';
import UserNotificationsPage from './pages/UserNotificationsPage';
import AdminManageEventsPage from './pages/AdminManageEventsPage';
import EventAnalyticsPage from './pages/EventAnalyticsPage';
import AdminManageUsersPage from './pages/AdminManageUsersPage';
import UserAnalyticsPage from './pages/UserAnalyticsPage';
import AdminNotificationCenter from './pages/AdminNotificationCenter';
import AdminFeedbackDashboardPage from './pages/AdminFeedbackDashboardPage';
import UserFeedbackPage from './pages/UserFeedbackPage';
import UserProfilePage from './pages/UserProfilePage';
import ProtectedRoute from './components/ProtectedRoute';
import Header from './components/Header';
import Footer from './components/Footer';
import HeaderAdmin from './components/HeaderAdmin';
import ForgotPasswordPage from './pages/ForgotPasswordPage';

const App = () => {
  const { token, role } = useSelector((state) => state.auth);
  const location = useLocation();

  const hideHeaderFooter = ['/login', '/register','/forgot-password'].includes(location.pathname);

  return (
    <>
     
     {!hideHeaderFooter && (role === 'ADMIN' ? <HeaderAdmin /> : <Header />)}

      <main className="main-content">
      <Routes>
        {/* Public */}
        <Route path="/" element={<LandingPage />} />
        <Route
          path="/login"
          element={token ? <Navigate to={role === 'ADMIN' ? '/dashboard/admin' : '/dashboard/user'} /> : <LoginPage />}
        />
        <Route path="/register" element={<RegisterPage />} />
        <Route path='/forgot-password' element={<ForgotPasswordPage />} />

        {/* User Dashboard */}
        <Route
          path="/dashboard/user"
          element={token && role === 'USER' ? <UserDashboard /> : <Navigate to="/login" />}
        />

        {/* Admin Dashboard */}
        <Route
          path="/dashboard/admin"
          element={token && role === 'ADMIN' ? <AdminDashboard /> : <Navigate to="/login" />}
        />

        {/* Events */}
        <Route
          path="/events"
          element={token && role === 'USER' ? <EventList /> : <Navigate to="/login" />}
        />
        <Route
          path="/event/:id"
          element={token && role === 'USER' ? <EventDetails /> : <Navigate to="/login" />}
        />

        {/* Booking */}
        <Route path="/payment/:eventId" element={<PaymentPage />} />
        <Route
          path="/booked/:ticketId"
          element={
            <ProtectedRoute>
              <BookingSuccessPage />
            </ProtectedRoute>
          }
        />

        {/* User Features */}
        <Route
          path="/tickets"
          element={token && role === 'USER' ? <TicketPage /> : <Navigate to="/login" />}
        />
        <Route
          path="/feedback/:eventId"
          element={token && role === 'USER' ? <FeedbackPage /> : <Navigate to="/login" />}
        />
        <Route
          path="/notifications"
          element={token && role === 'USER' ? <UserNotificationsPage /> : <Navigate to="/login" />}
        />
        <Route
          path="/user/feedbacks"
          element={token && role === 'USER' ? <UserFeedbackPage /> : <Navigate to="/login" />}
        />
        <Route
          path="/profile"
          element={token && role === 'USER' ? <UserProfilePage /> : <Navigate to="/login" />}
        />

        {/* Admin Features */}
        <Route
          path="/admin/events/manage"
          element={token && role === 'ADMIN' ? <AdminManageEventsPage /> : <Navigate to="/login" />}
        />
        <Route
          path="/admin/analytics/:eventId"
          element={token && role === 'ADMIN' ? <EventAnalyticsPage /> : <Navigate to="/login" />}
        />
        <Route
          path="/admin/users"
          element={token && role === 'ADMIN' ? <AdminManageUsersPage /> : <Navigate to="/login" />}
        />
        <Route
          path="/admin/user/:userId/analytics"
          element={token && role === 'ADMIN' ? <UserAnalyticsPage /> : <Navigate to="/login" />}
        />
        <Route
          path="/admin/notifications"
          element={token && role === 'ADMIN' ? <AdminNotificationCenter /> : <Navigate to="/login" />}
        />
        <Route
          path="/admin/feedbacks"
          element={token && role === 'ADMIN' ? <AdminFeedbackDashboardPage /> : <Navigate to="/login" />}
        />
      </Routes>
      </main>

      {location.pathname === '/dashboard/user' && <Footer />}
      
    </>
  );
};

export default App;

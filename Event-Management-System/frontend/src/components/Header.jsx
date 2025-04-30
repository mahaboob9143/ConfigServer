import React from 'react';
import { Navbar, Nav, Container, NavDropdown, Image } from 'react-bootstrap';
import { useNavigate, useLocation } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { FaUserCircle, FaTicketAlt, FaBell, FaComments } from 'react-icons/fa';
import { logout } from '../redux/authSlice';
import logo from '../assets/logo.png';
 
const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();
  const { token, role } = useSelector((state) => state.auth);
 
  const handleLogout = () => {
    dispatch(logout());
    navigate('/');
  };
 
  const isLandingPage = location.pathname === '/';
  const dashboardPath = role === 'ADMIN' ? '/dashboard/admin' : '/dashboard/user';
 
  return (
    <Navbar
      expand="lg"
      sticky="top"
      variant="dark"
      className="px-3 shadow-sm"
      style={{
        background: 'linear-gradient(to right, #6a0dad, #b86d00)',
        fontFamily: 'Segoe UI, sans-serif',
        fontWeight: 500,
        zIndex: 9999,
      }}
    >
      <Container fluid>
        {/* ✅ Brand & Logo */}
        <Navbar.Brand
          onClick={() => navigate(token ? dashboardPath : '/')}
          style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', fontSize: '1.3rem' }}
        >
          <Image
            src={logo}
            alt="EMS Logo"
            height="30"
            className="me-2"
            style={{ borderRadius: '4px' }}
          />
          Event Management
        </Navbar.Brand>
 
        <Navbar.Toggle aria-controls="navbar-user" />
        <Navbar.Collapse id="navbar-user">
          <Nav className="ms-auto align-items-center gap-3">
            {/* ✅ Show only if NOT on Landing Page and role is USER */}
            {!isLandingPage && role === 'USER' && (
              <>
                <Nav.Link onClick={() => navigate('/tickets')} className="text-white d-flex align-items-center">
                  <FaTicketAlt className="me-2" />
                  Tickets
                </Nav.Link>
                <Nav.Link onClick={() => navigate('/user/feedbacks')} className="text-white d-flex align-items-center">
                  <FaComments className="me-2" />
                  Feedbacks
                </Nav.Link>
                <Nav.Link onClick={() => navigate('/notifications')} className="text-white d-flex align-items-center">
                  <FaBell className="me-2" />
                  Notifications
                </Nav.Link>
              </>
            )}
 
            {/* ✅ Profile dropdown – shown only if not on Landing Page */}
            {token && !isLandingPage && (
              <NavDropdown
                align="end"
                title={<FaUserCircle className="text-white" size={24} />}
                id="user-profile-dropdown"
              >
                <NavDropdown.Item onClick={() => navigate('/profile')}>
                  Edit Profile
                </NavDropdown.Item>
                <NavDropdown.Divider />
                <NavDropdown.Item onClick={handleLogout}>Logout</NavDropdown.Item>
              </NavDropdown>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};
 
export default Header;
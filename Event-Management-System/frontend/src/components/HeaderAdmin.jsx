import React from 'react';
import { Navbar, Nav, Container, NavDropdown, Image } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { FaUserCircle } from 'react-icons/fa';
import { useDispatch } from 'react-redux';
import { logout } from '../redux/authSlice';
import logo from '../assets/logo.png'; // ✅ Your logo

const HeaderAdmin = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <Navbar
      expand="lg"
      sticky="top"
      className="px-3 shadow-sm"
      style={{
        background: 'linear-gradient(to right, #6f42c1, #d4af37)',
        fontWeight: 500,
        zIndex: 9999,
      }}
    >
      <Container fluid>
        {/* ✅ Logo + Brand */}
        <Navbar.Brand
          onClick={() => navigate('/dashboard/admin')}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            cursor: 'pointer',
            color: '#fff',
            fontSize: '1.3rem',
            fontWeight: 'bold',
          }}
        >
          <Image src={logo} height="30" width="30" roundedCircle />
          Event Management
        </Navbar.Brand>

        <Navbar.Toggle aria-controls="admin-navbar" />
        <Navbar.Collapse id="admin-navbar">
          <Nav className="ms-auto align-items-center">
            <NavDropdown
              align="end"
              title={<FaUserCircle size={24} className="text-white" />}
              id="admin-profile-dropdown"
            >
              <NavDropdown.Item onClick={handleLogout}>Logout</NavDropdown.Item>
            </NavDropdown>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default HeaderAdmin;

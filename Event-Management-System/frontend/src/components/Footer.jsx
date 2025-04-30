import React from 'react';
import { Container, Row, Col, Image } from 'react-bootstrap';
import { FaFacebookF, FaInstagram } from 'react-icons/fa';
import logo from '../assets/logo.png'; // ✅ Add your logo here

const Footer = () => {
  return (
    <footer
      style={{
        background: '#6f42c1',
        color: 'white',
        padding: '16px 0',
        fontSize: '0.9rem',
        marginTop: 'auto',
      }}
    >
      <Container>
        <Row className="align-items-center justify-content-between">
          <Col xs={12} md={4} className="mb-2 mb-md-0 d-flex align-items-center gap-2">
            <Image src={logo} height="30" width="30" roundedCircle />
            <span style={{ fontWeight: 'bold' }}>Event Management</span>
          </Col>

          <Col xs={12} md={4} className="text-center mb-2 mb-md-0">
            © {new Date().getFullYear()} Event Management. All rights reserved.
          </Col>

          <Col xs={12} md={4} className="text-md-end text-center">
            <FaFacebookF className="me-3" style={{ cursor: 'pointer' }} />
            <FaInstagram style={{ cursor: 'pointer' }} />
          </Col>
        </Row>
      </Container>
    </footer>
  );
};

export default Footer;

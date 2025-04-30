import React from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { BsCalendarEvent, BsChatLeftDots, BsBell, BsPeople } from 'react-icons/bs';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
  const navigate = useNavigate();

  const cardStyle = {
    boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
    borderRadius: '12px',
    transition: 'transform 0.2s ease',
    cursor: 'pointer',
  };

  const iconStyle = { fontSize: '2rem', marginBottom: '10px' };

  return (
    <Container className="mt-5">
      <h2 className="mb-4 text-center">Welcome, Admin!</h2>

      {/* Summary cards */}
      <Row className="mb-5">
        <Col md={3}>
          <Card bg="primary" text="white" className="text-center" style={cardStyle}>
            <Card.Body>
              <BsCalendarEvent style={iconStyle} />
              <Card.Title>Total Events</Card.Title>
              <Card.Text style={{ fontSize: '1.8rem' }}>--</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card bg="success" text="white" className="text-center" style={cardStyle}>
            <Card.Body>
              <BsChatLeftDots style={iconStyle} />
              <Card.Title>Feedback</Card.Title>
              <Card.Text style={{ fontSize: '1.8rem' }}>--</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card bg="warning" text="dark" className="text-center" style={cardStyle}>
            <Card.Body>
              <BsBell style={iconStyle} />
              <Card.Title>Notifications</Card.Title>
              <Card.Text style={{ fontSize: '1.8rem' }}>--</Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card bg="info" text="white" className="text-center" style={cardStyle}>
            <Card.Body>
              <BsPeople style={iconStyle} />
              <Card.Title>Users</Card.Title>
              <Card.Text style={{ fontSize: '1.8rem' }}>--</Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Quick Actions */}
      <Row className="text-center">
        <Col md={3}>
          <Button variant="outline-primary" className="w-100" onClick={() => navigate('/admin/events/manage')}>
            Manage Events
          </Button>
        </Col>
        <Col md={3}>
          <Button variant="outline-success" className="w-100" onClick={() => navigate('/admin/feedbacks')}>
            View Feedbacks
          </Button>
        </Col>
        <Col md={3}>
          <Button variant="outline-warning" className="w-100" onClick={() => navigate('/admin/notifications')}>
            Notification Center
          </Button>
        </Col>
        <Col md={3}>
          <Button variant="outline-info" className="w-100" onClick={() => navigate('/admin/users')}>
            Manage Users
          </Button>
        </Col>
      </Row>
    </Container>
  );
};

export default AdminDashboard;

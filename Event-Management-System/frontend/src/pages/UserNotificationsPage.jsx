import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { Container, Alert, Button, Spinner, Row, Col, Card } from 'react-bootstrap';
import { BsBell, BsX } from 'react-icons/bs';

const UserNotificationsPage = () => {
  const token = useSelector((state) => state.auth.token);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');

  const fetchNotifications = async () => {
    try {
      const response = await axios.get('http://localhost:9090/notifications-api/user', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setNotifications(response.data);
    } catch (error) {
      console.error('Failed to fetch notifications', error);
      setErrorMsg('Unable to load notifications.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleDismiss = async (notificationId) => {
    const confirm = window.confirm('Are you sure you want to dismiss this notification?');
    if (!confirm) return;

    try {
      await axios.put(`http://localhost:9090/notifications-api/soft-delete/${notificationId}`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setNotifications((prev) => prev.filter((n) => n.notificationId !== notificationId));
    } catch (error) {
      console.error('Failed to dismiss notification', error);
      setErrorMsg('Error while deleting notification');
    }
  };

  const cardStyle = {
    transition: 'all 0.3s ease',
    boxShadow: '0 4px 10px rgba(0,0,0,0.1)',
    borderRadius: '8px',
    marginBottom: '20px',
    padding: '10px',
    backgroundColor: '#fff',
  };

  const cardHoverStyle = {
    ...cardStyle,
    backgroundColor: '#f9f9f9',
    transform: 'scale(1.01)',
  };

  const titleStyle = {
    fontSize: '1.2rem',
    fontWeight: '600',
  };

  const messageStyle = {
    fontSize: '0.95rem',
    color: '#555',
  };

  return (
    <Container className="mt-5">
      <h3 className="text-center mb-4" style={{ fontWeight: 'bold' }}>
        <BsBell /> My Notifications
      </h3>

      {loading ? (
        <div className="text-center mt-5">
          <Spinner animation="border" />
          <p className="mt-2">Loading notifications...</p>
        </div>
      ) : errorMsg ? (
        <Alert variant="danger" className="text-center">{errorMsg}</Alert>
      ) : notifications.length === 0 ? (
        <Alert variant="info" className="text-center">No notifications available.</Alert>
      ) : (
        <Row className="justify-content-center">
          {notifications.map((note) => (
            <Col md={8} key={note.notificationId}>
              <Card
                style={cardStyle}
                onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#f9f9f9')}
                onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = '#fff')}
              >
                <Card.Body className="d-flex justify-content-between align-items-center">
                  <div>
                    <div style={titleStyle}>{note.title}</div>
                    <div style={messageStyle}>
                      {note.message}
                      <br />
                      
                    </div>
                  </div>
                  <Button
                    variant="outline-danger"
                    size="sm"
                    onClick={() => handleDismiss(note.notificationId)}
                  >
                    <BsX size={20} />
                  </Button>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}
    </Container>
  );
};

export default UserNotificationsPage;

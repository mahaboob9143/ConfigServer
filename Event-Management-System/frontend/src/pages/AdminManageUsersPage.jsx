
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Container, Row, Col, Card, Spinner, Alert } from 'react-bootstrap';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';

const AdminManageUsersPage = () => {
  const token = useSelector((state) => state.auth.token);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const res = await axios.get('http://localhost:9090/user-api/view', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUsers(res.data);
      } catch (err) {
        console.error('Failed to fetch users', err);
        setErrorMsg('Unable to load user data.');
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [token]);

  const cardStyle = {
    cursor: 'pointer',
    transition: 'transform 0.2s ease',
    boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
    borderRadius: '8px',
  };

  return (
    <Container className="mt-5">
      <h3 className="text-center mb-4">👥 Manage Users</h3>

      {loading ? (
        <div className="text-center">
          <Spinner animation="border" />
          <p>Loading users...</p>
        </div>
      ) : errorMsg ? (
        <Alert variant="danger" className="text-center">
          {errorMsg}
        </Alert>
      ) : (
        <Row>
          {users.map((user) => (
            <Col md={4} key={user.userId} className="mb-4">
              <Card
                style={cardStyle}
                onClick={() => navigate(`/admin/user/${user.userId}/analytics`)}
                onMouseEnter={(e) => (e.currentTarget.style.transform = 'scale(1.02)')}
                onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1.0)')}
              >
                <Card.Body>
                  <Card.Title>User ID: {user.userId}</Card.Title>
                  <Card.Text>Email: {user.email}</Card.Text>
                  <Card.Text>Role: {user.role}</Card.Text>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}
    </Container>
  );
};

export default AdminManageUsersPage;

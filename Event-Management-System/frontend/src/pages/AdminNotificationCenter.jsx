import React, { useEffect, useState } from 'react';
import {
  Container,
  Table,
  Spinner,
  Alert,
  Button,
  Form,
  Row,
  Col,
  Badge,
} from 'react-bootstrap';
import axios from 'axios';
import { useSelector } from 'react-redux';

const AdminNotificationCenter = () => {
  const token = useSelector((state) => state.auth.token);
  const [notifications, setNotifications] = useState([]);
  const [users, setUsers] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState('');
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  const fetchAllNotifications = async () => {
    try {
      setLoading(true);
      const res = await axios.get('http://localhost:9090/notifications-api/all', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setNotifications(res.data);
    } catch (err) {
      setErrorMsg('Failed to load notifications');
    } finally {
      setLoading(false);
    }
  };

  const fetchUsers = async () => {
    try {
      const res = await axios.get('http://localhost:9090/user-api/view', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUsers(res.data);
    } catch (err) {
      console.error('Failed to fetch users');
    }
  };

  const fetchUserNotifications = async (userId) => {
    if (!userId) return fetchAllNotifications();
    try {
      setLoading(true);
      const res = await axios.get(`http://localhost:9090/notifications-api/admin/user/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setNotifications(res.data);
    } catch (err) {
      setErrorMsg('Failed to load user notifications');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    const confirm = window.confirm('Are you sure you want to delete this notification?');
    if (!confirm) return;

    try {
      await axios.put(`http://localhost:9090/notifications-api/admin/delete/${id}`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setSuccessMsg('Notification deleted successfully!');
      setTimeout(() => setSuccessMsg(''), 3000);
      selectedUserId ? fetchUserNotifications(selectedUserId) : fetchAllNotifications();
    } catch (err) {
      setErrorMsg('Failed to delete notification');
    }
  };

  useEffect(() => {
    fetchAllNotifications();
    fetchUsers();
  }, []);

  const filtered = notifications.filter(
    (n) =>
      n.message?.toLowerCase().includes(keyword.toLowerCase())
  );

  return (
    <Container className="mt-5">
      <h3 className="text-center mb-4">📢 Admin Notification Center</h3>

      {errorMsg && <Alert variant="danger">{errorMsg}</Alert>}
      {successMsg && <Alert variant="success">{successMsg}</Alert>}

      <Row className="mb-3">
        <Col md={4}>
          <Form.Select
            value={selectedUserId}
            onChange={(e) => {
              setSelectedUserId(e.target.value);
              fetchUserNotifications(e.target.value);
            }}
          >
            <option value="">🔍 Filter by User</option>
            {users.map((user) => (
              <option key={user.userId} value={user.userId}>
                {user.name} ({user.email})
              </option>
            ))}
          </Form.Select>
        </Col>
        <Col md={4}>
          <Form.Control
            placeholder="🔎 Search message"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
        </Col>
        <Col md={4}>
          <Button variant="outline-secondary" onClick={fetchAllNotifications}>
            🔄 Reset
          </Button>
        </Col>
      </Row>

      {loading ? (
        <div className="text-center">
          <Spinner animation="border" />
          <p>Loading notifications...</p>
        </div>
      ) : (
        <Table bordered hover responsive>
          <thead className="table-dark">
            <tr>
              <th>ID</th>
              <th>Message</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan="4" className="text-center">
                  No notifications found.
                </td>
              </tr>
            ) : (
              filtered.map((note) => (
                <tr key={note.notificationId}>
                  <td>{note.notificationId}</td>
                  <td>{note.message}</td>
                  <td>
                    <Badge bg={note.active ? 'success' : 'secondary'}>
                      {note.active ? 'Active' : 'Deleted'}
                    </Badge>
                  </td>
                  <td>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      onClick={() => handleDelete(note.notificationId)}
                      disabled={!note.active}
                    >
                      🗑️ Delete
                    </Button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </Table>
      )}
    </Container>
  );
};

export default AdminNotificationCenter;

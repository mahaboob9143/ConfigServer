import React, { useEffect, useState } from 'react';
import {
  Container, Table, Spinner, Alert, Form, Button, Badge
} from 'react-bootstrap';
import axios from 'axios';
import { useSelector } from 'react-redux';

const AdminFeedbackDashboardPage = () => {
  const token = useSelector((state) => state.auth.token);
  const [feedbacks, setFeedbacks] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [deletedIds, setDeletedIds] = useState([]); // ✅ Track deleted feedbacks
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchAllFeedbacks();
  }, []);

  const fetchAllFeedbacks = async () => {
    setLoading(true);
    setErrorMsg('');
    try {
      const all = [];

      const eventRes = await axios.get('http://localhost:9090/events-api/view/all', {
        headers: { Authorization: `Bearer ${token}` },
      });

      const events = eventRes.data;

      for (const event of events) {
        try {
          const res = await axios.get(`http://localhost:9090/feedback-api/event/${event.eventId}`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          all.push(...res.data);
        } catch (e) {
          console.error(`No feedbacks for event ID ${event.eventId}`);
        }
      }

      setFeedbacks(all);
      setFiltered(all);
      setDeletedIds([]); // Reset deleted IDs
    } catch (err) {
      setErrorMsg('Failed to load feedbacks.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    const query = e.target.value.toLowerCase();
    setSearchQuery(query);

    const result = feedbacks.filter(
      (f) =>
        f.userName?.toLowerCase().includes(query) ||
        f.eventName?.toLowerCase().includes(query)
    );
    setFiltered(result);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this feedback?')) return;

    try {
      await axios.delete(`http://localhost:9090/feedback-api/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setSuccessMsg('Feedback deleted successfully.');
      setDeletedIds((prev) => [...prev, id]); // ✅ Mark as deleted (disable button)
      setTimeout(() => setSuccessMsg(''), 3000);
    } catch (err) {
      setErrorMsg('Failed to delete feedback.');
    }
  };

  return (
    <Container className="mt-5">
      <h3 className="text-center mb-4">📊 Admin Feedback Dashboard</h3>

      {errorMsg && <Alert variant="danger">{errorMsg}</Alert>}
      {successMsg && <Alert variant="success">{successMsg}</Alert>}

      <Form.Control
        type="text"
        placeholder="🔍 Search by Event or User"
        value={searchQuery}
        onChange={handleSearch}
        className="mb-4"
      />

      {loading ? (
        <div className="text-center">
          <Spinner animation="border" />
          <p>Loading feedbacks...</p>
        </div>
      ) : (
        <Table bordered hover responsive>
          <thead className="table-dark">
            <tr>
              <th>#</th>
              <th>Event</th>
              <th>User</th>
              <th>Message</th>
              <th>Rating</th>
              <th>Date</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan="7" className="text-center text-muted">
                  No feedbacks found.
                </td>
              </tr>
            ) : (
              filtered.map((fb, idx) => (
                <tr key={fb.feedbackId}>
                  <td>{idx + 1}</td>
                  <td>{fb.eventName}</td>
                  <td>{fb.userName}</td>
                  <td>{fb.message}</td>
                  <td><Badge bg="warning" text="dark">{fb.rating} ⭐</Badge></td>
                  <td>{new Date(fb.feedbackDate).toLocaleString()}</td>
                  <td>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      onClick={() => handleDelete(fb.feedbackId)}
                      disabled={deletedIds.includes(fb.feedbackId)} // ✅ Disable if deleted
                    >
                      {deletedIds.includes(fb.feedbackId) ? 'Deleted' : 'Delete'}
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

export default AdminFeedbackDashboardPage;

import React, { useEffect, useState } from 'react';
import {
  Container,
  Table,
  Form,
  Row,
  Col,
  Button,
  Alert,
  Spinner,
} from 'react-bootstrap';
import axios from 'axios';
import { useSelector } from 'react-redux';

const UserFeedbackPage = () => {
  const token = useSelector((state) => state.auth.token);
  const email = token ? JSON.parse(atob(token.split('.')[1])).sub : null;

  const [userId, setUserId] = useState(null);
  const [feedbacks, setFeedbacks] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');

  // Filters
  const [eventFilter, setEventFilter] = useState('');
  const [ratingFilter, setRatingFilter] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  // Get userId by email
  const fetchUserId = async () => {
    try {
      const res = await axios.get(`http://localhost:9090/user-api/email/${email}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUserId(res.data);
    } catch (err) {
      setErrorMsg('Unable to fetch user details');
      setLoading(false);
    }
  };

  // Get feedbacks by userId
  const fetchFeedbacks = async (uid) => {
    try {
      const res = await axios.get(`http://localhost:9090/feedback-api/user/${uid}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setFeedbacks(res.data);
      setFiltered(res.data);
    } catch (err) {
      setErrorMsg('Unable to fetch feedbacks');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (email) fetchUserId();
  }, [email]);

  useEffect(() => {
    if (userId) fetchFeedbacks(userId);
  }, [userId]);

  const applyFilters = () => {
    let list = [...feedbacks];

    if (eventFilter) {
      list = list.filter((f) =>
        f.eventName?.toLowerCase().includes(eventFilter.toLowerCase())
      );
    }

    if (ratingFilter) {
      list = list.filter((f) => f.rating === parseInt(ratingFilter));
    }

    if (startDate) {
      const start = new Date(startDate);
      start.setHours(0, 0, 0, 0);
      list = list.filter((f) => new Date(f.feedbackDate) >= start);
    }

    if (endDate) {
      const end = new Date(endDate);
      end.setHours(23, 59, 59, 999);
      list = list.filter((f) => new Date(f.feedbackDate) <= end);
    }

    setFiltered(list);
  };

  const resetFilters = () => {
    setEventFilter('');
    setRatingFilter('');
    setStartDate('');
    setEndDate('');
    setFiltered(feedbacks);
  };

  return (
    <Container className="mt-5">
      <h3 className="text-center mb-4">📝 My Feedbacks</h3>

      {errorMsg && <Alert variant="danger">{errorMsg}</Alert>}

      {/* Filters */}
      <Row className="mb-3">
        <Col md={3}>
          <Form.Control
            placeholder="🔍 Filter by Event"
            value={eventFilter}
            onChange={(e) => setEventFilter(e.target.value)}
          />
        </Col>
        <Col md={2}>
          <Form.Select
            value={ratingFilter}
            onChange={(e) => setRatingFilter(e.target.value)}
          >
            <option value="">⭐ Rating</option>
            {[1, 2, 3, 4, 5].map((r) => (
              <option key={r} value={r}>{r} Star</option>
            ))}
          </Form.Select>
        </Col>
        <Col md={2}>
          <Form.Control
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
        </Col>
        <Col md={2}>
          <Form.Control
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />
        </Col>
        <Col md={3} className="d-flex gap-2">
          <Button variant="outline-primary" onClick={applyFilters}>
            Apply
          </Button>
          <Button variant="outline-danger" onClick={resetFilters}>
            Clear Filters
          </Button>
        </Col>
      </Row>

      {/* Feedback Count */}
      <div className="text-end text-muted mb-2">
        Showing <strong>{filtered.length}</strong> feedback{filtered.length !== 1 && 's'}
      </div>

      {/* Feedback Table */}
      {loading ? (
        <div className="text-center my-5">
          <Spinner animation="border" />
          <p className="mt-2">Loading feedback...</p>
        </div>
      ) : (
        <Table bordered hover responsive>
          <thead className="table-dark">
            <tr>
              <th>Event</th>
              <th>Message</th>
              <th>Rating</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan="4" className="text-center text-muted">
                  No feedback found.
                </td>
              </tr>
            ) : (
              filtered.map((fb) => (
                <tr key={fb.feedbackId}>
                  <td>{fb.eventName}</td>
                  <td>{fb.message}</td>
                  <td>{fb.rating} ⭐</td>
                  <td>{new Date(fb.feedbackDate).toLocaleString()}</td>
                </tr>
              ))
            )}
          </tbody>
        </Table>
      )}
    </Container>
  );
};

export default UserFeedbackPage;

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Container, Table, Button, Modal, Form, Alert, Badge, Row, Col } from 'react-bootstrap';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';

const AdminManageEventsPage = () => {
  const token = useSelector((state) => state.auth.token);
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [currentEvent, setCurrentEvent] = useState(null);
  const [formData, setFormData] = useState({ name: '', category: '', location: '', date: '' });
  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchEvents();
  }, []);

  const autoDismissMsg = () => {
    setTimeout(() => {
      setErrorMsg('');
      setSuccessMsg('');
    }, 3000);
  };

  const fetchEvents = async () => {
    try {
      const res = await axios.get('http://localhost:9090/events-api/view/all', {
        headers: { Authorization: `Bearer ${token}` },
      });
      setEvents(res.data);
      setFilteredEvents(res.data);
    } catch (err) {
      setErrorMsg('Failed to load events');
      autoDismissMsg();
    }
  };

  const handleSearch = (e) => {
    const value = e.target.value.toLowerCase();
    setSearchTerm(value);
    const filtered = events.filter(event =>
      event.name.toLowerCase().includes(value) ||
      event.category.toLowerCase().includes(value) ||
      event.location.toLowerCase().includes(value)
    );
    setFilteredEvents(filtered);
  };

  const handleDelete = async (eventId) => {
    const confirm = window.confirm('Are you sure you want to delete this event?');
    if (!confirm) return;

    try {
      await axios.delete(`http://localhost:9090/events-api/${eventId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setSuccessMsg('Event deleted!');
      fetchEvents();
    } catch (err) {
      setErrorMsg('Error deleting event');
    } finally {
      autoDismissMsg();
    }
  };

  const openEditModal = (event) => {
    setCurrentEvent(event);
    setFormData({ ...event, date: event.date?.substring(0, 16) || '' });
    setShowModal(true);
  };

  const openAddModal = () => {
    setCurrentEvent(null);
    setFormData({ name: '', category: '', location: '', date: '' });
    setShowModal(true);
  };

  const handleFormSubmit = async () => {
    try {
      const endpoint = currentEvent
        ? `http://localhost:9090/events-api/${currentEvent.eventId}`
        : 'http://localhost:9090/events-api/events';
      const method = currentEvent ? 'put' : 'post';

      await axios[method](endpoint, formData, {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      });

      setSuccessMsg(currentEvent ? 'Event updated!' : 'Event created!');
      setShowModal(false);
      fetchEvents();
    } catch (err) {
      setErrorMsg('Failed to save event.');
    } finally {
      autoDismissMsg();
    }
  };

  return (
    <Container className="mt-5">
      <h3 className="mb-4 text-center">Manage Events</h3>

      {errorMsg && <Alert variant="danger">{errorMsg}</Alert>}
      {successMsg && <Alert variant="success">{successMsg}</Alert>}

      <Row className="mb-3 align-items-center">
        <Col md={6}>
          <Button variant="primary" onClick={openAddModal}>➕ Add New Event</Button>
        </Col>
        <Col md={6} className="text-end">
          <Form.Control
            type="text"
            placeholder="🔍 Search by Name, Category or Location"
            value={searchTerm}
            onChange={handleSearch}
          />
        </Col>
      </Row>

      <Table bordered hover responsive>
        <thead className="table-dark">
          <tr>
            <th>#</th>
            <th>Name</th>
            <th>Category</th>
            <th>Location</th>
            <th>Date</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {filteredEvents.map((event, idx) => (
            <tr key={event.eventId}>
              <td>{idx + 1}</td>
              <td>{event.name}</td>
              <td>{event.category}</td>
              <td>{event.location}</td>
              <td>{new Date(event.date).toLocaleString()}</td>
              <td>
                <Badge bg={event.active ? 'success' : 'secondary'}>
                  {event.active ? 'Active' : 'Inactive'}
                </Badge>
              </td>
              <td>
                <Button
                  size="sm"
                  variant="warning"
                  className="me-2"
                  onClick={() => openEditModal(event)}
                  disabled={!event.active}
                >
                  Edit
                </Button>
                <Button
                  size="sm"
                  variant="danger"
                  className="me-2"
                  onClick={() => handleDelete(event.eventId)}
                  disabled={!event.active}
                >
                  Delete
                </Button>
                <Button
                  size="sm"
                  variant="info"
                  onClick={() => navigate(`/admin/analytics/${event.eventId}`)}
                >
                  Analytics
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      {/* Modal for Add/Edit */}
      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>{currentEvent ? 'Edit Event' : 'Add New Event'}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group className="mb-3">
              <Form.Label>Name</Form.Label>
              <Form.Control
                type="text"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Category</Form.Label>
              <Form.Control
                type="text"
                value={formData.category}
                onChange={(e) => setFormData({ ...formData, category: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Location</Form.Label>
              <Form.Control
                type="text"
                value={formData.location}
                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Date & Time</Form.Label>
              <Form.Control
                type="datetime-local"
                value={formData.date}
                onChange={(e) => setFormData({ ...formData, date: e.target.value })}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>Cancel</Button>
          <Button variant="success" onClick={handleFormSubmit}>
            {currentEvent ? 'Update' : 'Create'}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default AdminManageEventsPage;

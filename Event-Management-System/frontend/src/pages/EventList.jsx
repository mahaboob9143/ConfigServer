import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Form, Button, Spinner, Alert } from 'react-bootstrap';
import EventCard from '../components/EventCard';
import { getAllEvents } from '../services/eventService';
import { FaSearch } from 'react-icons/fa';

const EventList = () => {
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [statusFilter, setStatusFilter] = useState('All');

  const categories = ['Technology', 'Art', 'Sports', 'Culture','Festival','Party'];
  const statuses = ['All', 'Upcoming', 'Ongoing', 'Past'];

  useEffect(() => {
    const fetchData = async () => {
      try {
        const data = await getAllEvents();
        setEvents(data);
        setFilteredEvents(data);
      } catch (err) {
        setError('Failed to load events');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleCategoryToggle = (category) => {
    const updated = selectedCategories.includes(category)
      ? selectedCategories.filter((c) => c !== category)
      : [...selectedCategories, category];
    setSelectedCategories(updated);
  };

  const applyFilters = () => {
    let result = [...events];

    if (searchTerm.trim()) {
      result = result.filter((event) =>
        event.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        event.location.toLowerCase().includes(searchTerm.toLowerCase()) ||
        event.category.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    if (selectedCategories.length) {
      result = result.filter((event) =>
        selectedCategories.includes(event.category)
      );
    }

    if (statusFilter !== 'All') {
      const now = new Date();
      result = result.filter((event) => {
        const eventDate = new Date(event.date);
        if (statusFilter === 'Upcoming') return eventDate > now;
        if (statusFilter === 'Ongoing') return eventDate.toDateString() === now.toDateString();
        if (statusFilter === 'Past') return eventDate < now;
        return true;
      });
    }

    setFilteredEvents(result);
  };

  useEffect(() => {
    applyFilters();
  }, [searchTerm, selectedCategories, statusFilter, events]);

  const clearFilters = () => {
    setSearchTerm('');
    setSelectedCategories([]);
    setStatusFilter('All');
  };

  return (
    <Container className="mt-4">
      <h2 className="text-center mb-4" style={{ color: '#5e2ca5' }}>Events</h2>

      <div
        style={{
          backgroundColor: '#fff9ff',
          padding: '20px 25px',
          borderRadius: '16px',
          boxShadow: '0 3px 15px rgba(0,0,0,0.1)',
          marginBottom: '40px',
        }}
      >
        {/* Search Bar */}
        <Row className="align-items-center mb-4">
          <Col md={10}>
            <Form.Control
              type="text"
              placeholder="Search events..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{
                borderRadius: '30px',
                padding: '10px 20px',
                fontSize: '0.95rem',
                border: '2px solid #6f42c1',
              }}
            />
          </Col>
          <Col md={2} className="text-end">
            <Button
              variant="outline-secondary"
              onClick={clearFilters}
              style={{
                borderRadius: '25px',
                fontWeight: 500,
                padding: '8px 18px',
              }}
            >
              Clear
            </Button>
          </Col>
        </Row>

        {/* Categories */}
        <Row className="mb-3">
          <Col>
            <div style={{ fontWeight: 600, color: '#5e2ca5', marginBottom: '8px' }}>Categories</div>
            {categories.map((cat) => (
              <Button
                key={cat}
                variant={selectedCategories.includes(cat) ? 'primary' : 'outline-primary'}
                onClick={() => handleCategoryToggle(cat)}
                className="me-2 mb-2"
                style={{
                  borderRadius: '20px',
                  fontWeight: 500,
                  padding: '6px 18px',
                  fontSize: '0.85rem',
                }}
              >
                {cat}
              </Button>
            ))}
          </Col>
        </Row>

        {/* Status Filter */}
        <Row>
          <Col>
            <div style={{ fontWeight: 600, color: '#5e2ca5', marginBottom: '8px' }}>Status</div>
            {statuses.map((status) => (
              <Button
                key={status}
                variant={statusFilter === status ? 'dark' : 'outline-dark'}
                onClick={() => setStatusFilter(status)}
                className="me-2 mb-2"
                style={{
                  borderRadius: '20px',
                  fontWeight: 500,
                  padding: '6px 18px',
                  fontSize: '0.85rem',
                }}
              >
                {status}
              </Button>
            ))}
          </Col>
        </Row>
      </div>

      {/* Events Display */}
      {loading ? (
        <div className="text-center"><Spinner animation="border" /></div>
      ) : error ? (
        <Alert variant="danger">{error}</Alert>
      ) : filteredEvents.length === 0 ? (
        <Alert variant="info" className="text-center">No events found.</Alert>
      ) : (
        <Row className="g-4">
          {filteredEvents.map((event) => (
            <Col key={event.eventId} sm={12} md={6} lg={4}>
              <EventCard event={event} />
            </Col>
          ))}
        </Row>
      )}
    </Container>
  );
};

export default EventList;

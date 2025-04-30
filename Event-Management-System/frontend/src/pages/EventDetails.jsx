import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import API from '../services/axiosConfig';
import { Button, Card, Container, Spinner } from 'react-bootstrap';

const EventDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    API.get(`/events-api/${id}`)
      .then((res) => {
        setEvent(res.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Error fetching event details:', err);
        setLoading(false);
      });
  }, [id]);

  const handleBook = () => {
    navigate(`/payment/${id}`);
  };

  if (loading) {
    return <div className="text-center my-5"><Spinner animation="border" /></div>;
  }

  if (!event) {
    return <div className="text-center text-danger mt-5">Event not found</div>;
  }

  return (
    <Container className="my-4">
      <Card>
        <Card.Body>
          <Card.Title>{event.name}</Card.Title>
          <Card.Subtitle className="mb-2 text-muted">{event.category}</Card.Subtitle>
          <Card.Text>{event.description}</Card.Text>
          <p><strong>Date:</strong> {event.date}</p>
          <p><strong>Location:</strong> {event.location}</p>
          <p><strong>Created By:</strong> {event.organizer || 'Admin'}</p>
          <Button variant="success" onClick={handleBook}>Book Ticket</Button>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default EventDetails;

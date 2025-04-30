import React from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import EventCarousel from '../components/EventCarousel';
import EventList from './EventList';

const UserDashboard = () => {
  const navigate = useNavigate();
  const { token } = useSelector((state) => state.auth);

  const getEmailFromToken = () => {
    if (!token) return '';
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub;
    } catch (err) {
      return '';
    }
  };

  const email = getEmailFromToken();

  return (
    <>
      <EventCarousel />

      <Container className="mt-4">
        <h3 className="text-center mb-4">Welcome, {email}</h3>
        <EventList/>
        
      </Container>
    </>
  );
};

export default UserDashboard;

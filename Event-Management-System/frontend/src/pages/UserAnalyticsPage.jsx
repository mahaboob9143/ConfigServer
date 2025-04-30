import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Card, Spinner, Alert } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { useSelector } from 'react-redux';
import { Bar } from 'react-chartjs-2';

import {
  Chart as ChartJS,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(BarElement, CategoryScale, LinearScale, Tooltip, Legend);

const UserAnalyticsPage = () => {
  const { userId } = useParams();
  const token = useSelector((state) => state.auth.token);
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    const fetchUserAnalytics = async () => {
      try {
        const res = await axios.get(`http://localhost:9090/user-api/${userId}/analytics`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        setAnalytics(res.data);
      } catch (err) {
        console.error('Error fetching user analytics:', err);
        setErrorMsg('Failed to load user analytics');
      } finally {
        setLoading(false);
      }
    };

    if (userId) fetchUserAnalytics();
  }, [userId, token]);

  const chartData = analytics && {
    labels: ['Total Tickets', 'Cancelled Tickets', 'Events Participated', 'Feedbacks'],
    datasets: [
      {
        label: 'User Stats',
        data: [
          analytics.totalTickets,
          analytics.cancelledTickets,
          analytics.eventsParticipated,
          analytics.feedbacksSubmitted
        ],
        backgroundColor: ['#007bff', '#dc3545', '#28a745', '#ffc107'],
      },
    ],
  };

  return (
    <Container className="mt-5">
      <h3 className="text-center mb-4">👤 User Analytics</h3>

      {loading ? (
        <div className="text-center">
          <Spinner animation="border" />
          <p>Loading user analytics...</p>
        </div>
      ) : errorMsg ? (
        <Alert variant="danger" className="text-center">{errorMsg}</Alert>
      ) : (
        <>
          <Row className="mb-4 justify-content-center">
            <Col md={3}>
              <Card bg="primary" text="white" className="text-center mb-3">
                <Card.Body>
                  <Card.Title>Total Tickets</Card.Title>
                  <Card.Text style={{ fontSize: '1.5rem' }}>{analytics.totalTickets}</Card.Text>
                </Card.Body>
              </Card>
            </Col>

            <Col md={3}>
              <Card bg="danger" text="white" className="text-center mb-3">
                <Card.Body>
                  <Card.Title>Cancelled Tickets</Card.Title>
                  <Card.Text style={{ fontSize: '1.5rem' }}>{analytics.cancelledTickets}</Card.Text>
                </Card.Body>
              </Card>
            </Col>

            <Col md={3}>
              <Card bg="success" text="white" className="text-center mb-3">
                <Card.Body>
                  <Card.Title>Events Participated</Card.Title>
                  <Card.Text style={{ fontSize: '1.5rem' }}>{analytics.eventsParticipated}</Card.Text>
                </Card.Body>
              </Card>
            </Col>

            <Col md={3}>
              <Card bg="warning" text="dark" className="text-center mb-3">
                <Card.Body>
                  <Card.Title>Feedbacks</Card.Title>
                  <Card.Text style={{ fontSize: '1.5rem' }}>{analytics.feedbacksSubmitted}</Card.Text>
                </Card.Body>
              </Card>
            </Col>
          </Row>

          <h5 className="text-center mt-4 mb-3">📊 Summary Chart</h5>
          <Bar data={chartData} />
        </>
      )}
    </Container>
  );
};

export default UserAnalyticsPage;

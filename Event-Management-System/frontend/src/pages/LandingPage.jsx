import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Container, Row, Col, Carousel, Card } from 'react-bootstrap';
import Footer from '../components/Footer';
import banner1 from '../assets/carousel1.png';
import banner2 from '../assets/carousel2.png';
import banner3 from '../assets/carousel3.png';

const LandingPage = () => {
  const navigate = useNavigate();
  const categories = ['Technology', 'Art', 'Sports', 'Culture'];

  const getImageForCategory = (category) => {
    const formatted = category?.toLowerCase();
    const available = ['technology', 'art', 'sports', 'culture', 'festival'];
    return available.includes(formatted)
      ? `/images/${formatted}.jpg`
      : '/images/default.jpg';
  };

  return (
    <div style={{ backgroundColor: '#fffbe6', minHeight: '100vh' }}>
      {/* Hero Carousel */}
      <div style={{ marginTop: '20px', padding: '0 20px' }}>
        <Carousel fade interval={3000} pause="hover" className="rounded-4 overflow-hidden shadow">
          {[banner1, banner2, banner3].map((img, idx) => (
            <Carousel.Item key={idx}>
              <img
                className="d-block w-100"
                src={img}
                alt={`Slide ${idx}`}
                style={{ height: '500px', objectFit: 'cover' }}
              />
              <Carousel.Caption
                style={{
                  background: 'rgba(0,0,0,0.5)',
                  borderRadius: '12px',
                  padding: '12px 24px',
                }}
              >
                <h3>Art Exhibition</h3>
                <p>Don't miss a day of creativity, fun, laughter, and surprises.</p>
                <Button variant="light" onClick={() => navigate('/login')}>Learn More</Button>
              </Carousel.Caption>
            </Carousel.Item>
          ))}
        </Carousel>
      </div>

      {/* Search & Filter */}
      <Container className="py-5">
        <div className="text-center mb-4">
          <input
            type="text"
            placeholder="Search events..."
            className="px-4 py-2"
            style={{
              borderRadius: '25px',
              border: '2px solid purple',
              outline: 'none',
              width: '60%',
              fontSize: '1rem',
              marginBottom: '20px'
            }}
            onFocus={() => navigate('/login')}
          />
          <div className="d-flex justify-content-center flex-wrap gap-2">
            {categories.map((cat, i) => (
              <Button
                key={i}
                variant="outline-primary"
                style={{
                  borderRadius: '30px',
                  padding: '6px 18px',
                  border: '2px solid purple',
                  color: 'purple',
                  fontWeight: 'bold'
                }}
                onClick={() => navigate('/login')}
              >
                {cat}
              </Button>
            ))}
          </div>
        </div>

        {/* Event Cards */}
        <Row className="g-4 justify-content-center">
          {categories.map((cat, index) => (
            <Col key={index} xs={12} sm={6} md={4} lg={3}>
              <Card className="h-100 shadow-sm text-center rounded-4">
                <Card.Img
                  variant="top"
                  src={getImageForCategory(cat)}
                  alt={cat}
                  style={{ height: '180px', objectFit: 'cover' }}
                />
                <Card.Body>
                  <Card.Title>{`${cat} of India`}</Card.Title>
                  <Card.Text>May 13, 2025</Card.Text>
                  <Button variant="primary" onClick={() => navigate('/login')}>View Details</Button>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>

        {/* Popular Categories */}
        <h4 className="text-center mt-5 mb-3" style={{ color: 'purple' }}>Popular Categories</h4>
        <div className="d-flex justify-content-center gap-3 flex-wrap">
          {categories.map((cat, index) => (
            <Button
              key={index}
              variant="outline-dark"
              style={{ borderRadius: '50px', padding: '10px 24px' }}
              onClick={() => navigate('/login')}
            >
              {cat}
            </Button>
          ))}
        </div>
      </Container>

      {/* Footer */}
      <Footer />
    </div>
  );
};

export default LandingPage;

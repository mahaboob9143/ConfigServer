import React from 'react';
import { Carousel, Container } from 'react-bootstrap';
import carousel1 from '../assets/carousel1.png';
import carousel2 from '../assets/carousel2.png';
import carousel3 from '../assets/carousel3.png';

const EventCarousel = () => {
  const slides = [
    { image: carousel1, title: 'Discover Amazing Events', caption: 'Book your seat now!' },
    { image: carousel2, title: 'Unforgettable Moments', caption: 'Join the excitement.' },
    { image: carousel3, title: 'New Events Weekly', caption: 'Don’t miss out!' },
  ];

  return (
    <Container fluid className="px-0 mt-3 mb-4">
      <Carousel fade interval={3000} pause="hover">
        {slides.map((slide, idx) => (
          <Carousel.Item key={idx}>
            <img
              className="d-block w-100"
              src={slide.image}
              alt={slide.title}
              style={{
                height: '500px',
                objectFit: 'cover',
                borderRadius: '8px',
              }}
            />
            <Carousel.Caption
              style={{
                background: 'rgba(0, 0, 0, 0.5)',
                borderRadius: '10px',
                padding: '14px 24px',
                backdropFilter: 'blur(3px)',
              }}
            >
              <h4>{slide.title}</h4>
              <p>{slide.caption}</p>
            </Carousel.Caption>
          </Carousel.Item>
        ))}
      </Carousel>
    </Container>
  );
};

export default EventCarousel;

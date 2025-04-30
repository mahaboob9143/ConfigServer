import React, { useState } from 'react';
import { Form, Button, Container, Alert } from 'react-bootstrap';
import { registerUser } from '../services/authService';
import { useNavigate } from 'react-router-dom';
 
const RegisterPage = () => {
  const navigate = useNavigate();
 
  const [user, setUser] = useState({
    name: '',
    email: '',
    password: '',
    contactNumber: '',
    role: 'USER',
    adminCode: ''
  });
 
  const [errors, setErrors] = useState({});
  const [serverMessage, setServerMessage] = useState('');
 
  const validate = () => {
    const errs = {};
    if (!user.name || user.name.length < 3) errs.name = 'Name must be at least 3 characters';
    if (!/^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$/.test(user.email)) errs.email = 'Invalid email format';
    if (!user.password || user.password.length < 6) errs.password = 'Password must be at least 6 characters';
    if (!/^\d{10}$/.test(user.contactNumber)) errs.contactNumber = 'Phone must be 10 digits';
 
    if (user.role === 'ADMIN' && user.adminCode !== '2025CTS)$') {
      errs.adminCode = 'Invalid admin code';
    }
 
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };
 
  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: '' });
  };
 
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
 
    const finalData = {
      name: user.name,
      email: user.email,
      password: user.password,
      contactNumber: user.contactNumber,
      role: user.role === 'ADMIN' && user.adminCode === '2025CTS)$' ? 'ADMIN' : 'USER'
    };
 
    try {
      await registerUser(finalData);
      setServerMessage('Registered successfully! Redirecting to login...');
      setTimeout(() => navigate('/login'), 2000);
    } catch (error) {
      console.error('REGISTRATION FAILED:', error);
      const backendMsg = error.response?.data || 'Registration failed. Please try again.';
      setServerMessage(`${backendMsg}`);
    }
  };
 
  return (
    <div
      style={{
        backgroundImage: `url("/images/login-bg.jpg")`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px'
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '500px',
          padding: '30px',
          borderRadius: '20px',
          background: 'rgba(255, 255, 255, 0.1)',
          backdropFilter: 'blur(16px)',
          WebkitBackdropFilter: 'blur(16px)',
          border: '1px solid rgba(255, 255, 255, 0.3)',
          color: '#fff',
          boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.37)'
        }}
      >
        <h3 className="text-center mb-4">User Registration</h3>
        {serverMessage && <Alert variant="info">{serverMessage}</Alert>}
 
        <Form onSubmit={handleSubmit} noValidate>
          <Form.Group className="mb-3">
            <Form.Label style={{ color: '#fff' }}>Name</Form.Label>
            <Form.Control
              type="text"
              name="name"
              value={user.name}
              onChange={handleChange}
              isInvalid={!!errors.name}
              style={{
                background: 'rgba(255, 255, 255, 0.2)',
                border: '1px solid rgba(255,255,255,0.3)',
                color: '#fff',
                borderRadius: '10px'
              }}
            />
            <Form.Control.Feedback type="invalid">{errors.name}</Form.Control.Feedback>
          </Form.Group>
 
          <Form.Group className="mb-3">
            <Form.Label style={{ color: '#fff' }}>Email</Form.Label>
            <Form.Control
              type="email"
              name="email"
              value={user.email}
              onChange={handleChange}
              isInvalid={!!errors.email}
              style={{
                background: 'rgba(255, 255, 255, 0.2)',
                border: '1px solid rgba(255,255,255,0.3)',
                color: '#fff',
                borderRadius: '10px'
              }}
            />
            <Form.Control.Feedback type="invalid">{errors.email}</Form.Control.Feedback>
          </Form.Group>
 
          <Form.Group className="mb-3">
            <Form.Label style={{ color: '#fff' }}>Password</Form.Label>
            <Form.Control
              type="password"
              name="password"
              value={user.password}
              onChange={handleChange}
              isInvalid={!!errors.password}
              style={{
                background: 'rgba(255, 255, 255, 0.2)',
                border: '1px solid rgba(255,255,255,0.3)',
                color: '#fff',
                borderRadius: '10px'
              }}
            />
            <Form.Control.Feedback type="invalid">{errors.password}</Form.Control.Feedback>
          </Form.Group>
 
          <Form.Group className="mb-3">
            <Form.Label style={{ color: '#fff' }}>Contact Number</Form.Label>
            <Form.Control
              type="text"
              name="contactNumber"
              value={user.contactNumber}
              onChange={handleChange}
              isInvalid={!!errors.contactNumber}
              style={{
                background: 'rgba(255, 255, 255, 0.2)',
                border: '1px solid rgba(255,255,255,0.3)',
                color: '#fff',
                borderRadius: '10px'
              }}
            />
            <Form.Control.Feedback type="invalid">{errors.contactNumber}</Form.Control.Feedback>
          </Form.Group>
 
          <Form.Group className="mb-3">
            <Form.Label style={{ color: '#fff' }}>Role</Form.Label>
            <Form.Select
              name="role"
              value={user.role}
              onChange={handleChange}
              style={{
                background: 'rgba(255, 255, 255, 0.2)',
                border: '1px solid rgba(255,255,255,0.3)',
                color: '#fff',
                borderRadius: '10px'
              }}
            >
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </Form.Select>
          </Form.Group>
 
          {user.role === 'ADMIN' && (
            <Form.Group className="mb-3">
              <Form.Label style={{ color: '#fff' }}>Admin Code</Form.Label>
              <Form.Control
                type="text"
                name="adminCode"
                value={user.adminCode}
                onChange={handleChange}
                isInvalid={!!errors.adminCode}
                style={{
                  background: 'rgba(255, 255, 255, 0.2)',
                  border: '1px solid rgba(255,255,255,0.3)',
                  color: '#fff',
                  borderRadius: '10px'
                }}
              />
              <Form.Control.Feedback type="invalid">{errors.adminCode}</Form.Control.Feedback>
            </Form.Group>
          )}
 
          <Button variant="light" type="submit" className="w-100 fw-bold" style={{ borderRadius: '10px' }}>
            Register
          </Button>
        </Form>
 
        <div className="text-center mt-3" style={{ color: '#ddd' }}>
          Already registered? <a href="/login" style={{ color: '#fff', fontWeight: 500 }}>Login here</a>
        </div>
      </div>
    </div>
  );
};
 
export default RegisterPage;
 
 
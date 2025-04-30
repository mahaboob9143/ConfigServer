import React, { useState } from 'react';
import { Form, Button, Container, Alert, InputGroup } from 'react-bootstrap';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { loginSuccess } from '../redux/authSlice';
import { loginUser } from '../services/authService';
import { FaEye, FaEyeSlash } from 'react-icons/fa';

const LoginPage = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [emailError, setEmailError] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [loginError, setLoginError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const validateEmail = (email) => /^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$/.test(email);

  const handleEmailChange = (e) => {
    const value = e.target.value;
    setEmail(value);
    setEmailError('');
    if (!validateEmail(value)) setEmailError('Invalid email format');
  };

  const handlePasswordChange = (e) => {
    const value = e.target.value;
    setPassword(value);
    setPasswordError('');
    if (value.length < 6) setPasswordError('Password must be at least 6 characters');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoginError('');

    if (!email || !validateEmail(email)) {
      setEmailError('Enter a valid email');
      return;
    }

    if (!password || password.length < 6) {
      setPasswordError('Password must be at least 6 characters');
      return;
    }

    try {
      const response = await loginUser({ email, password });
      const token = response.token;
      const payload = JSON.parse(atob(token.split('.')[1]));
      const role = payload.role;

      dispatch(loginSuccess({ token, role }));
      navigate(role === 'ADMIN' ? '/dashboard/admin' : '/dashboard/user');
    } catch (error) {
      console.error('Login Error:', error);
      setLoginError('Invalid credentials. Please try again.');
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
        padding: '20px',
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '400px',
          padding: '30px',
          borderRadius: '20px',
          background: 'rgba(255, 255, 255, 0.1)',
          backdropFilter: 'blur(16px)',
          WebkitBackdropFilter: 'blur(16px)',
          border: '1px solid rgba(255, 255, 255, 0.3)',
          color: '#fff',
          boxShadow: '0 8px 32px 0 rgba(0, 0, 0, 0.37)',
        }}
      >
        <h3 className="text-center mb-4" style={{ fontWeight: '600' }}>
          Login
        </h3>

        {loginError && <Alert variant="danger">{loginError}</Alert>}

        <Form onSubmit={handleSubmit} noValidate>
          {/* Email Field */}
          <Form.Group controlId="email" className="mb-3">
            <Form.Label style={{ color: '#fff' }}>Email address</Form.Label>
            <Form.Control
              type="email"
              value={email}
              onChange={handleEmailChange}
              isInvalid={!!emailError}
              placeholder="Enter your email"
              required
              style={{
                background: 'rgba(255, 255, 255, 0.2)',
                border: '1px solid rgba(255, 255, 255, 0.3)',
                color: '#fff',
                borderRadius: '10px',
                padding: '12px',
              }}
            />
            <Form.Control.Feedback type="invalid">{emailError}</Form.Control.Feedback>
          </Form.Group>

          {/* Password Field */}
          <Form.Group controlId="password" className="mb-4">
            <Form.Label style={{ color: '#fff' }}>Password</Form.Label>
            <InputGroup>
              <Form.Control
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={handlePasswordChange}
                isInvalid={!!passwordError}
                placeholder="Enter your password"
                required
                style={{
                  background: 'rgba(255, 255, 255, 0.2)',
                  border: '1px solid rgba(255, 255, 255, 0.3)',
                  color: '#fff',
                  borderRadius: '10px 0 0 10px',
                  padding: '12px',
                }}
              />
              <Button
                variant="light"
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  borderRadius: '0 10px 10px 0',
                  background: 'rgba(255, 255, 255, 0.2)',
                  border: '1px solid rgba(255, 255, 255, 0.3)',
                  color: '#000',
                }}
              >
                {showPassword ? <FaEyeSlash /> : <FaEye />}
              </Button>
            </InputGroup>
            <Form.Control.Feedback type="invalid">{passwordError}</Form.Control.Feedback>
          </Form.Group>

          {/* Submit Button */}
          <Button
            variant="light"
            type="submit"
            className="w-100 fw-bold mb-3"
            style={{
              borderRadius: '10px',
              padding: '12px',
              fontWeight: '600',
              color: '#000',
            }}
          >
            Log In
          </Button>

          <div className="text-center mb-2">
            <a href="/forgot-password" style={{ color: '#ddd' }}>
              Forgot password?
            </a>
          </div>

          <div className="text-center" style={{ color: '#ddd' }}>
            New user?{' '}
            <a href="/register" style={{ color: '#fff', fontWeight: '500' }}>
              Register here
            </a>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default LoginPage;

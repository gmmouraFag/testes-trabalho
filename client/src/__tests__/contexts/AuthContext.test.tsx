import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { AuthProvider, useAuth } from '@/contexts/AuthContext';

// Test component that uses the hook
const TestComponent = () => {
  const { isAuthenticated, user, login, logout } = useAuth();

  return (
    <div>
      <div data-testid="auth-status">
        {isAuthenticated ? `Logged in as ${user?.email}` : 'Not logged in'}
      </div>
      <button
        data-testid="login-btn"
        onClick={() => login('test@example.com', 'TestPass123!')}
      >
        Login
      </button>
      <button data-testid="logout-btn" onClick={logout}>
        Logout
      </button>
    </div>
  );
};

describe('AuthContext', () => {
  describe('AuthProvider & useAuth Hook', () => {
    it('✓ should provide initial auth state', () => {
      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      expect(screen.getByTestId('auth-status')).toHaveTextContent('Not logged in');
    });

    it('✓ should throw error when useAuth is used outside provider', () => {
      const ComponentWithoutProvider = () => {
        const { isAuthenticated } = useAuth();
        return <div>{isAuthenticated.toString()}</div>;
      };

      // Suppress console.error for this test
      const spy = jest.spyOn(console, 'error').mockImplementation(() => {});

      expect(() => {
        render(<ComponentWithoutProvider />);
      }).toThrow('useAuth must be used within AuthProvider');

      spy.mockRestore();
    });

    it('✓ should update auth state after login', async () => {
      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      const loginBtn = screen.getByTestId('login-btn');
      fireEvent.click(loginBtn);

      await waitFor(() => {
        expect(screen.getByTestId('auth-status')).toHaveTextContent(
          'Logged in as test@example.com'
        );
      });
    });

    it('✓ should clear auth state after logout', async () => {
      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      const loginBtn = screen.getByTestId('login-btn');
      fireEvent.click(loginBtn);

      await waitFor(() => {
        expect(screen.getByTestId('auth-status')).toHaveTextContent('Logged in');
      });

      const logoutBtn = screen.getByTestId('logout-btn');
      fireEvent.click(logoutBtn);

      await waitFor(() => {
        expect(screen.getByTestId('auth-status')).toHaveTextContent('Not logged in');
      });
    });
  });

  describe('Login functionality', () => {
    it('✓ should set user data on successful login', async () => {
      const TestLoginComponent = () => {
        const { user, login, isAuthenticated } = useAuth();

        return (
          <div>
            <button onClick={() => login('user@test.com', 'Pass123!')}>
              Sign In
            </button>
            {isAuthenticated && (
              <div data-testid="user-email">{user?.email}</div>
            )}
          </div>
        );
      };

      render(
        <AuthProvider>
          <TestLoginComponent />
        </AuthProvider>
      );

      fireEvent.click(screen.getByText('Sign In'));

      await waitFor(() => {
        expect(screen.getByTestId('user-email')).toHaveTextContent(
          'user@test.com'
        );
      });
    });

    it('✓ should handle login with email and password', async () => {
      const TestLoginComponent = () => {
        const { isAuthenticated, login } = useAuth();

        return (
          <div>
            <button
              onClick={() =>
                login('newuser@example.com', 'SecurePassword123!')
              }
              data-testid="custom-login"
            >
              Login
            </button>
            <div data-testid="auth-check">
              {isAuthenticated ? 'Authenticated' : 'Not Authenticated'}
            </div>
          </div>
        );
      };

      render(
        <AuthProvider>
          <TestLoginComponent />
        </AuthProvider>
      );

      fireEvent.click(screen.getByTestId('custom-login'));

      await waitFor(() => {
        expect(screen.getByTestId('auth-check')).toHaveTextContent(
          'Authenticated'
        );
      });
    });
  });

  describe('Signup functionality', () => {
    it('✓ should set user data on successful signup', async () => {
      const TestSignupComponent = () => {
        const { user, signup, isAuthenticated } = useAuth();

        return (
          <div>
            <button
              onClick={() =>
                signup('newuser@example.com', 'NewPass123!')
              }
              data-testid="signup-btn"
            >
              Sign Up
            </button>
            {isAuthenticated && (
              <div data-testid="signup-user">{user?.email}</div>
            )}
          </div>
        );
      };

      render(
        <AuthProvider>
          <TestSignupComponent />
        </AuthProvider>
      );

      fireEvent.click(screen.getByTestId('signup-btn'));

      await waitFor(() => {
        expect(screen.getByTestId('signup-user')).toHaveTextContent(
          'newuser@example.com'
        );
      });
    });
  });

  describe('Session management', () => {
    it('✓ should maintain user session after login', async () => {
      const MultiRenderComponent = () => {
        const { user, isAuthenticated, login } = useAuth();

        return (
          <div>
            <button onClick={() => login('persistent@example.com', 'Pass123!')}>
              Login
            </button>
            {isAuthenticated && (
              <div data-testid="session-user">{user?.email}</div>
            )}
          </div>
        );
      };

      render(
        <AuthProvider>
          <MultiRenderComponent />
        </AuthProvider>
      );

      fireEvent.click(screen.getByText('Login'));

      await waitFor(() => {
        expect(screen.getByTestId('session-user')).toHaveTextContent(
          'persistent@example.com'
        );
      });

      // User should still be logged in
      expect(screen.getByTestId('session-user')).toBeInTheDocument();
    });
  });
});

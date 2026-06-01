import { authService } from '@/service/auth/auth';
import api from '@/service/api';

jest.mock('@/service/api');

describe('Auth Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('signIn', () => {
    it('✓ should call signIn endpoint with correct data', async () => {
      const mockResponse = {
        data: {
          id: 1,
          email: 'user@example.com',
          token: 'jwt-token'
        }
      };

      (api.post as jest.Mock).mockResolvedValue(mockResponse);

      const result = await authService.signIn({
        email: 'user@example.com',
        password: 'SecurePass123!'
      });

      expect(api.post).toHaveBeenCalledWith('/auth/signin', {
        email: 'user@example.com',
        password: 'SecurePass123!'
      });
      expect(result).toEqual(mockResponse.data);
    });

    it('✓ should handle signIn error', async () => {
      const error = new Error('Invalid credentials');
      (api.post as jest.Mock).mockRejectedValue(error);

      await expect(
        authService.signIn({
          email: 'wrong@example.com',
          password: 'WrongPass123!'
        })
      ).rejects.toThrow('Invalid credentials');
    });

    it('✓ should return correct response structure', async () => {
      const mockResponse = {
        data: {
          id: 1,
          email: 'test@example.com',
          token: 'token123'
        }
      };

      (api.post as jest.Mock).mockResolvedValue(mockResponse);

      const result = await authService.signIn({
        email: 'test@example.com',
        password: 'TestPass123!'
      });

      expect(result).toHaveProperty('id');
      expect(result).toHaveProperty('email');
      expect(result).toHaveProperty('token');
    });
  });

  describe('signUp', () => {
    it('✓ should call signUp endpoint with correct data', async () => {
      const mockResponse = {
        data: {
          id: 1,
          email: 'newuser@example.com',
          token: 'jwt-token'
        }
      };

      (api.post as jest.Mock).mockResolvedValue(mockResponse);

      const result = await authService.signUp({
        email: 'newuser@example.com',
        password: 'SecurePass123!',
        confirmPassword: 'SecurePass123!'
      });

      expect(api.post).toHaveBeenCalledWith('/auth/signup', {
        email: 'newuser@example.com',
        password: 'SecurePass123!',
        confirmPassword: 'SecurePass123!'
      });
      expect(result).toEqual(mockResponse.data);
    });

    it('✓ should handle signUp error (email already exists)', async () => {
      const error = new Error('Email already exists');
      (api.post as jest.Mock).mockRejectedValue(error);

      await expect(
        authService.signUp({
          email: 'existing@example.com',
          password: 'SecurePass123!',
          confirmPassword: 'SecurePass123!'
        })
      ).rejects.toThrow('Email already exists');
    });

    it('✓ should return correct response structure', async () => {
      const mockResponse = {
        data: {
          id: 2,
          email: 'another@example.com',
          token: 'token456'
        }
      };

      (api.post as jest.Mock).mockResolvedValue(mockResponse);

      const result = await authService.signUp({
        email: 'another@example.com',
        password: 'AnotherPass123!',
        confirmPassword: 'AnotherPass123!'
      });

      expect(result).toHaveProperty('id');
      expect(result).toHaveProperty('email');
      expect(result).toHaveProperty('token');
      expect(result.id).toBe(2);
    });
  });

  describe('resetPassword', () => {
    it('✓ should call resetPassword endpoint', async () => {
      (api.post as jest.Mock).mockResolvedValue({ data: {} });

      await authService.resetPassword({
        email: 'user@example.com'
      });

      expect(api.post).toHaveBeenCalledWith('/auth/reset-password', {
        email: 'user@example.com'
      });
    });

    it('✓ should handle resetPassword error', async () => {
      const error = new Error('Email not found');
      (api.post as jest.Mock).mockRejectedValue(error);

      await expect(
        authService.resetPassword({
          email: 'nonexistent@example.com'
        })
      ).rejects.toThrow('Email not found');
    });

    it('✓ should handle network errors', async () => {
      const error = new Error('Network error');
      (api.post as jest.Mock).mockRejectedValue(error);

      await expect(
        authService.resetPassword({
          email: 'user@example.com'
        })
      ).rejects.toThrow('Network error');
    });
  });

  describe('Integration scenarios', () => {
    it('✓ should handle successful sign-up and sign-in flow', async () => {
      const signUpResponse = {
        data: {
          id: 1,
          email: 'newuser@example.com',
          token: 'signup-token'
        }
      };

      const signInResponse = {
        data: {
          id: 1,
          email: 'newuser@example.com',
          token: 'signin-token'
        }
      };

      (api.post as jest.Mock)
        .mockResolvedValueOnce(signUpResponse)
        .mockResolvedValueOnce(signInResponse);

      const signUpResult = await authService.signUp({
        email: 'newuser@example.com',
        password: 'SecurePass123!',
        confirmPassword: 'SecurePass123!'
      });

      const signInResult = await authService.signIn({
        email: 'newuser@example.com',
        password: 'SecurePass123!'
      });

      expect(signUpResult.id).toBe(signInResult.id);
      expect(signUpResult.email).toBe(signInResult.email);
    });
  });
});

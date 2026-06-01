import { isEmailValid, getEmailValidationMessage } from '@/utils/email';

describe('Email Validation Utils', () => {
  describe('isEmailValid', () => {
    it('✓ should validate correct email', () => {
      expect(isEmailValid('user@example.com')).toBe(true);
    });

    it('✓ should validate email with multiple domains', () => {
      expect(isEmailValid('user@mail.co.uk')).toBe(true);
    });

    it('✓ should validate email with numbers', () => {
      expect(isEmailValid('user123@example.com')).toBe(true);
    });

    it('✗ should reject email without @ symbol', () => {
      expect(isEmailValid('userexample.com')).toBe(false);
    });

    it('✗ should reject email without domain extension', () => {
      expect(isEmailValid('user@example')).toBe(false);
    });

    it('✗ BUG: should reject email with only @ (no domain)', () => {
      const result = isEmailValid('user@');
      // Frontend regex should catch this - backend doesn't validate properly!
      expect(result).toBe(false);
    });

    it('✗ should reject null email', () => {
      expect(isEmailValid('')).toBe(false);
    });

    it('✗ should reject email with spaces', () => {
      expect(isEmailValid('user @example.com')).toBe(false);
    });

    it('✗ should reject email with spaces in domain', () => {
      expect(isEmailValid('user@exam ple.com')).toBe(false);
    });

    it('✗ BUG: Email validation inconsistency with backend', () => {
      // Frontend validates properly, but backend uses weak validation
      // Backend only checks for "@" symbol
      const emailWithoutDomain = 'user@';
      const result = isEmailValid(emailWithoutDomain);
      expect(result).toBe(false); // Frontend correct
      // Backend would accept this due to containing "@"
    });
  });

  describe('getEmailValidationMessage', () => {
    it('✓ should return empty string for valid email', () => {
      expect(getEmailValidationMessage('user@example.com')).toBe('');
    });

    it('✓ should return required message for empty email', () => {
      const message = getEmailValidationMessage('');
      expect(message).toContain('obrigatório');
    });

    it('✓ should return invalid message for invalid email', () => {
      const message = getEmailValidationMessage('invalid-email');
      expect(message).toContain('inválido');
    });

    it('✓ should handle trimmed spaces', () => {
      expect(isEmailValid('  user@example.com  ')).toBe(true);
    });
  });
});

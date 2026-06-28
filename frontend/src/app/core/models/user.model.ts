export interface User {
  id: number;
  email: string;
  name: string;
  surname: string;
  phone?: string;
  roles: string[];
  approved: boolean;
  enabled: boolean;
  createdAt: string;
}

export interface UserRegistration {
  email: string;
  password: string;
  name: string;
  surname: string;
  phone?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  status: 'SUCCESS' | 'FAILURE';
  message: string;
  error?: string;
}

export interface Provider {
  id: number;
  name: string;
  address?: string;
  phoneNumber?: string;
  website?: string;
  contactPerson?: string;
  email?: string;
  types: string[];
}

export interface ProviderDTO {
  name: string;
  address?: string;
  phoneNumber?: string;
  website?: string;
  contactPerson?: string;
  email?: string;
  types: string[];
}

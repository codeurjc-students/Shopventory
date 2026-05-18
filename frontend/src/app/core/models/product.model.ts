export interface Product {
  id: number;
  name: string;
  sku?: string;
  description?: string;
  descriptionShort?: string;
  price: number;
  stock: number;
  minStockThreshold: number;
  categories: string[];
  lowStock: boolean;
  createdAt: string;
}

export interface ProductDTO {
  name: string;
  sku?: string;
  description?: string;
  descriptionShort?: string;
  price: number;
  stock: number;
  minStockThreshold: number;
  categories: string[];
  providerId?: number;
}

export interface StockUpdate {
  quantity: number;
  reason?: string;
}

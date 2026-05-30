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
  providers?: { id: number; name: string }[];
  lowStock: boolean;
  hasImage: boolean;
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
  providerIds?: number[];
}

export interface StockUpdate {
  quantity: number;
  reason?: string;
}

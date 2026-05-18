export type OrderType = 'SALE' | 'PURCHASE';
export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'DELIVERED' | 'CANCELLED';

export interface OrderItem {
  id: number;
  product: { id: number; name: string; sku?: string };
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface Order {
  id: number;
  type: OrderType;
  status: OrderStatus;
  orderDate: string;
  deliveryDate?: string;
  notes?: string;
  discount: number;
  totalAmount: number;
  customerName?: string;
  customerEmail?: string;
  items: OrderItem[];
}

export interface OrderDTO {
  type: OrderType;
  deliveryDate?: string;
  notes?: string;
  discount?: number;
  providerId?: number;
  customerName?: string;
  customerEmail?: string;
  items: { productId: number; quantity: number }[];
}

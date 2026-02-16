export interface AuthResponse {
  token: string;
  username: string;
  fullName: string;
  roles: string[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  fullName: string;
  email?: string;
  dcId?: number;
  role?: string;
}

export interface DC {
  id: number;
  code: string;
  name: string;
  address?: string;
  city?: string;
  state?: string;
  zip?: string;
  active: boolean;
}

export interface Sku {
  id: number;
  code: string;
  description: string;
  uom: string;
  weight?: number;
  length?: number;
  width?: number;
  height?: number;
  active: boolean;
}

export interface Vendor {
  id: number;
  code: string;
  name: string;
  contactName?: string;
  email?: string;
  phone?: string;
  address?: string;
  active: boolean;
}

export interface Carrier {
  id: number;
  code: string;
  name: string;
  scac?: string;
  contactName?: string;
  phone?: string;
  active: boolean;
}

export interface Location {
  id: number;
  dcId: number;
  dcCode?: string;
  code: string;
  zone: string;
  aisle?: string;
  bay?: string;
  level?: string;
  position?: string;
  locType: string;
  maxQty: number;
  pickSequence: number;
  active: boolean;
}

export interface PurchaseOrder {
  id: number;
  poNumber: string;
  vendorId: number;
  vendorName?: string;
  dcId: number;
  status: string;
  expectedDate?: string;
  notes?: string;
  lines: PurchaseOrderLine[];
}

export interface PurchaseOrderLine {
  id?: number;
  skuId: number;
  skuCode?: string;
  expectedQty: number;
  receivedQty: number;
  lineNumber: number;
  status: string;
}

export interface Asn {
  id: number;
  asnNumber: string;
  poId: number;
  poNumber?: string;
  carrierId?: number;
  carrierName?: string;
  status: string;
  expectedArrival?: string;
  arrivedAt?: string;
  trailerNumber?: string;
  doorNumber?: string;
  notes?: string;
  lines: AsnLine[];
}

export interface AsnLine {
  id?: number;
  poLineId: number;
  skuId: number;
  skuCode?: string;
  expectedQty: number;
  receivedQty: number;
  lotNumber?: string;
  status: string;
}

export interface ReceiveRequest {
  asnLineId: number;
  qty: number;
  locationId: number;
  lotNumber?: string;
}

export interface Inventory {
  id: number;
  skuId: number;
  skuCode?: string;
  skuDescription?: string;
  locationId: number;
  locationCode?: string;
  dcId: number;
  status: string;
  lotNumber?: string;
  qtyOnHand: number;
  qtyAllocated: number;
}

export interface CycleCount {
  id: number;
  dcId: number;
  locationId: number;
  locationCode?: string;
  skuId?: number;
  skuCode?: string;
  status: string;
  systemQty?: number;
  countedQty?: number;
  variance?: number;
  countedAt?: string;
  notes?: string;
}

export interface WmsOrder {
  id: number;
  orderNumber: string;
  dcId: number;
  status: string;
  priority: number;
  customerName?: string;
  shipToAddress?: string;
  shipToCity?: string;
  shipToState?: string;
  shipToZip?: string;
  carrierId?: number;
  carrierName?: string;
  waveId?: number;
  waveNumber?: string;
  requiredDate?: string;
  notes?: string;
  lines: OrderLine[];
}

export interface OrderLine {
  id?: number;
  skuId: number;
  skuCode?: string;
  lineNumber: number;
  orderedQty: number;
  allocatedQty: number;
  pickedQty: number;
  status: string;
}

export interface Wave {
  id: number;
  waveNumber: string;
  dcId: number;
  status: string;
  releasedAt?: string;
  completedAt?: string;
  orderCount?: number;
  orderIds?: number[];
}

export interface PickTask {
  id: number;
  waveId: number;
  waveNumber?: string;
  orderId: number;
  orderNumber?: string;
  orderLineId: number;
  skuId: number;
  skuCode?: string;
  skuDescription?: string;
  fromLocationId: number;
  fromLocationCode?: string;
  dcId: number;
  qtyToPick: number;
  qtyPicked: number;
  status: string;
  assignedTo?: number;
  assignedToName?: string;
  pickSequence: number;
}

export interface Shipment {
  id: number;
  shipmentNumber: string;
  dcId: number;
  carrierId?: number;
  carrierName?: string;
  status: string;
  bolNumber?: string;
  trailerNumber?: string;
  doorNumber?: string;
  shippedAt?: string;
  notes?: string;
  lines: ShipmentLine[];
}

export interface ShipmentLine {
  id?: number;
  orderId: number;
  orderNumber?: string;
  status: string;
}

export interface DashboardKpis {
  totalInventory: number;
  openOrders: number;
  pendingPicks: number;
  shipmentsToday: number;
  inboundToday: number;
  pickCompletionRate: number;
}

export interface User {
  id: number;
  username: string;
  fullName: string;
  email?: string;
  dcId?: number;
  dcCode?: string;
  roles: string[];
  active: boolean;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

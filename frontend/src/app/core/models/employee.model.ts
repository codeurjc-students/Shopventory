export interface Employee {
  id: number;
  name: string;
  surname: string;
  email: string;
  phone?: string;
  position: string;
  hireDate: string;
}

export interface EmployeeDTO {
  name: string;
  surname: string;
  email: string;
  phone?: string;
  position: string;
  hireDate: string;
}
